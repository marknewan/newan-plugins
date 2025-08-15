package com.demoniclarvatracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
class WidgetOverlay extends Overlay
{
	private static final int MINIMAP_DOT_RADIUS = 4;

	private final DemonicLarvaTrackerPlugin plugin;
	private final DemonicLarvaTrackerConfig config;

	@Inject
	public WidgetOverlay(final DemonicLarvaTrackerPlugin plugin, final DemonicLarvaTrackerConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (!config.highlightMinimap())
		{
			return null;
		}

		final var larvae = plugin.getLarvae();
		if (larvae.isEmpty())
		{
			return null;
		}

		for (final var entry : larvae.entrySet())
		{
			final var npc = entry.getKey();

			if (npc.isDead() || (config.hideDeadLarva() && entry.getValue().isDead()))
			{
				continue;
			}

			final var point = npc.getMinimapLocation();
			if (point == null)
			{
				continue;
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
					continue;
			}

			graphics2D.setColor(Color.BLACK);
			graphics2D.fillOval(point.getX() - MINIMAP_DOT_RADIUS / 2, point.getY() - MINIMAP_DOT_RADIUS / 2 + 1, MINIMAP_DOT_RADIUS, MINIMAP_DOT_RADIUS);
			graphics2D.setColor(color);
			graphics2D.fillOval(point.getX() - MINIMAP_DOT_RADIUS / 2, point.getY() - MINIMAP_DOT_RADIUS / 2, MINIMAP_DOT_RADIUS, MINIMAP_DOT_RADIUS);
		}

		return null;
	}
}