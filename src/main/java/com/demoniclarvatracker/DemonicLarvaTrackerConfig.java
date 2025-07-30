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
		name = "Base Larva",
		description = "",
		position = 2,
		closedByDefault = true
	)
	String SECTION_BASE_LARVA = "baseLarva";

	@ConfigSection(
		name = "Melee Larva",
		description = "",
		position = 3,
		closedByDefault = true
	)
	String SECTION_MELEE_LARVA = "meleeLarva";

	@ConfigSection(
		name = "Range Larva",
		description = "",
		position = 4,
		closedByDefault = true
	)
	String SECTION_RANGE_LARVA = "rangeLarva";

	@ConfigSection(
		name = "Magic Larva",
		description = "",
		position = 5,
		closedByDefault = true
	)
	String SECTION_MAGIC_LARVA = "magicLarva";

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

	// Highlight Clickbox

	@ConfigItem(
		name = "Highlight Clickbox Outline",
		description = "Highlight clickbox outline of larva npcs.",
		position = 10,
		keyName = "highlightClickBoxOutline",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightClickBoxOutline()
	{
		return false;
	}

	@Range(max = 50)
	@ConfigItem(
		name = "Width",
		description = "Width of highlight clickbox outline.",
		position = 11,
		keyName = "highlightClickBoxWidth",
		section = SECTION_HIGHLIGHTS
	)
	default double highlightClickBoxWidth()
	{
		return 1;
	}

	@ConfigItem(
		name = "Highlight Clickbox Fill",
		description = "Highlight clickbox fill of larva npcs.",
		position = 12,
		keyName = "highlightClickBoxFill",
		section = SECTION_HIGHLIGHTS
	)
	default boolean highlightClickBoxFill()
	{
		return false;
	}

	// Highlight Minimap

	@ConfigItem(
		name = "Highlight Minimap",
		description = "Highlight minimap location of larva npcs.",
		position = 13,
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
		position = 14,
		keyName = "antiAliasing",
		section = SECTION_HIGHLIGHTS
	)
	default boolean antiAliasing()
	{
		return false;
	}

	// Colors

	// Base Colors

	@Alpha
	@ConfigItem(
		name = "Outline",
		description = "",
		position = 0,
		keyName = "colorOutlineBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Outline",
		description = "",
		position = 1,
		keyName = "colorTileOutlineBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorTileOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Fill",
		description = "",
		position = 2,
		keyName = "colorTileFillBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorTileFillBase()
	{
		return ColorUtil.colorWithAlpha(Color.WHITE, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Hull Outline",
		description = "",
		position = 3,
		keyName = "colorHullOutlineBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorHullOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Hull Fill",
		description = "",
		position = 4,
		keyName = "colorHullFillBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorHullFillBase()
	{
		return ColorUtil.colorWithAlpha(Color.WHITE, 16);
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Outline",
		description = "",
		position = 5,
		keyName = "colorClickBoxOutlineBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorClickBoxOutlineBase()
	{
		return Color.WHITE;
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Fill",
		description = "",
		position = 6,
		keyName = "colorClickBoxFillBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorClickBoxFillBase()
	{
		return ColorUtil.colorWithAlpha(Color.WHITE, 16);
	}

	@ConfigItem(
		name = "Menu Entry",
		description = "",
		position = 7,
		keyName = "colorMenuBase",
		section = SECTION_BASE_LARVA
	)
	default Color colorMenuBase()
	{
		return Color.WHITE;
	}

	// Melee Colors

	@Alpha
	@ConfigItem(
		name = "Outline",
		description = "",
		position = 0,
		keyName = "colorOutlineMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Outline",
		description = "",
		position = 1,
		keyName = "colorTileOutlineMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorTileOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Fill",
		description = "",
		position = 2,
		keyName = "colorTileFillMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorTileFillMelee()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Hull Outline",
		description = "",
		position = 3,
		keyName = "colorHullOutlineMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorHullOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Hull Fill",
		description = "",
		position = 4,
		keyName = "colorHullFillMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorHullFillMelee()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 16);
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Outline",
		description = "",
		position = 5,
		keyName = "colorClickBoxOutlineMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorClickBoxOutlineMelee()
	{
		return Color.RED;
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Fill",
		description = "",
		position = 6,
		keyName = "colorClickBoxFillMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorClickBoxFillMelee()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 16);
	}

	@ConfigItem(
		name = "Menu Entry",
		description = "",
		position = 7,
		keyName = "colorMenuMelee",
		section = SECTION_MELEE_LARVA
	)
	default Color colorMenuMelee()
	{
		return Color.RED;
	}

	// Range Colors

	@Alpha
	@ConfigItem(
		name = "Outline",
		description = "",
		position = 0,
		keyName = "colorOutlineRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Outline",
		description = "",
		position = 1,
		keyName = "colorTileOutlineRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorTileOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Fill",
		description = "",
		position = 2,
		keyName = "colorTileFillRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorTileFillRange()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Hull Outline",
		description = "",
		position = 3,
		keyName = "colorHullOutlineRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorHullOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Hull Fill",
		description = "",
		position = 4,
		keyName = "colorHullFillRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorHullFillRange()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, 16);
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Outline",
		description = "",
		position = 5,
		keyName = "colorClickBoxOutlineRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorClickBoxOutlineRange()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Fill",
		description = "",
		position = 6,
		keyName = "colorClickBoxFillRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorClickBoxFillRange()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, 16);
	}

	@ConfigItem(
		name = "Menu Entry",
		description = "",
		position = 7,
		keyName = "colorMenuRange",
		section = SECTION_RANGE_LARVA
	)
	default Color colorMenuRange()
	{
		return Color.GREEN;
	}

	// Magic Colors

	@Alpha
	@ConfigItem(
		name = "Outline",
		description = "",
		position = 0,
		keyName = "colorOutlineMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Outline",
		description = "",
		position = 1,
		keyName = "colorTileOutlineMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorTileOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Tile Fill",
		description = "",
		position = 2,
		keyName = "colorTileFillMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorTileFillMagic()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, 32);
	}

	@Alpha
	@ConfigItem(
		name = "Hull Outline",
		description = "",
		position = 3,
		keyName = "colorHullOutlineMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorHullOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Hull Fill",
		description = "",
		position = 4,
		keyName = "colorHullFillMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorHullFillMagic()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, 16);
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Outline",
		description = "",
		position = 5,
		keyName = "colorClickBoxOutlineMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorClickBoxOutlineMagic()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Clickbox Fill",
		description = "",
		position = 6,
		keyName = "colorClickBoxFillMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorClickBoxFillMagic()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, 16);
	}

	@ConfigItem(
		name = "Menu Entry",
		description = "",
		position = 7,
		keyName = "colorMenuMagic",
		section = SECTION_MAGIC_LARVA
	)
	default Color colorMenuMagic()
	{
		return Color.BLUE;
	}
}
