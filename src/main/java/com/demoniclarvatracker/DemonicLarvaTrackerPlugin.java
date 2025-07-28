/*
 * Copyright (c) 2025, marknewan <http://github.com/marknewan>
 * Copyright (c) 2023, Buchus <http://github.com/MoreBuchus>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.demoniclarvatracker;

import com.demoniclarvatracker.attackstyles.AttackStyle;
import com.demoniclarvatracker.attackstyles.WeaponType;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

@Singleton
@PluginDescriptor(
	name = "Demonic Larva Tracker",
	description = "Tracks demonic larva at Doom of Mokhaiotl.",
	tags = {"doom", "mokhaiotl", "demonic", "larva", "grub", "tracker"}
)
public class DemonicLarvaTrackerPlugin extends Plugin
{
	private static final Set<Integer> REGION_IDS = Set.of(5269, 13668, 14180);

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Hooks hooks;
	@Inject
	private NpcUtil npcUtil;
	@Inject
	private DemonicLarvaTrackerConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private SceneOverlay sceneOverlay;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	private final int[] previousSkillXp = new int[Skill.values().length];

	private final Map<Skill, Integer> fakeXpDrops = new EnumMap<>(Skill.class);

	@Getter(AccessLevel.PACKAGE)
	private final Map<NPC, Larva> larvae = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<NPC> deadLarvae = new HashSet<>();

	@Nullable
	private NPC npc;
	@Nullable
	private AttackStyle attackStyle;

	private int attackStyleVarbit = -1;
	private int weaponTypeVarbit = -1;
	private int castModeVarbit = -1;

	private long lastTickNano;
	private int lastTickMillis;

	private boolean damageProcessed;

	private boolean enabled;

	@Override
	protected void startUp()
	{
		clientThread.invokeLater(() -> {
			if (client.getGameState() == GameState.LOGGED_IN && inRegion())
			{
				init();
			}
		});
	}

	private void init()
	{
		assert client.isClientThread();

		enabled = true;

		hooks.registerRenderableDrawListener(drawListener);
		overlayManager.add(sceneOverlay);

		initAttackStyles();
		System.arraycopy(client.getSkillExperiences(), 0, previousSkillXp, 0, previousSkillXp.length);
	}

	@Override
	protected void shutDown()
	{
		enabled = false;

		hooks.unregisterRenderableDrawListener(drawListener);

		Arrays.fill(previousSkillXp, 0);

		fakeXpDrops.clear();
		larvae.clear();
		deadLarvae.clear();

		npc = null;
		attackStyle = null;

		attackStyleVarbit = -1;
		weaponTypeVarbit = -1;
		castModeVarbit = -1;

		lastTickNano = 0;
		lastTickMillis = 0;

		damageProcessed = false;
	}

	@Provides
	DemonicLarvaTrackerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(DemonicLarvaTrackerConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
				if (inRegion())
				{
					if (!enabled)
					{
						init();
					}
				}
				else
				{
					if (enabled)
					{
						shutDown();
					}
				}
				break;
			case HOPPING:
			case LOGIN_SCREEN:
				if (enabled)
				{
					shutDown();
				}
				break;
		}
	}

	@Subscribe
	public void onNpcSpawned(final NpcSpawned event)
	{
		if (!enabled)
		{
			return;
		}

		final var npc = event.getNpc();

		if (isLarva(npc))
		{
			larvae.put(npc, new Larva(npc));
			if (config.removeSpawnAnimation())
			{
				npc.clearSpotAnims();
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(final NpcDespawned event)
	{
		if (!enabled)
		{
			return;
		}

		final var npc = event.getNpc();
		final int id = npc.getId();

		if (isLarva(npc))
		{
			larvae.remove(npc);
			deadLarvae.remove(npc);
		}
		else if (id >= NpcID.DOM_BOSS && id <= NpcID.DOM_BOSS_BURROWED)
		{
			larvae.clear();
			deadLarvae.clear();
		}
	}

	@Subscribe
	public void onGraphicChanged(final GraphicChanged event)
	{
		if (!enabled || !config.removeSpawnAnimation())
		{
			return;
		}

		final var actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		final var npc = (NPC) actor;
		if (!isLarva(npc))
		{
			return;
		}

		npc.clearSpotAnims();
	}

	@Subscribe
	public void onActorDeath(final ActorDeath event)
	{
		if (!enabled)
		{
			return;
		}

		final var actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		final var npc = (NPC) actor;
		if (isLarva(npc))
		{
			larvae.remove(npc);
			deadLarvae.remove(npc);
		}
	}

	@Subscribe
	private void onInteractingChanged(final InteractingChanged event)
	{
		if (!enabled || event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		npc = null;

		final var actor = event.getTarget();
		if (!(actor instanceof NPC))
		{
			return;
		}

		final var npc = (NPC) actor;
		if (!isLarva(npc))
		{
			return;
		}

		this.npc = npc;
	}

	@Subscribe
	public void onFakeXpDrop(final FakeXpDrop event)
	{
		if (!enabled)
		{
			return;
		}

		final var skill = event.getSkill();

		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case RANGED:
			case HITPOINTS:
				fakeXpDrops.merge(skill, event.getXp(), Integer::sum);
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onStatChanged(final StatChanged event)
	{
		if (!enabled)
		{
			return;
		}

		final int idx = event.getSkill().ordinal();
		final int previousXp = previousSkillXp[idx];
		final int currentXp = event.getXp();
		previousSkillXp[idx] = currentXp;

		if (previousXp <= 0)
		{
			return;
		}

		if (npc == null)
		{
			final var actor = client.getLocalPlayer().getInteracting();
			if (!(actor instanceof NPC))
			{
				return;
			}
			final var npc = (NPC) actor;
			if (!isLarva(npc))
			{
				return;
			}
			this.npc = npc;
		}

		final int xp = currentXp - previousXp;
		if (xp <= 0)
		{
			return;
		}

		processXp(event.getSkill(), xp);
	}

	@Subscribe
	private void onHitsplatApplied(final HitsplatApplied event)
	{
		if (!enabled)
		{
			return;
		}

		final int damage = event.getHitsplat().getAmount();
		if (damage == 0)
		{
			return;
		}

		final var actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		final var npc = (NPC) actor;

		final var larva = larvae.get(npc);
		if (larva == null)
		{
			return;
		}

		larva.onHitsplat(damage);

		if (larva.isDead())
		{
			larva.setDeathTick(client.getTickCount());
			deadLarvae.add(npc);
		}
		else if (larva.willDie())
		{
			larva.kill(client.getTickCount());
			deadLarvae.add(npc);
		}
		else
		{
			larva.setDeathTick(0);
			deadLarvae.remove(npc);
		}
	}

	@Subscribe
	public void onMenuEntryAdded(final MenuEntryAdded event)
	{
		if (!enabled || !config.recolorLarvaMenuEntries())
		{
			return;
		}

		final var entry = event.getMenuEntry();

		final var npc = entry.getNpc();
		if (npc == null)
		{
			return;
		}

		final Color color;

		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY:
				color = config.colorMenuBase();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				color = config.colorMenuRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				color = config.colorMenuMagic();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				color = config.colorMenuMelee();
				break;
			default:
				return;
		}

		entry.setTarget(ColorUtil.colorTag(color) + npc.getName());
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
		if (!enabled)
		{
			return;
		}

		switch (event.getMenuAction())
		{
			case WIDGET_TARGET_ON_NPC:
			case NPC_FIRST_OPTION:
			case NPC_SECOND_OPTION:
			case NPC_THIRD_OPTION:
			case NPC_FOURTH_OPTION:
			case NPC_FIFTH_OPTION:
				final var npc = event.getMenuEntry().getNpc();
				this.npc = isLarva(npc) ? npc : null;
				initAttackStyles();
				break;
			case WALK:
			case WIDGET_TARGET_ON_WIDGET:
			case WIDGET_TARGET_ON_GROUND_ITEM:
			case WIDGET_TARGET_ON_PLAYER:
			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
				this.npc = null;
				break;
			default:
				if (event.isItemOp())
				{
					this.npc = null;
				}
				break;
		}
	}

	@Subscribe
	private void onVarbitChanged(final VarbitChanged event)
	{
		if (!enabled)
		{
			return;
		}

		final int id = event.getVarbitId();

		switch (id)
		{
			case VarPlayerID.COM_MODE:
				attackStyleVarbit = event.getValue();
				weaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
				castModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
				break;
			case VarbitID.COMBAT_WEAPON_CATEGORY:
				weaponTypeVarbit = event.getValue();
				attackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
				castModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
				break;
			case VarbitID.AUTOCAST_DEFMODE:
				castModeVarbit = event.getValue();
				attackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
				weaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
				break;
			default:
				return;
		}

		updateAttackStyle(attackStyleVarbit, weaponTypeVarbit, castModeVarbit);
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (!enabled)
		{
			return;
		}

		damageProcessed = false;
		processLag();
		processFakeXpDrops();
		processLarvas();
	}

	private boolean inRegion()
	{
		final var wv = client.getTopLevelWorldView();
		return wv.isInstance() && Arrays.stream(wv.getMapRegions()).anyMatch(REGION_IDS::contains);
	}

	private boolean shouldDraw(final Renderable renderable, final boolean overheads)
	{
		if (renderable instanceof NPC)
		{
			final var npc = (NPC) renderable;
			if (isLarva(npc))
			{
				if (config.hideDeadLarva() && deadLarvae.contains(npc))
				{
					return false;
				}

				switch (npc.getId())
				{
					case NpcID.DOM_DEMONIC_ENERGY_RANGE:
					case NpcID.DOM_DEMONIC_ENERGY_MAGE:
					case NpcID.DOM_DEMONIC_ENERGY_MELEE:
						return !(overheads && config.hideLarvaOverheads());
					default:
						break;
				}
			}
		}

		return true;
	}

	private void processLag()
	{
		final long time = System.nanoTime();
		lastTickMillis = (int) ((time - lastTickNano) / 1_000_000L);
		lastTickNano = time;

		if (lastTickMillis < config.lagProtectionThreshold())
		{
			return;
		}

		if (!larvae.isEmpty())
		{
			larvae.values().forEach(Larva::revive);
		}

		if (!deadLarvae.isEmpty())
		{
			deadLarvae.clear();
		}
	}

	private void processFakeXpDrops()
	{
		fakeXpDrops.forEach(this::processXp);
		fakeXpDrops.clear();
	}

	private void processLarvas()
	{
		if (larvae.isEmpty())
		{
			return;
		}

		for (final var entry : larvae.entrySet())
		{
			final var npc = entry.getKey();
			final var larva = entry.getValue();

			final boolean notActuallyDead = larva.isDead() && larva.isExpired(client.getTickCount()) && !npcUtil.isDying(npc);

			if (notActuallyDead)
			{
				larva.revive();
				deadLarvae.remove(npc);
				continue;
			}

			final boolean dying = larva.getHp() > 0 && npcUtil.isDying(npc);
			if (dying)
			{
				larva.kill(client.getTickCount());
				deadLarvae.add(npc);
			}
		}
	}

	private void processXp(Skill skill, final int xp)
	{
		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case RANGED:
				if (attackStyle == AttackStyle.LONGRANGE)
				{
					skill = Skill.RANGED;
				}
				break;
			case HITPOINTS:
				if (attackStyle != AttackStyle.CASTING)
				{
					return;
				}
				break;
			default:
				return;
		}

		final int damage = calcDamage(skill, attackStyle, xp);
		processDamage(damage, skill);
	}

	private void processDamage(final int damage, final Skill skill)
	{
		if (damage <= 0 || skill == null || damageProcessed || npc == null)
		{
			return;
		}

		damageProcessed = true;

		clientThread.invokeLater(() -> {
			final var larva = larvae.get(npc);
			if (larva == null)
			{
				return;
			}

			larva.queueDamage(damage);

			if (larva.willDie())
			{
				larva.kill(client.getTickCount());
				deadLarvae.add(npc);
			}
			else
			{
				larva.setDeathTick(0);
				deadLarvae.remove(npc);
			}
		});
	}

	private void initAttackStyles()
	{
		attackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
		weaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
		castModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);
		updateAttackStyle(attackStyleVarbit, weaponTypeVarbit, castModeVarbit);
	}

	private void updateAttackStyle(int attackStyleVarbit, final int weaponTypeVarbit, final int castModeVarbit)
	{
		if (attackStyleVarbit == 4)
		{
			attackStyleVarbit += castModeVarbit;
		}

		final var attackStyles = WeaponType.byTypeId(weaponTypeVarbit).getAttackStyles();
		if (attackStyleVarbit >= attackStyles.length)
		{
			return;
		}

		attackStyle = attackStyles[attackStyleVarbit];
		if (attackStyle == null)
		{
			attackStyle = AttackStyle.OTHER;
		}
	}

	private static int calcDamage(final Skill skill, final AttackStyle attackStyle, final int xp)
	{
		double damage = 0;

		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
				switch (attackStyle)
				{
					case ACCURATE:
					case AGGRESSIVE:
					case DEFENSIVE:
						damage = xp / 4.0D;
						break;
					case CONTROLLED:
						damage = xp / 1.33D;
						break;
					case DEFENSIVE_CASTING:
						damage = xp;
						break;
					default:
						break;
				}
				break;
			case HITPOINTS:
				if (attackStyle == AttackStyle.CASTING)
				{
					damage = xp / 1.33D;
					break;
				}
				break;
			case RANGED:
				switch (attackStyle)
				{
					case RANGING:
						damage = xp / 4.0D;
						break;
					case LONGRANGE:
						damage = xp / 2.0D;
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}

		return (int) Math.round(damage);
	}

	private static boolean isLarva(final NPC npc)
	{
		if (npc == null)
		{
			return false;
		}

		final int id = npc.getId();

		return id == NpcID.DOM_DEMONIC_ENERGY || id == NpcID.DOM_DEMONIC_ENERGY_RANGE ||
			id == NpcID.DOM_DEMONIC_ENERGY_MAGE || id == NpcID.DOM_DEMONIC_ENERGY_MELEE;
	}

	@Data
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	private static class Larva
	{
		private static final int MAX_HP = 2;
		private static final int DEATH_TICK_THRESHOLD = 2;

		@EqualsAndHashCode.Include
		private final NPC npc;

		private int deathTick;

		private int hp = MAX_HP;
		private int queuedDamage;

		private boolean dead;

		private Larva(final @NonNull NPC npc)
		{
			this.npc = npc;
		}

		private void onHitsplat(final int amount)
		{
			hp = Math.max(0, hp - amount);
			queuedDamage = Math.max(0, queuedDamage - amount);
		}

		private void queueDamage(final int amount)
		{
			queuedDamage += amount;
		}

		private boolean willDie()
		{
			return hp == 0 || queuedDamage >= hp;
		}

		private void kill(final int tick)
		{
			hp = 0;
			deathTick = tick;
			dead = true;
		}

		private void revive()
		{
			hp = MAX_HP;
			deathTick = 0;
			dead = false;
		}

		private boolean isExpired(final int tick)
		{
			return deathTick != 0 && tick > (deathTick + DEATH_TICK_THRESHOLD);
		}

		@Override
		public String toString()
		{
			return String.format("[name=%s hp=%d deathTick=%d queuedDamage=%d isDead=%s]",
				npc.getName(), hp, deathTick, queuedDamage, dead);
		}
	}
}
