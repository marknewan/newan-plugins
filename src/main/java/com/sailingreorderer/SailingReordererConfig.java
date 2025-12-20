package com.sailingreorderer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(SailingReordererConfig.CONFIG_GROUP)
public interface SailingReordererConfig extends Config
{
	String CONFIG_GROUP = "sailingreorderer";
	String CONFIG_PREFIX_BOAT = "boat_";

	@ConfigItem(
		name = "Default Steering Button",
		description = "Restore the default location of the steering assign button.",
		keyName = "defaultSteeringButton",
		position = 0
	)
	default boolean defaultSteeringButton()
	{
		return false;
	}
}
