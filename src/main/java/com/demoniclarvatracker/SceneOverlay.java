package com.demoniclarvatracker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
class SceneOverlay extends Overlay
{
	private final Client client;
	private final DemonicLarvaTrackerPlugin plugin;
	private final DemonicLarvaTrackerConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	SceneOverlay(
		final Client client,
		final DemonicLarvaTrackerPlugin plugin,
		final DemonicLarvaTrackerConfig config,
		final ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		if (!config.highlightOutline() &&
			!config.highlightTileOutline() &&
			!config.highlightTileFill() &&
			!config.highlightHullOutline() &&
			!config.highlightHullFill() &&
			!config.highlightClickBoxOutline() &&
			!config.highlightClickBoxFill() &&
			!config.highlightNameLabel())
		{
			return null;
		}

		final var larvae = plugin.getLarvae();
		if (larvae.isEmpty())
		{
			return null;
		}

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, config.antiAliasing() ?
			RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		graphics.setFont(FontManager.getRunescapeFont());

		for (final var entry : larvae.entrySet())
		{
			final var npc = entry.getKey();

			if (npc.isDead() || (config.hideDeadLarva() && entry.getValue().isDead()))
			{
				continue;
			}

			if (config.highlightOutline())
			{
				renderOutline(npc);
			}

			if (config.highlightTileOutline() || config.highlightTileFill())
			{
				renderTile(graphics, npc);
			}

			if (config.highlightHullOutline() || config.highlightHullFill())
			{
				renderHull(graphics, npc);
			}

			if (config.highlightClickBoxOutline() || config.highlightClickBoxFill())
			{
				renderClickBox(graphics, npc);
			}

			if (config.highlightNameLabel())
			{
				renderNameLabel(graphics, npc);
			}
		}

		return null;
	}

	private void renderOutline(final NPC npc)
	{
		final Color color;

		// TODO: big larva
		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY:
				color = config.colorOutlineBase();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				color = config.colorOutlineRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				color = config.colorOutlineMagic();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				color = config.colorOutlineMelee();
				break;
			default:
				return;
		}

		modelOutlineRenderer.drawOutline(npc, config.highlightOutlineWidth(), color, config.highlightOutlineFeather());
	}

	private void renderTile(final Graphics2D graphics, final NPC npc)
	{
		final Color outlineColor;
		final Color fillColor;

		// TODO: big larva
		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY:
				outlineColor = config.colorTileOutlineBase();
				fillColor = config.colorTileFillBase();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				outlineColor = config.colorTileOutlineRange();
				fillColor = config.colorTileFillRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				outlineColor = config.colorTileOutlineMagic();
				fillColor = config.colorTileFillMagic();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				outlineColor = config.colorTileOutlineMelee();
				fillColor = config.colorTileFillMelee();
				break;
			default:
				return;
		}

		final Polygon polygon;

		switch (config.highlightTileMode())
		{
			case TILE:
				polygon = npc.getCanvasTilePoly();
				break;
			case TRUE_TILE:
				final var lp = LocalPoint.fromWorld(client.getTopLevelWorldView(), npc.getWorldLocation());
				if (lp == null)
				{
					return;
				}
				polygon = Perspective.getCanvasTilePoly(client, lp);
				break;
			default:
				return;
		}

		if (config.highlightTileOutline())
		{
			graphics.setColor(outlineColor);
			graphics.setStroke(new BasicStroke((float) config.highlightTileOutlineWidth()));
			graphics.draw(polygon);
		}

		if (config.highlightTileFill())
		{
			graphics.setColor(fillColor);
			graphics.fill(polygon);
		}
	}

	private void renderHull(final Graphics2D graphics, final NPC npc)
	{
		final Color outlineColor;
		final Color fillColor;

		// TODO: big larva
		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY:
				outlineColor = config.colorHullOutlineBase();
				fillColor = config.colorHullFillBase();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				outlineColor = config.colorHullOutlineRange();
				fillColor = config.colorHullFillRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				outlineColor = config.colorHullOutlineMagic();
				fillColor = config.colorHullFillMagic();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				outlineColor = config.colorHullOutlineMelee();
				fillColor = config.colorHullFillMelee();
				break;
			default:
				return;
		}

		final var shape = npc.getConvexHull();

		if (config.highlightHullOutline())
		{
			graphics.setColor(outlineColor);
			graphics.setStroke(new BasicStroke((float) config.highlightHullWidth()));
			graphics.draw(shape);
		}

		if (config.highlightHullFill())
		{
			graphics.setColor(fillColor);
			graphics.fill(shape);
		}
	}

	private void renderClickBox(final Graphics2D graphics, final NPC npc)
	{
		final var lp = npc.getLocalLocation();
		if (lp == null)
		{
			return;
		}

		final var shape = Perspective.getClickbox(client, npc.getModel(), npc.getCurrentOrientation(), lp.getX(), lp.getY(),
			Perspective.getTileHeight(client, lp, npc.getWorldView().getPlane()));
		if (shape == null)
		{
			return;
		}

		Color outlineColor;
		Color fillColor;

		// TODO: big larva
		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY:
				outlineColor = config.colorClickBoxOutlineBase();
				fillColor = config.colorClickBoxFillBase();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				outlineColor = config.colorClickBoxOutlineRange();
				fillColor = config.colorClickBoxFillRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				outlineColor = config.colorClickBoxOutlineMagic();
				fillColor = config.colorClickBoxFillMagic();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				outlineColor = config.colorClickBoxOutlineMelee();
				fillColor = config.colorClickBoxFillMelee();
				break;
			default:
				return;
		}

		boolean mouseover = false;

		if (config.highlightClickboxMouseover())
		{
			final var point = client.getMouseCanvasPosition();
			mouseover = shape.contains(point.getX(), point.getY());
		}

		if (config.highlightClickBoxOutline())
		{
			if (mouseover)
			{
				outlineColor = darken(outlineColor, 0.7);
			}

			graphics.setColor(outlineColor);

			graphics.setStroke(new BasicStroke((float) config.highlightClickBoxWidth()));
			graphics.draw(shape);
		}

		if (config.highlightClickBoxFill())
		{
			if (mouseover)
			{
				fillColor = darken(fillColor, 0.5);
			}

			graphics.setColor(fillColor);

			graphics.fill(shape);
		}
	}

	private void renderNameLabel(final Graphics2D graphics, final NPC npc)
	{
		final String label;
		final Color color;

		// TODO: big larva
		switch (npc.getId())
		{
			case NpcID.DOM_DEMONIC_ENERGY_MELEE:
				label = config.nameLabelMelee();
				color = config.colorMenuMelee();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_RANGE:
				label = config.nameLabelRange();
				color = config.colorMenuRange();
				break;
			case NpcID.DOM_DEMONIC_ENERGY_MAGE:
				label = config.nameLabelMagic();
				color = config.colorMenuMagic();
				break;
			default:
				return;
		}

		if (label.isBlank())
		{
			return;
		}

		final var point = npc.getCanvasTextLocation(graphics, label, npc.getLogicalHeight() + 40);
		if (point == null)
		{
			return;
		}

		final int x = point.getX();
		final int y = point.getY();

		graphics.setColor(Color.BLACK);
		graphics.drawString(label, x + 1, y + 1);
		graphics.drawString(label, x + 2, y + 2);

		graphics.setColor(color);
		graphics.drawString(label, x, y);
	}

	private static Color darken(final Color color, final double factor)
	{
		return new Color(
			Math.max((int) (color.getRed() * factor), 0),
			Math.max((int) (color.getGreen() * factor), 0),
			Math.max((int) (color.getBlue() * factor), 0),
			color.getAlpha()
		);
	}
}
