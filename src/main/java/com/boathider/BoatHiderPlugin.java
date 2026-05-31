package com.boathider;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
import net.runelite.api.events.WorldEntityDespawned;
import net.runelite.api.events.WorldEntitySpawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.callback.ClientThread;
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
	private static final Set<String> CONFIG_KEYS_CACHED_OBJS = Set.of(
		BoatHiderConfig.CONFIG_KEY_HIDE_HULL,
		BoatHiderConfig.CONFIG_KEY_HIDE_KEEL,
		BoatHiderConfig.CONFIG_KEY_HIDE_TRIM,
		BoatHiderConfig.CONFIG_KEY_HIDE_SALVAGING_STATION,
		BoatHiderConfig.CONFIG_KEY_HIDE_CARGO_HOLD,
		BoatHiderConfig.CONFIG_KEY_HIDE_INOCULATION_STATION,
		BoatHiderConfig.CONFIG_KEY_HIDE_KEG,
		BoatHiderConfig.CONFIG_KEY_HIDE_CHUM_STATION,
		BoatHiderConfig.CONFIG_KEY_HIDE_RANGE,
		BoatHiderConfig.CONFIG_KEY_HIDE_OTHER
	);

	private static final int BOAT_CATEGORY = 2395;

	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private BoatHiderConfig config;
	@Inject
	private RenderCallbackManager renderCallbackManager;

	private final Map<Integer, WorldEntity> boats = new HashMap<>();

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
	private boolean showBallisticAttractor;
	private boolean showKeg;
	private boolean showTrawlingNet;
	private boolean showChumStation;
	private boolean showFathomStonePearl;
	private boolean showAnchor;
	private boolean showRange;
	private boolean showBosunsWorkbench;
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
		updateConfig(true);
		renderCallbackManager.register(this);
	}

	@Override
	public void shutDown()
	{
		renderCallbackManager.unregister(this);
		clientThread.invokeLater(() -> {
			invalidateZones();
			boats.clear();
		});
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged e)
	{
		if (e.getGroup().equals(BoatHiderConfig.CONFIG_GROUP))
		{
			updateConfig(CONFIG_KEYS_CACHED_OBJS.contains(e.getKey()));
		}
	}

	private void updateConfig(final boolean invalidate)
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
		showBallisticAttractor = !config.hideBallisticAttractor();
		showKeg = !config.hideKeg();
		showTrawlingNet = !config.hideTrawlingNet();
		showChumStation = !config.hideChumStation();
		showFathomStonePearl = !config.hideFathomStonePearl();
		showAnchor = !config.hideAnchor();
		showRange = !config.hideRange();
		showBosunsWorkbench = !config.hideBosunsWorkbench();
		showOtherPlayerBoat = !config.hideOtherPlayerBoat();
		showOther = !config.hideOther();

		if (invalidate)
		{
			clientThread.invokeLater(this::invalidateZones);
		}
	}

	@Subscribe
	public void onWorldEntitySpawned(final WorldEntitySpawned e)
	{
		final var we = e.getWorldEntity();
		if (we.getConfig().getCategory() == BOAT_CATEGORY)
		{
			boats.put(we.getWorldView().getId(), we);
		}
	}

	@Subscribe
	public void onWorldEntityDespawned(final WorldEntityDespawned e)
	{
		boats.remove(e.getWorldEntity().getWorldView().getId());
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

			if (BoatID.BOSUNS_WORKBENCH_IDS.contains(id))
			{
				return showBosunsWorkbench;
			}

			if (id == ObjectID.SAILING_FACILITY_RANGE)
			{
				return showRange;
			}

			if (id == ObjectID.SAILING_BALLISTIC_ATTRACTOR)
			{
				return showBallisticAttractor;
			}

			if (renderable instanceof DynamicObject || renderable instanceof Model)
			{
				return showOther;
			}
		}
		return true;
	}

	private void invalidateZones()
	{
		assert client.isClientThread();

		final var dc = client.getDrawCallbacks();
		if (dc == null)
		{
			return;
		}

		final var player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		final var wv = player.getWorldView();
		if (wv.isTopLevel())
		{
			return;
		}

		final var boat = boats.get(wv.getId());
		if (boat == null)
		{
			return;
		}

		final var scene = wv.getScene();

		switch (boat.getConfig().getId())
		{
			case 1: // raft
			case 2: // skiff
				dc.invalidateZone(scene, 0, 0);
				break;
			case 3: // sloop
				dc.invalidateZone(scene, 0, 0);
				dc.invalidateZone(scene, 0, 1);
				break;
			default:
				break;
		}
	}
}