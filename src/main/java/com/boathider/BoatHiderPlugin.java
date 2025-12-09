package com.boathider;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.Model;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.Scene;
import net.runelite.api.TileObject;
import net.runelite.api.WorldEntity;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Singleton
@PluginDescriptor(
	name = "Boat Hider",
	description = "Hide parts of the boat.",
	tags = {"sailing", "boat", "hider"}
)
public class BoatHiderPlugin extends Plugin implements RenderCallback
{
	@Inject
	private Client client;
	@Inject
	private BoatHiderConfig config;
	@Inject
	private RenderCallbackManager renderCallbackManager;

	private boolean showSail;
	private boolean showHull;
	private boolean showHelm;
	private boolean showKeel;
	private boolean showTrim;
	private boolean showWindGaleCatcher;
	private boolean showCrystalExtractor;
	private boolean showCrystalExtractorBar;
	private boolean showSalvagingStation;
	private boolean showCargoHold;
	private boolean showTeleportationFocus;
	private boolean showInoculationStation;
	private boolean showEternalBrazier;
	private boolean showFlag;
	private boolean showSalvagingHook;
	private boolean showCannon;
	private boolean showKeg;
	private boolean showTrawlingNet;
	private boolean showChumStation;
	private boolean showFathomStonePearl;
	private boolean showAnchor;
	private boolean showRange;
	private boolean showOtherPlayerBoat;
	private boolean showOther;

	@Provides
	BoatHiderConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(BoatHiderConfig.class);
	}

	@Override
	public void startUp()
	{
		updateConfig();
		renderCallbackManager.register(this);
	}

	@Override
	public void shutDown()
	{
		renderCallbackManager.unregister(this);
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (e.getGroup().equals(BoatHiderConfig.CONFIG_GROUP))
		{
			updateConfig();
		}
	}

	private void updateConfig()
	{
		showSail = !config.hideSail();
		showHull = !config.hideHull();
		showHelm = !config.hideHelm();
		showKeel = !config.hideKeel();
		showTrim = !config.hideTrim();
		showWindGaleCatcher = !config.hideWindGaleCatcher();
		showCrystalExtractor = !config.hideCrystalExtractor();
		showCrystalExtractorBar = !config.hideCrystalExtractorBar();
		showSalvagingStation = !config.hideSalvagingStation();
		showCargoHold = !config.hideCargoHold();
		showTeleportationFocus = !config.hideTeleportationFocus();
		showInoculationStation = !config.hideInoculationStation();
		showEternalBrazier = !config.hideEternalBrazier();
		showFlag = !config.hideFlag();
		showSalvagingHook = !config.hideSalvagingHook();
		showCannon = !config.hideCannon();
		showKeg = !config.hideKeg();
		showTrawlingNet = !config.hideTrawlingNet();
		showChumStation = !config.hideChumStation();
		showFathomStonePearl = !config.hideFathomStonePearl();
		showAnchor = !config.hideAnchor();
		showRange = !config.hideRange();
		showOtherPlayerBoat = !config.hideOtherPlayerBoat();
		showOther = !config.hideOther();
	}

	@Override
	public boolean addEntity(final Renderable renderable, final boolean ui)
	{
		if (renderable instanceof NPC)
		{
			if (((NPC) renderable).getId() == NpcID.CRYSTAL_EXTRACTOR_HEADBAR)
			{
				return showCrystalExtractorBar || !ui;
			}
		}

		return true;
	}

	@Override
	public boolean drawObject(final Scene scene, final TileObject o)
	{
		final var wv = o.getWorldView();
		if (wv == null || wv.isTopLevel())
		{
			return true;
		}

		final var entity = client.getTopLevelWorldView().worldEntities().byIndex(wv.getId());
		if (entity == null ||
			!BoatID.WORLD_ENTITY_TYPE_BOAT.contains(entity.getConfig().getId()))
		{
			return true;
		}

		final var type = entity.getOwnerType();
		if (type == WorldEntity.OWNER_TYPE_NOT_PLAYER)
		{
			// Always show npc boats
			return true;
		}

		if (o instanceof GameObject)
		{
			final var renderable = ((GameObject) o).getRenderable();

			if (type == WorldEntity.OWNER_TYPE_OTHER_PLAYER)
			{
				// Always show player actors
				return showOtherPlayerBoat || renderable instanceof Player;
			}

			final var id = o.getId();

			if (BoatID.SAIL_IDS.contains(id))
			{
				return showSail;
			}

			if (BoatID.HULL_IDS.contains(id))
			{
				return showHull;
			}

			if (BoatID.HELM_IDS.contains(id))
			{
				return showHelm;
			}

			if (BoatID.KEEL_IDS.contains(id))
			{
				return showKeel;
			}

			if (BoatID.TRIM_IDS.contains(id))
			{
				return showTrim;
			}

			if (BoatID.WIND_CATCHER_IDS.contains(id))
			{
				return showWindGaleCatcher;
			}

			if (BoatID.CRYSTAL_EXTRACTOR_IDS.contains(id))
			{
				return showCrystalExtractor;
			}

			if (BoatID.SALVAGING_STATION_IDS.contains(id))
			{
				return showSalvagingStation;
			}

			if (BoatID.CARGO_HOLD_IDS.contains(id))
			{
				return showCargoHold;
			}

			if (BoatID.TELEPORTATION_FOCUS_IDS.contains(id))
			{
				return showTeleportationFocus;
			}

			if (BoatID.INOCULATION_STATION_IDS.contains(id))
			{
				return showInoculationStation;
			}

			if (BoatID.ETERNAL_BRAZIER_IDS.contains(id))
			{
				return showEternalBrazier;
			}

			if (BoatID.FLAG_IDS.contains(id))
			{
				return showFlag;
			}

			if (BoatID.SALVAGING_HOOK_IDS.contains(id))
			{
				return showSalvagingHook;
			}

			if (BoatID.CANNON_IDS.contains(id))
			{
				return showCannon;
			}

			if (BoatID.KEG_IDS.contains(id))
			{
				return showKeg;
			}

			if (BoatID.TRAWLING_NET_IDS.contains(id))
			{
				return showTrawlingNet;
			}

			if (BoatID.CHUM_STATION_IDS.contains(id))
			{
				return showChumStation;
			}

			if (BoatID.FATHOM_STONE_PEARL_IDS.contains(id))
			{
				return showFathomStonePearl;
			}

			if (BoatID.ANCHOR_IDS.contains(id))
			{
				return showAnchor;
			}

			if (id == ObjectID.SAILING_FACILITY_RANGE)
			{
				return showRange;
			}

			if (renderable instanceof DynamicObject || renderable instanceof Model)
			{
				return showOther;
			}
		}
		return true;
	}
}