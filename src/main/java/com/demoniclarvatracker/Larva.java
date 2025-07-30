package com.demoniclarvatracker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.runelite.api.NPC;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class Larva
{
	private static final int MAX_HP = 2;
	private static final int DEATH_TICK_THRESHOLD = 2;

	@EqualsAndHashCode.Include
	private final NPC npc;

	private int deathTick;

	private int hp = MAX_HP;

	private boolean processed;

	Larva(final @NonNull NPC npc)
	{
		this.npc = npc;
	}

	void applyDamage(final int amount)
	{
		hp = Math.max(0, hp - amount);
	}

	boolean isDead()
	{
		return hp == 0;
	}

	void kill(final int tick)
	{
		hp = 0;
		deathTick = tick;
	}

	void revive()
	{
		hp = MAX_HP;
		deathTick = 0;
	}

	boolean isExpired(final int tick)
	{
		return deathTick != 0 && tick > (deathTick + DEATH_TICK_THRESHOLD);
	}

	@Override
	public String toString()
	{
		return String.format("[name=%s hp=%d deathTick=%d isDead=%s]", npc.getName(), hp, deathTick, isDead());
	}
}
