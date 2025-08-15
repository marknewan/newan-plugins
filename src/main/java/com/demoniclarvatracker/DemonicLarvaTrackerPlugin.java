/*
 * Copyright (c) 2025, marknewan <http://github.com/marknewan>
 * Copyright (c) 2023, Buchus <http://github.com/MoreBuchus>
 * Copyright (c) 2017, honeyhoney <https://github.com/honeyhoney>
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

import com.google.inject.Provides;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EnumID;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.ParamID;
import net.runelite.api.Renderable;
import net.runelite.api.Skill;
import net.runelite.api.StructComposition;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

@Slf4j
@Singleton
@PluginDescriptor(
	name = "Demonic Larva Tracker",
	description = "Tracks demonic larva at Doom of Mokhaiotl.",
	tags = {"doom", "mokhaiotl", "demonic", "larva", "grub", "tracker", "delve"}
)
public class DemonicLarvaTrackerPlugin extends Plugin
{
	private static final Set<Integer> REGION_IDS = Set.of(5269, 13668, 14180);
	private static final AttackStyle[] ATTACK_STYLES_POWERED_STAVE = new AttackStyle[]{
		AttackStyle.CASTING, AttackStyle.CASTING, null, AttackStyle.DEFENSIVE, null, null
	};

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Hooks hooks;
	@Inject
	private DemonicLarvaTrackerConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private SceneOverlay sceneOverlay;
	@Inject
	private WidgetOverlay widgetOverlay;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private SpriteManager spriteManager;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Getter(AccessLevel.PACKAGE)
	private final Map<NPC, Larva> larvae = new HashMap<>();

	private final Map<Larva, Hitsplat> larvaHitsplats = new HashMap<>();
	private final Map<Skill, Integer> realXpDrops = new EnumMap<>(Skill.class);
	private final Map<Skill, Integer> fakeXpDrops = new EnumMap<>(Skill.class);

	private final int[] previousSkillXp = new int[Skill.values().length];

	@Nullable
	private NPC interactingNpc;
	@Nullable
	private AttackStyle attackStyle;
	@Nullable
	private Counter counter;

	private long lastTickNano;

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
		overlayManager.add(widgetOverlay);

		setCounter();

		initAttackStyles();
		System.arraycopy(client.getSkillExperiences(), 0, previousSkillXp, 0, previousSkillXp.length);
	}

	@Override
	protected void shutDown()
	{
		enabled = false;

		hooks.unregisterRenderableDrawListener(drawListener);

		overlayManager.remove(sceneOverlay);
		overlayManager.remove(widgetOverlay);

		infoBoxManager.removeInfoBox(counter);

		larvae.clear();
		larvaHitsplats.clear();
		realXpDrops.clear();
		fakeXpDrops.clear();

		Arrays.fill(previousSkillXp, 0);

		interactingNpc = null;
		attackStyle = null;
		counter = null;

		lastTickNano = 0;
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
			default:
				break;
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
	public void onNpcSpawned(final NpcSpawned event)
	{
		if (!enabled)
		{
			return;
		}

		final var npc = event.getNpc();

		if (!isLarva(npc))
		{
			return;
		}

		larvae.put(npc, new Larva(npc));
		if (config.removeSpawnAnimation())
		{
			npc.clearSpotAnims();
			npc.setAnimation(AnimationID.NPC_DEMONIC_GRUB_WALK);
			npc.setAnimationFrame(0);
		}

		log.debug("{} - onNpcSpawned: {} ({})", client.getTickCount(), npc.getName(), npc.getIndex());
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

			log.debug("{} - onNpcDespawned: {} ({})", client.getTickCount(), npc.getName(), npc.getIndex());
		}
		else if (id >= NpcID.DOM_BOSS && id <= NpcID.DOM_BOSS_BURROWED)
		{
			larvae.clear();
		}
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

		final var larva = larvae.get(npc);
		if (larva == null)
		{
			return;
		}

		larva.kill(client.getTickCount());

		log.debug("{} - onActorDeath: {} ({})", client.getTickCount(), npc.getName(), npc.getIndex());
	}

	@Subscribe()
	private void onInteractingChanged(final InteractingChanged event)
	{
		if (!enabled || event.getSource() != client.getLocalPlayer())
		{
			return;
		}

		interactingNpc = null;

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

		this.interactingNpc = npc;

		log.debug("{} - onInteractingChanged: {} ({})", client.getTickCount(), npc.getName(), npc.getIndex());
	}

	@Subscribe
	public void onFakeXpDrop(final FakeXpDrop event)
	{
		if (!enabled)
		{
			return;
		}

		final var skill = event.getSkill();
		final var xp = event.getXp();

		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case RANGED:
			case HITPOINTS:
				fakeXpDrops.merge(skill, xp, Integer::sum);

				log.debug("{} - onFakeXpDrop: {} {}", client.getTickCount(), skill, xp);
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

		final var skill = event.getSkill();
		final var xp = event.getXp();

		final int idx = skill.ordinal();
		final int prevXp = previousSkillXp[idx];
		previousSkillXp[idx] = xp;
		if (prevXp == 0)
		{
			return;
		}

		final int xpDiff = xp - prevXp;
		if (xpDiff <= 0)
		{
			return;
		}

		switch (skill)
		{
			case ATTACK:
			case STRENGTH:
			case DEFENCE:
			case RANGED:
			case HITPOINTS:
				realXpDrops.merge(skill, xpDiff, Integer::sum);

				log.debug("{} - onStatChanged: skill={} xp={}", client.getTickCount(), skill, xpDiff);
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onHitsplatApplied(final HitsplatApplied event)
	{
		if (!enabled)
		{
			return;
		}

		final var hitsplat = event.getHitsplat();
		final int damage = hitsplat.getAmount();
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

		larvaHitsplats.put(larva, hitsplat);

		log.debug("{} - onHitsplatApplied: {} ({}) damage={}", client.getTickCount(), npc.getName(), npc.getIndex(), damage);
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
			case NpcID.DOM_DEMONIC_ENERGY_GIANT_RANGE:
				color = config.colorMenuRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
			case NpcID.DOM_DEMONIC_ENERGY_GIANT_MAGE:
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
	private void onVarbitChanged(final VarbitChanged event)
	{
		if (!enabled)
		{
			return;
		}

		if (event.getVarpId() == VarPlayerID.COM_MODE ||
			event.getVarbitId() == VarbitID.COMBAT_WEAPON_CATEGORY ||
			event.getVarbitId() == VarbitID.AUTOCAST_DEFMODE)
		{
			initAttackStyles();
		}
		else if (event.getVarbitId() == VarbitID.DOM_MISSED_ORBS)
		{
			if (counter != null)
			{
				counter.setCount(event.getValue());
			}
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		if (!enabled || processLag())
		{
			return;
		}

		larvae.values().forEach(larva -> larva.setXpProcessed(false));

		processHitSplats();
		larvaHitsplats.clear();

		processXpDrops(realXpDrops);
		realXpDrops.clear();

		processXpDrops(fakeXpDrops);
		fakeXpDrops.clear();

		reviveLarvae();

		log.debug("---- END GAME TICK {} ----", client.getTickCount());
	}

	private void setCounter()
	{
		counter = new Counter(spriteManager.getSprite(SpriteID.IconBoss25x25.DOOM_OF_MOKHAIOTL, 0), this, client.getVarbitValue(VarbitID.DOM_MISSED_ORBS))
		{
			@Override
			public boolean render()
			{
				return getCount() > 0 && config.infoboxLarvaCounter();
			}
		};

		counter.setTooltip("Demonic Charge");

		infoBoxManager.addInfoBox(counter);
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
			final var larva = larvae.get(npc);
			if (larva != null)
			{
				if (config.hideDeadLarva() && (npc.isDead() || larva.isDead()))
				{
					return false;
				}

				if (npc.getId() != NpcID.DOM_DEMONIC_ENERGY)
				{
					return !(overheads && config.hideLarvaOverheads());
				}
			}
		}

		return true;
	}

	private boolean processLag()
	{
		if (lastTickNano == 0)
		{
			lastTickNano = System.nanoTime();
			return false;
		}

		final long time = System.nanoTime();
		final int lastTickMillis = (int) ((time - lastTickNano) / 1_000_000L);
		lastTickNano = time;

		if (lastTickMillis < config.lagProtectionThreshold())
		{
			return false;
		}

		if (config.printLagMessages())
		{
			client.addChatMessage(
				ChatMessageType.GAMEMESSAGE,
				"DemonicLarvaTracker",
				String.format("[<col=FF0000>D<col=00FF00>L<col=0000FF>T</col>] <col=FFFF00>Warning</col>: Tick %d was <col=FFA500>%d ms</col>.",
					client.getTickCount(), lastTickMillis),
				null
			);
		}

		larvae.values().forEach(Larva::revive);
		larvaHitsplats.clear();
		realXpDrops.clear();
		fakeXpDrops.clear();

		return true;
	}

	private void processHitSplats()
	{
		if (larvaHitsplats.isEmpty())
		{
			return;
		}

		larvaHitsplats.keySet().removeIf(l -> !larvae.containsValue(l));

		for (final var entry : larvaHitsplats.entrySet())
		{
			final var larva = entry.getKey();
			final var npc = larva.getNpc();

			final int damage = entry.getValue().getAmount();

			log.debug("{} - processHitSplats (damage): {} ({}) queuedDamage={} damage={}",
				client.getTickCount(), npc.getName(), npc.getIndex(), larva.getQueuedDamage(), damage);

			larva.dequeueDamage(damage);
			if (larva.hasQueuedDamage())
			{
				continue;
			}

			larva.recalcHp();

			log.debug("{} - processHitSplats (hp): {} ({}) healthRatio={} healthScale={} hp={}",
				client.getTickCount(), npc.getName(), npc.getIndex(), npc.getHealthRatio(), npc.getHealthScale(), larva.getHp());

			if (larva.isDead())
			{
				larva.kill(client.getTickCount());

				log.debug("{} - processHitSplats (kill): {} ({}) hp={} deathTick={}",
					client.getTickCount(), npc.getName(), npc.getIndex(), larva.getHp(), larva.getDeathTick());
			}
			else
			{
				larva.resetDeathTick();

				log.debug("{} - processHitSplats (alive): {} ({}) hp={} deathTick={}",
					client.getTickCount(), npc.getName(), npc.getIndex(), larva.getHp(), larva.getDeathTick());
			}
		}
	}

	private void processXpDrops(final Map<Skill, Integer> xpDrops)
	{
		if (xpDrops.isEmpty())
		{
			return;
		}

		if (interactingNpc == null || attackStyle == null)
		{
			log.debug("{} - processXpDrops (skipping): interactingNpc={} attackStyle={}",
				client.getTickCount(), interactingNpc, attackStyle);
			return;
		}

		final var larva = larvae.get(interactingNpc);
		if (larva == null || larva.isDead() || larva.isXpProcessed())
		{
			log.debug("{} - processXpDrops (skipping): larva null/dead/processed", client.getTickCount());
			return;
		}

		final var npc = larva.getNpc();

		for (final var entry : xpDrops.entrySet())
		{
			var skill = entry.getKey();
			final var xp = entry.getValue();

			if (skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE || skill == Skill.RANGED)
			{
				if (attackStyle == AttackStyle.LONGRANGE)
				{
					skill = Skill.RANGED;
				}
			}
			else if (skill == Skill.HITPOINTS && attackStyle != AttackStyle.CASTING)
			{
				log.debug("{} - processXpDrops (skipping): skill={} attackStyle={} != CASTING", client.getTickCount(), skill, attackStyle);
				continue;
			}

			larva.setXpProcessed(true);

			final int damage = calcDamageFromXpDrop(skill, xp);
			larva.damage(damage);

			if (larva.isDead())
			{
				larva.kill(client.getTickCount());

				log.debug("{} - processXpDrops (killed): {} ({}) damage={} queuedDamage={} hp={} deathTick={}",
					client.getTickCount(), npc.getName(), npc.getIndex(), damage, larva.getQueuedDamage(), larva.getHp(), larva.getDeathTick());
			}
			else
			{
				larva.resetDeathTick();

				log.debug("{} - processXpDrops (alive): {} ({}) damage={} queuedDamage={} hp={} deathTick={}",
					client.getTickCount(), npc.getName(), npc.getIndex(), damage, larva.getQueuedDamage(), larva.getHp(), larva.getDeathTick());
			}
		}
	}

	private void reviveLarvae()
	{
		if (larvae.isEmpty())
		{
			return;
		}

		for (final var entry : larvae.entrySet())
		{
			final var npc = entry.getKey();
			final var larva = entry.getValue();

			if (!larva.isTimedOut(client.getTickCount(), config.deathTickTimeout()))
			{
				continue;
			}

			final int deathTick = larva.getDeathTick();

			larva.revive();

			log.debug("{} - reviveLarvae: {} ({}) hp={} queuedDamage={} deathTick={}",
				client.getTickCount(), npc.getName(), npc.getIndex(), larva.getHp(), larva.getQueuedDamage(), deathTick);
		}
	}

	private void initAttackStyles()
	{
		final int equippedWeaponTypeVarbit = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
		final int attackStyleVarbit = client.getVarpValue(VarPlayerID.COM_MODE);
		final int castingModeVarbit = client.getVarbitValue(VarbitID.AUTOCAST_DEFMODE);

		updateAttackStyle(equippedWeaponTypeVarbit, attackStyleVarbit, castingModeVarbit);
	}

	private void updateAttackStyle(final int equippedWeaponType, int attackStyleIndex, final int castingMode)
	{
		final AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponType);

		if (attackStyleIndex < attackStyles.length)
		{
			if (attackStyleIndex == 4)
			{
				attackStyleIndex += castingMode;
			}

			attackStyle = attackStyles[attackStyleIndex];

			if (attackStyle == AttackStyle.DEFENSIVE)
			{
				if (Arrays.equals(attackStyles, ATTACK_STYLES_POWERED_STAVE))
				{
					attackStyle = AttackStyle.DEFENSIVE_CASTING;
				}
			}
			else if (attackStyle == null)
			{
				attackStyle = AttackStyle.OTHER;
			}

			log.debug("{} - updateAttackStyle: attackStyle={}", client.getTickCount(), attackStyle);
		}
	}

	private AttackStyle[] getWeaponTypeStyles(final int weaponType)
	{
		final int weaponStyleEnum = client.getEnum(EnumID.WEAPON_STYLES).getIntValue(weaponType);
		if (weaponStyleEnum == -1)
		{
			if (weaponType == 22)
			{
				return new AttackStyle[]{
					AttackStyle.ACCURATE,
					AttackStyle.AGGRESSIVE,
					null,
					AttackStyle.DEFENSIVE,
					AttackStyle.CASTING,
					AttackStyle.DEFENSIVE_CASTING
				};
			}

			if (weaponType == 30)
			{
				return new AttackStyle[]{
					AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE
				};
			}
			return new AttackStyle[0];
		}
		final int[] weaponStyleStructs = client.getEnum(weaponStyleEnum).getIntVals();

		final AttackStyle[] styles = new AttackStyle[weaponStyleStructs.length];
		int i = 0;
		for (final int style : weaponStyleStructs)
		{
			final StructComposition attackStyleStruct = client.getStructComposition(style);
			final String attackStyleName = attackStyleStruct.getStringValue(ParamID.ATTACK_STYLE_NAME);

			AttackStyle attackStyle = AttackStyle.valueOf(attackStyleName.toUpperCase());
			if (attackStyle == AttackStyle.OTHER)
			{
				++i;
				continue;
			}

			if (i == 5 && attackStyle == AttackStyle.DEFENSIVE)
			{
				attackStyle = AttackStyle.DEFENSIVE_CASTING;
			}

			styles[i++] = attackStyle;
		}
		return styles;
	}

	private int calcDamageFromXpDrop(final Skill skill, final int xp)
	{
		if (attackStyle == null)
		{
			return 0;
		}

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
			id == NpcID.DOM_DEMONIC_ENERGY_MAGE || id == NpcID.DOM_DEMONIC_ENERGY_MELEE ||
			id == NpcID.DOM_DEMONIC_ENERGY_GIANT_RANGE || id == NpcID.DOM_DEMONIC_ENERGY_GIANT_MAGE;
	}
}
