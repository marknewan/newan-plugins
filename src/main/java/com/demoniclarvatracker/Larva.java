package com.demoniclarvatracker;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.runelite.api.NPC;

@ToString
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Larva
{
	private static final int MAX_HP = 2;

	@EqualsAndHashCode.Include
	private final NPC npc;

	private int hp = MAX_HP;

	private int queuedDamage;

	private int deathTick;

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

	void damage(final int amount)
	{
		queuedDamage = Math.min(MAX_HP, queuedDamage + amount);
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
		queuedDamage = 0;
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

	boolean isTimedOut(final int tick, final int timeout)
	{
		return deathTick != 0 && tick > (deathTick + timeout);
	}

	void dequeueDamage(final int amount)
	{
		queuedDamage = Math.max(0, queuedDamage - amount);
	}

	boolean hasQueuedDamage()
	{
		return queuedDamage > 0;
	}
}
