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
		name = "General",
		description = "",
		position = 0
	)
	String SECTION_GENERAL = "general";

	@ConfigSection(
		name = "Miscellaneous",
		description = "",
		position = 1,
		closedByDefault = true
	)
	String SECTION_MISCELLANEOUS = "miscellaneous";

	// General

	@ConfigItem(
		name = "Hide Sail",
		description = "",
		keyName = "hideSail",
		position = 0,
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
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
		section = SECTION_GENERAL
	)
	default boolean hideCannon()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Ballistic Attractor",
		description = "",
		keyName = "hideBallisticAttractor",
		position = 10,
		section = SECTION_GENERAL
	)
	default boolean hideBallisticAttractor()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Trawling Net",
		description = "",
		keyName = "hideTrawlingNet",
		position = 11,
		section = SECTION_GENERAL
	)
	default boolean hideTrawlingNet()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Fathom Stone/Pearl",
		description = "",
		keyName = "hideFathomStonePearl",
		position = 12,
		section = SECTION_GENERAL
	)
	default boolean hideFathomStonePearl()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Anchor",
		description = "",
		keyName = "hideAnchor",
		position = 13,
		section = SECTION_GENERAL
	)
	default boolean hideAnchor()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Bosun's Workbench",
		description = "",
		keyName = "hideBosunsWorkbench",
		position = 14,
		section = SECTION_GENERAL
	)
	default boolean hideBosunsWorkbench()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_HULL = "hideHull";

	@ConfigItem(
		name = "Hide Hull",
		description = "",
		keyName = CONFIG_KEY_HIDE_HULL,
		position = 15,
		section = SECTION_GENERAL
	)
	default boolean hideHull()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_KEEL = "hideKeel";

	@ConfigItem(
		name = "Hide Keel",
		description = "",
		keyName = CONFIG_KEY_HIDE_KEEL,
		position = 16,
		section = SECTION_GENERAL
	)
	default boolean hideKeel()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_TRIM = "hideTrim";

	@ConfigItem(
		name = "Hide Trim",
		description = "",
		keyName = CONFIG_KEY_HIDE_TRIM,
		position = 17,
		section = SECTION_GENERAL
	)
	default boolean hideTrim()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_SALVAGING_STATION = "hideSalvagingStation";

	@ConfigItem(
		name = "Hide Salvaging Station",
		description = "",
		keyName = CONFIG_KEY_HIDE_SALVAGING_STATION,
		position = 18,
		section = SECTION_GENERAL
	)
	default boolean hideSalvagingStation()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_CARGO_HOLD = "hideCargoHold";

	@ConfigItem(
		name = "Hide Cargo Hold",
		description = "",
		keyName = CONFIG_KEY_HIDE_CARGO_HOLD,
		position = 19,
		section = SECTION_GENERAL
	)
	default boolean hideCargoHold()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_INOCULATION_STATION = "hideInoculationStation";

	@ConfigItem(
		name = "Hide Inoculation Station",
		description = "",
		keyName = CONFIG_KEY_HIDE_INOCULATION_STATION,
		position = 20,
		section = SECTION_GENERAL
	)
	default boolean hideInoculationStation()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_KEG = "hideKeg";

	@ConfigItem(
		name = "Hide Keg",
		description = "",
		keyName = CONFIG_KEY_HIDE_KEG,
		position = 21,
		section = SECTION_GENERAL
	)
	default boolean hideKeg()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_CHUM_STATION = "hideChumStation";

	@ConfigItem(
		name = "Hide Chum Station",
		description = "",
		keyName = CONFIG_KEY_HIDE_CHUM_STATION,
		position = 22,
		section = SECTION_GENERAL
	)
	default boolean hideChumStation()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_RANGE = "hideRange";

	@ConfigItem(
		name = "Hide Range",
		description = "",
		keyName = CONFIG_KEY_HIDE_RANGE,
		position = 23,
		section = SECTION_GENERAL
	)
	default boolean hideRange()
	{
		return false;
	}

	// Miscellaneous

	@ConfigItem(
		name = "Hide Other Player Boats",
		description = "Hide other player's boats while still showing the players.",
		keyName = "hideOtherPlayerBoats",
		position = 0,
		section = SECTION_MISCELLANEOUS
	)
	default boolean hideOtherPlayerBoat()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_OTHER = "hideOther";

	@ConfigItem(
		name = "Hide Other",
		description = "Hide objects with no config option.",
		keyName = CONFIG_KEY_HIDE_OTHER,
		position = 1,
		section = SECTION_MISCELLANEOUS
	)
	default boolean hideOther()
	{
		return false;
	}
}
