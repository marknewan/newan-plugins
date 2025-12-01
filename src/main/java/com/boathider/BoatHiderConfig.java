package com.boathider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(BoatHiderConfig.CONFIG_GROUP)
public interface BoatHiderConfig extends Config
{
	String CONFIG_GROUP = "boat-hider";

	@ConfigSection(
		name = "Dynamic Objects",
		description = "Animated objects that will hide/unhide instantly.",
		position = 1
	)
	String SECTION_DYNAMIC = "dynamicObjects";

	@ConfigSection(
		name = "Static Objects (read desc.)",
		description = "Non-animated objects that require reloading the boat to hide/unhide." +
			"<br>On a skiff, it is sufficient to simply navigate/stop-navigating the helm of the boat to reload it." +
			"<br>Otherwise, switching boats, teleporting away, or re-logging forces a reload.",
		position = 2,
		closedByDefault = true
	)
	String SECTION_STATIC = "staticObjects";

	@ConfigSection(
		name = "Miscellaneous",
		description = "Both animated and non-animated objects.",
		position = 3,
		closedByDefault = true
	)
	String SECTION_MISCELLANEOUS = "miscellaneous";

	// Dynamic

	@ConfigItem(
		name = "Hide Sail",
		description = "",
		keyName = "hideSail",
		position = 0,
		section = SECTION_DYNAMIC
	)
	default boolean hideSail()
	{
		return true;
	}

	@ConfigItem(
		name = "Hide Helm",
		description = "",
		keyName = "hideHelm",
		position = 1,
		section = SECTION_DYNAMIC
	)
	default boolean hideHelm()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Wind/Gale Catcher",
		description = "",
		keyName = "hideWindGaleCatcher",
		position = 2,
		section = SECTION_DYNAMIC
	)
	default boolean hideWindGaleCatcher()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Crystal Extractor",
		description = "",
		keyName = "hideCrystalExtractor",
		position = 3,
		section = SECTION_DYNAMIC
	)
	default boolean hideCrystalExtractor()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Crystal Extractor Bar",
		description = "",
		keyName = "hideCrystalExtractorBar",
		position = 4,
		section = SECTION_DYNAMIC
	)
	default boolean hideCrystalExtractorBar()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Teleporation Focus",
		description = "",
		keyName = "hideTeleportationFocus",
		position = 5,
		section = SECTION_DYNAMIC
	)
	default boolean hideTeleportationFocus()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Eternal Brazier",
		description = "",
		keyName = "hideEternalBrazier",
		position = 6,
		section = SECTION_DYNAMIC
	)
	default boolean hideEternalBrazier()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Flag",
		description = "",
		keyName = "hideFlag",
		position = 7,
		section = SECTION_DYNAMIC
	)
	default boolean hideFlag()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Salvaging Hook",
		description = "",
		keyName = "hideSalvagingHook",
		position = 8,
		section = SECTION_DYNAMIC
	)
	default boolean hideSalvagingHook()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Cannon",
		description = "",
		keyName = "hideCannon",
		position = 9,
		section = SECTION_DYNAMIC
	)
	default boolean hideCannon()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Trawling Net",
		description = "",
		keyName = "hideTrawlingNet",
		position = 10,
		section = SECTION_DYNAMIC
	)
	default boolean hideTrawlingNet()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Fathom Stone/Pearl",
		description = "",
		keyName = "hideFathomStonePearl",
		position = 11,
		section = SECTION_DYNAMIC
	)
	default boolean hideFathomStonePearl()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Anchor",
		description = "",
		keyName = "hideAnchor",
		position = 12,
		section = SECTION_DYNAMIC
	)
	default boolean hideAnchor()
	{
		return false;
	}

	// Static

	@ConfigItem(
		name = "Hide Hull",
		description = "",
		keyName = "hideHull",
		position = 0,
		section = SECTION_STATIC
	)
	default boolean hideHull()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Keel",
		description = "",
		keyName = "hideKeel",
		position = 1,
		section = SECTION_STATIC
	)
	default boolean hideKeel()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Trim",
		description = "",
		keyName = "hideTrim",
		position = 2,
		section = SECTION_STATIC
	)
	default boolean hideTrim()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Salvaging Station",
		description = "",
		keyName = "hideSalvagingStation",
		position = 3,
		section = SECTION_STATIC
	)
	default boolean hideSalvagingStation()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Cargo Hold",
		description = "",
		keyName = "hideCargoHold",
		position = 4,
		section = SECTION_STATIC
	)
	default boolean hideCargoHold()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Inoculation Station",
		description = "",
		keyName = "hideInoculationStation",
		position = 5,
		section = SECTION_STATIC
	)
	default boolean hideInoculationStation()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Keg",
		description = "",
		keyName = "hideKeg",
		position = 6,
		section = SECTION_STATIC
	)
	default boolean hideKeg()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Chum Station",
		description = "",
		keyName = "hideChumStation",
		position = 7,
		section = SECTION_STATIC
	)
	default boolean hideChumStation()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Range",
		description = "",
		keyName = "hideRange",
		position = 8,
		section = SECTION_STATIC
	)
	default boolean hideRange()
	{
		return false;
	}

	// Miscellaneous

	@ConfigItem(
		name = "Hide Other Player Boats",
		description = "",
		keyName = "hideOtherPlayerBoats",
		position = 0,
		section = SECTION_MISCELLANEOUS
	)
	default boolean hideOtherPlayerBoat()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Other",
		description = "Hide objects with no config option.",
		keyName = "hideOther",
		position = 1,
		section = SECTION_MISCELLANEOUS
	)
	default boolean hideOther()
	{
		return false;
	}
}
