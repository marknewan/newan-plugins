package com.demoniclarvatracker;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.util.ColorUtil;

@ConfigGroup(DemonicLarvaTrackerConfig.CONFIG_GROUP)
public interface DemonicLarvaTrackerConfig extends Config
{
	String CONFIG_GROUP = "demoniclarvatracker";

	@ConfigSection(
		name = "General",
		description = "",
		position = 0,
		closedByDefault = true
	)
	String SECTION_GENERAL = "general";

	@ConfigSection(
		name = "Highlights",
		description = "",
		position = 1,
		closedByDefault = true
	)
	String SECTION_HIGHLIGHTS = "highlights";

	@ConfigSection(
		name = "Colors",
		description = "",
		position = 2,
		closedByDefault = true
	)
	String SECTION_COLORS = "colors";

	// General

	// General

	@ConfigItem(
		name = "Death Prediction",
		description = "Hide larva predicted to die.",
		position = 0,
		keyName = "hideDeadLarva",
		section = SECTION_GENERAL
	)
	default boolean hideDeadLarva()
	{
		return true;
	}

	@Units(value = "ms")
	@ConfigItem(
		name = "Death Lag Protection",
		description = "Unhide larva after a lag spike.",
		position = 1,
		keyName = "lagProtectionThreshold",
		section = SECTION_GENERAL
	)
	default int lagProtectionThreshold()
	{
		return 1000;
	}

	@ConfigItem(
		name = "Print Lag Messages",
		description = "Print to chat when a lag spike is encountered." +
			"<br>Increase the lag protection if you see these messages a lot.",
		position = 2,
		keyName = "printLagMessages",
		section = SECTION_GENERAL
	)
	default boolean printLagMessages()
	{
		return false;
	}

	@ConfigItem(
		name = "Hide Larva Overheads",
		description = "Hide larva overheads." +
			"<br>Only applies to range, melee, and magic larvae.",
		position = 3,
		keyName = "hideLarvaOverheads",
		section = SECTION_GENERAL
	)
	default boolean hideLarvaOverheads()
	{
		return true;
	}

	@ConfigItem(
		name = "Recolor Larva Menu Entries",
		description = "Recolor menu entries for larva npcs.",
		position = 4,
		keyName = "recolorLarvaMenuEntries",
		section = SECTION_GENERAL
	)
	default boolean recolorLarvaMenuEntries()
	{
		return true;
	}

	@ConfigItem(
		name = "Remove Spawn Animation",
		description = "Remove the spawn animation from larvas.",
		position = 5,
		keyName = "removeSpawnAnimation",
		section = SECTION_GENERAL
	)
	default boolean removeSpawnAnimation()
	{
		return true;
	}

	// Highlights

	// Highlight Outline

	@ConfigItem(
		name = "Highlight Outline",
		description = "Highlight outline of larva npcs.",
		position = 0,
		keyName = "highlightOutline",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightOutline()
	{
		return false;
	}

	@Range(max = 50)
	@ConfigItem(
		name = "Width",
		description = "Width of highlight outline.",
		position = 1,
		keyName = "highlightOutlineWidth",
		section = SECTION_HIGHLIGHTS
	)
	default int highlightOutlineWidth()
	{
		return 1;
	}

	@Range(max = 4)
	@ConfigItem(
		name = "Feather",
		description = "Feather of highlight outline.",
		position = 2,
		keyName = "highlightOutlineFeather",
		section = SECTION_HIGHLIGHTS
	)
	default int highlightOutlineFeather()
	{
		return 1;
	}

	// Highlight Tile

	@ConfigItem(
		name = "Highlight Tile Outline",
		description = "Highlight tile outline of larva npcs.",
		position = 3,
		keyName = "highlightTileOutline",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightTileOutline()
	{
		return false;
	}

	@Range(max = 5)
	@ConfigItem(
		name = "Width",
		description = "Width of highlight tile outline.",
		position = 4,
		keyName = "highlightTileOutlineWidth",
		section = SECTION_HIGHLIGHTS
	)
	default double highlightTileOutlineWidth()
	{
		return 1;
	}

	@ConfigItem(
		name = "Highlight Tile Fill",
		description = "Highlight tile fill of larva npcs.",
		position = 5,
		keyName = "highlightTileFill",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightTileFill()
	{
		return false;
	}

	@ConfigItem(
		name = "Tile Mode",
		description = "Highlight tile mode of larva npcs.",
		position = 6,
		keyName = "highlightTileMode",
		section = SECTION_HIGHLIGHTS
	)
	default TileMode highlightTileMode()
	{
		return TileMode.TRUE_TILE;
	}

	// Highlight Hull

	@ConfigItem(
		name = "Highlight Hull Outline",
		description = "Highlight hull outline of larva npcs.",
		position = 7,
		keyName = "highlightHullOutline",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightHullOutline()
	{
		return false;
	}

	@Range(max = 50)
	@ConfigItem(
		name = "Width",
		description = "Width of highlight hull outline.",
		position = 8,
		keyName = "highlightHullWidth",
		section = SECTION_HIGHLIGHTS
	)
	default double highlightHullWidth()
	{
		return 1;
	}

	@ConfigItem(
		name = "Highlight Hull Fill",
		description = "Highlight hull fill of larva npcs.",
		position = 9,
		keyName = "highlightHullFill",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightHullFill()
	{
		return false;
	}

	// Highlight Minimap

	@ConfigItem(
		name = "Highlight Minimap",
		description = "Highlight minimap location of larva npcs.",
		position = 10,
		keyName = "highlightMinimap",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightMinimap()
	{
		return false;
	}

	@ConfigItem(
		name = "Anti-aliasing",
		description = "Smooths out edges of outlines." +
			"<br>Reduces 'jagged' or 'stair-step' appearance that can occur.",
		position = 11,
		keyName = "antiAliasing",
		section = SECTION_HIGHLIGHTS
	)
	default boolean antiAliasing()
	{
		return false;
	}

	// Colors

	// Demonic Larva

	@Alpha
	@ConfigItem(
		name = "Base Outline",
		description = "Demonic larva outline color.",
		position = 0,
		keyName = "colorOutlineBase",
		section = SECTION_COLORS
	)
	default Color colorOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Base Tile Outline",
		description = "Demonic larva tile outline color.",
		position = 1,
		keyName = "colorTileOutlineBase",
		section = SECTION_COLORS
	)
	default Color colorTileOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Base Tile Fill",
		description = "Demonic larva tile fill color.",
		position = 2,
		keyName = "colorTileFillBase",
		section = SECTION_COLORS
	)
	default Color colorTileFillBase()
	{
		return ColorUtil.colorWithAlpha(Color.WHITE, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Base Hull Outline",
		description = "Demonic larva hull outline color.",
		position = 3,
		keyName = "colorHullOutlineBase",
		section = SECTION_COLORS
	)
	default Color colorHullOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Base Hull Fill",
		description = "Demonic larva hull fill color.",
		position = 4,
		keyName = "colorHullFillBase",
		section = SECTION_COLORS
	)
	default Color colorHullFillBase()
	{
		return ColorUtil.colorWithAlpha(Color.WHITE, 16);
	}

	@ConfigItem(
		name = "Base Menu",
		description = "Demonic larva menu color.",
		position = 5,
		keyName = "colorMenuBase",
		section = SECTION_COLORS
	)
	default Color colorMenuBase()
	{
		return Color.WHITE;
	}

	// Demonic melee larva

	@Alpha
	@ConfigItem(
		name = "Melee Outline",
		description = "Demonic melee larva outline color.",
		position = 6,
		keyName = "colorOutlineMelee",
		section = SECTION_COLORS
	)
	default Color colorOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Melee Tile Outline",
		description = "Demonic melee larva tile outline color.",
		position = 7,
		keyName = "colorTileOutlineMelee",
		section = SECTION_COLORS
	)
	default Color colorTileOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Melee Tile Fill",
		description = "Demonic melee larva tile fill color.",
		position = 8,
		keyName = "colorTileFillMelee",
		section = SECTION_COLORS
	)
	default Color colorTileFillMelee()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Melee Hull Outline",
		description = "Demonic melee larva hull outline color.",
		position = 9,
		keyName = "colorHullOutlineMelee",
		section = SECTION_COLORS
	)
	default Color colorHullOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Melee Hull Fill",
		description = "Demonic melee larva hull fill color.",
		position = 10,
		keyName = "colorHullFillMelee",
		section = SECTION_COLORS
	)
	default Color colorHullFillMelee()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 16);
	}

	@ConfigItem(
		name = "Melee Menu",
		description = "Demonic melee larva menu color.",
		position = 11,
		keyName = "colorMenuMelee",
		section = SECTION_COLORS
	)
	default Color colorMenuMelee()
	{
		return Color.RED;
	}

	// Demonic range larva

	@Alpha
	@ConfigItem(
		name = "Range Outline",
		description = "Demonic range larva outline color.",
		position = 12,
		keyName = "colorOutlineRange",
		section = SECTION_COLORS
	)
	default Color colorOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Range Tile Outline",
		description = "Demonic range larva tile outline color.",
		position = 13,
		keyName = "colorTileOutlineRange",
		section = SECTION_COLORS
	)
	default Color colorTileOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Range Tile Fill",
		description = "Demonic range larva tile fill color.",
		position = 14,
		keyName = "colorTileFillRange",
		section = SECTION_COLORS
	)
	default Color colorTileFillRange()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Range Hull Outline",
		description = "Demonic range larva hull outline color.",
		position = 15,
		keyName = "colorHullOutlineRange",
		section = SECTION_COLORS
	)
	default Color colorHullOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Range Hull Fill",
		description = "Demonic range larva hull fill color.",
		position = 16,
		keyName = "colorHullFillRange",
		section = SECTION_COLORS
	)
	default Color colorHullFillRange()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, 16);
	}

	@ConfigItem(
		name = "Range Menu",
		description = "Demonic range larva menu color.",
		position = 17,
		keyName = "colorMenuRange",
		section = SECTION_COLORS
	)
	default Color colorMenuRange()
	{
		return Color.GREEN;
	}

	// Demonic magic larva

	@Alpha
	@ConfigItem(
		name = "Magic Outline",
		description = "Demonic magic larva outline color.",
		position = 18,
		keyName = "colorOutlineMagic",
		section = SECTION_COLORS
	)
	default Color colorOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Magic Tile Outline",
		description = "Demonic magic larva tile outline color.",
		position = 19,
		keyName = "colorTileOutlineMagic",
		section = SECTION_COLORS
	)
	default Color colorTileOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Magic Tile Fill",
		description = "Demonic magic larva tile fill color.",
		position = 20,
		keyName = "colorTileFillMagic",
		section = SECTION_COLORS
	)
	default Color colorTileFillMagic()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Magic Hull Outline",
		description = "Demonic magic larva hull outline color.",
		position = 21,
		keyName = "colorHullOutlineMagic",
		section = SECTION_COLORS
	)
	default Color colorHullOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Magic Hull Fill",
		description = "Demonic magic larva hull fill color.",
		position = 22,
		keyName = "colorHullFillMagic",
		section = SECTION_COLORS
	)
	default Color colorHullFillMagic()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, 16);
	}

	@ConfigItem(
		name = "Magic Menu",
		description = "Demonic magic larva menu color.",
		position = 23,
		keyName = "colorMenuMagic",
		section = SECTION_COLORS
	)
	default Color colorMenuMagic()
	{
		return Color.BLUE;
	}
}
