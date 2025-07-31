package com.demoniclarvatracker;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.runelite.api.NPC;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Larva
{
	private static final int MAX_HP = 2;
	private static final int DEATH_TICK_THRESHOLD = 2;

	@EqualsAndHashCode.Include
	@Getter(AccessLevel.PACKAGE)
	private final NPC npc;

	@Getter(AccessLevel.PACKAGE)
	private int hp = MAX_HP;

	@Getter(AccessLevel.PACKAGE)
	private int deathTick;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private boolean xpProcessed;

	Larva(final @NonNull NPC npc)
	{
		this.npc = npc;
	}

	boolean isDead()
	{
		return hp == 0;
	}

	void applyDamage(final int amount)
	{
		hp = Math.max(0, hp - amount);
	}

	void kill(final int tick)
	{
		hp = 0;
		deathTick = tick;
	}

	void resetDeathTick()
	{
		deathTick = 0;
	}

	void revive()
	{
		recalcHp();
		deathTick = 0;
	}

	void recalcHp()
	{
		final int ratio = npc.getHealthRatio();
		final int scale = npc.getHealthScale();

		if (ratio == -1 || scale == -1)
		{
			return;
		}

		hp = (int) (MAX_HP * ((double) ratio / scale));
	}

	boolean isExpired(final int tick)
	{
		return deathTick != 0 && tick > (deathTick + DEATH_TICK_THRESHOLD);
	}
}
