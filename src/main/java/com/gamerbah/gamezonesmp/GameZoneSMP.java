package com.gamerbah.gamezonesmp;

import com.gamerbah.gamezonesmp.command.*;
import com.gamerbah.gamezonesmp.command.administration.*;
import com.gamerbah.gamezonesmp.event.*;
import com.gamerbah.gamezonesmp.event.player.*;
import com.gamerbah.gamezonesmp.util.ChunkLoader;
import com.gamerbah.gamezonesmp.util.DeathChest;
import com.gamerbah.gamezonesmp.util.integration.DiscordSRVListener;
import com.gamerbah.gamezonesmp.util.integration.twitch.TwitchBot;
import com.gamerbah.gamezonesmp.util.integration.twitch.TwitchIntegration;
import com.gamerbah.gamezonesmp.util.manager.DataManager;
import com.gamerbah.gamezonesmp.util.manager.GameProfileManager;
import com.gamerbah.gamezonesmp.util.manager.ScoreboardManager;
import com.gamerbah.gamezonesmp.util.manager.UpdateManager;
import com.gamerbah.gamezonesmp.util.message.M;
import com.gamerbah.gamezonesmp.util.shop.ShopManager;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import com.gamerbah.gamezonesmp.util.task.player.SleepTimer;
import com.gamerbah.gamezonesmp.util.task.server.ChunkRandomTick;
import com.gamerbah.gamezonesmp.util.task.server.UpdateRunnable;
import com.google.common.base.Charsets;
import github.scarsz.discordsrv.DiscordSRV;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public final class GameZoneSMP extends JavaPlugin {

	@Getter
	private static GameZoneSMP instance;

	@Getter
	private DiscordSRVListener discordListener;

	@Getter
	private final HashSet<UUID>           afk                = new HashSet<>();
	@Getter
	private final HashSet<UUID>           inventoryCheck     = new HashSet<>();
	@Getter
	private final HashSet<UUID>           creative           = new HashSet<>();
	@Getter
	private final HashSet<UUID>           frozenPlayers      = new HashSet<>();
	@Getter
	private final HashSet<String>         registeredCommands = new HashSet<>();
	@Getter
	private final HashMap<UUID, UUID>     messaging          = new HashMap<>();
	@Getter
	private final HashMap<UUID, UUID>     teleportRequests   = new HashMap<>();
	@Getter
	private final HashMap<Integer, UUID>  discordLink        = new HashMap<>();
	@Getter
	private final HashMap<UUID, Integer>  linkMap            = new HashMap<>();
	@Getter
	private final HashMap<UUID, Integer>  loaderCount        = new HashMap<>();
	@Getter
	private final HashMap<UUID, GameMode> deaths             = new HashMap<>();
	@Getter
	private final List<UUID>              respawning         = new ArrayList<>();
	@Getter
	private final List<UUID>              pvp                = new ArrayList<>();
	@Getter
	private final List<DeathChest>        deathChests        = new ArrayList<>();
	@Getter
	private final List<ChunkLoader>       chunkLoaders       = new ArrayList<>();

	@Getter
	private final ConcurrentHashMap<UUID, Integer> afkTimer = new ConcurrentHashMap<>();

	@Getter
	private final HashMap<UUID, String> twitchWatchers = new HashMap<>();

	@Getter
	private ExecutorService threadPool;

	@Getter
	@Setter
	private DataManager        dataManager;
	@Getter
	private GameProfileManager profileManager;
	@Getter
	private UpdateManager      updateManager;
	@Getter
	private ScoreboardManager  scoreboardManager;
	@Getter
	private ShopManager        shopManager;

	@Getter
	private TwitchIntegration twitchIntegration;

	private int        updateRunnable;
	private int        sleepTimer;
	private BukkitTask chunkTicker;

	@Getter
	@Setter
	private boolean frozen   = false;
	@Getter
	@Setter
	private boolean silenced = false;
	@Getter
	@Setter
	private boolean devMode  = false;

	@Setter
	@Getter
	private int sleeping = 0;

	private File              creativeDataFile;
	private File              survivalDataFile;
	private File              pluginMessagesFile;
	private File              serverShopsFile;
	@Getter
	private FileConfiguration creativeData;
	@Getter
	private FileConfiguration survivalData;
	@Getter
	private FileConfiguration pluginMessages;
	@Getter
	private FileConfiguration serverShops;

	@Override
	public void onEnable() {
		instance = this;

		createConfigs();
		devMode = getConfig().getBoolean("developmentMode");

		threadPool = Executors.newFixedThreadPool(5);

		try {
			if (!devMode) discordListener = new DiscordSRVListener(this);
		} catch (Throwable e) {
			M.log(Level.SEVERE, "Unable to start DiscordSRVListener!");
		}

		dataManager = new DataManager(this);
		profileManager = new GameProfileManager(this);
		updateManager = new UpdateManager(this);
		scoreboardManager = new ScoreboardManager(this);
		shopManager = new ShopManager(this);

		twitchIntegration = new TwitchIntegration(this);

		registerCommands();
		registerListeners();
		startTaskTimers();
		createRecipes();

		profileManager.getProfiles().clear();

		loadConfigData();
		shopManager.setupServerShops();

		getServer().getOnlinePlayers().forEach(player -> {
			getAfkTimer().put(player.getUniqueId(), 0);
			if (player.getGameMode() == GameMode.CREATIVE) {
				creative.add(player.getUniqueId());
			}
			scoreboardManager.assign(player);
		});

		if (!devMode) DiscordSRV.api.subscribe(discordListener);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTask(updateRunnable);
		getServer().getScheduler().cancelTask(sleepTimer);
		if (chunkTicker != null) chunkTicker.cancel();

		if (!devMode) DiscordSRV.api.unsubscribe(discordListener);

		profileManager.getProfiles().clear();

		twitchIntegration.getBots().forEach(TwitchBot::stop);

		saveConfigData();

		if (!dataManager.getDataSource().isClosed()) {
			dataManager.getDataSource().close();
		}

	}

	@SuppressWarnings("ConstantConditions")
	private void registerCommands() {
		getCommand("afk").setExecutor(new AFKCommand(this));
		getCommand("message").setExecutor(new MessageCommand(this));
		getCommand("ping").setExecutor(new PingCommand(this));
		getCommand("reply").setExecutor(new ReplyCommand(this));
		getCommand("home").setExecutor(new HomeCommand());
		getCommand("help").setExecutor(new HelpCommand());
		getCommand("rules").setExecutor(new RulesCommand());
		getCommand("checkinventory").setExecutor(new CheckInventoryCommand(this));
		getCommand("world").setExecutor(new WorldCommand(this));
		getCommand("pvp").setExecutor(new PvpCommand(this));
		getCommand("rank").setExecutor(new RankCommand(this));
		getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
		getCommand("freeze").setExecutor(new FreezeCommand(this));
		getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
		getCommand("setlocation").setExecutor(new SetLocationCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
		getCommand("gamemode").setExecutor(new GameModeCommand(this));
		getCommand("twitch").setExecutor(new TwitchCommand(this));
		getCommand("smite").setExecutor(new SmiteCommand(this));
		getCommand("slimechunks").setExecutor(new SlimeChunksCommand(this));
		getCommand("chat").setExecutor(new ChatCommand(this));
		getCommand("reload").setExecutor(new ReloadCommand(this));
		getCommand("vanish").setExecutor(new VanishCommand(this));
		getCommand("openinv").setExecutor(new OpenInvCommand(this));
		getCommand("shop").setExecutor(new ShopCommand(this));

	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
		getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
		getServer().getPluginManager().registerEvents(new PlayerChat(this), this);
		getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);
		getServer().getPluginManager().registerEvents(new PlayerDamageEvent(this), this);
		getServer().getPluginManager().registerEvents(new PlayerCommandPreProcess(this), this);
		getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
		getServer().getPluginManager().registerEvents(new PlayerSleep(this), this);
		getServer().getPluginManager().registerEvents(new VehicleEnter(), this);
		getServer().getPluginManager().registerEvents(new InventoryClick(this), this);
		getServer().getPluginManager().registerEvents(new InventoryClose(this), this);
		getServer().getPluginManager().registerEvents(new EntityTeleport(), this);
		getServer().getPluginManager().registerEvents(new ServerListPingListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerDamageByPlayer(this), this);
		getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
		getServer().getPluginManager().registerEvents(new EntityDamageEntity(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
		getServer().getPluginManager().registerEvents(new FreezeCommand(this), this);
	}

	private void createConfigs() {
		creativeDataFile = new File(getDataFolder(), "creativeData.yml");
		survivalDataFile = new File(getDataFolder(), "survivalData.yml");
		pluginMessagesFile = new File(getDataFolder(), "messages.yml");
		serverShopsFile = new File(getDataFolder(), "shops-server.yml");
		if (!creativeDataFile.exists()) {
			creativeDataFile.getParentFile().mkdirs();
			saveResource("creativeData.yml", false);
		}
		if (!survivalDataFile.exists()) {
			survivalDataFile.getParentFile().mkdirs();
			saveResource("survivalData.yml", false);
		}
		if (!pluginMessagesFile.exists()) {
			pluginMessagesFile.getParentFile().mkdirs();
			saveResource("messages.yml", false);
		}
		if (!serverShopsFile.exists()) {
			serverShopsFile.getParentFile().mkdirs();
			saveResource("shops-server.yml", false);
		}

		creativeData = new YamlConfiguration();
		survivalData = new YamlConfiguration();
		pluginMessages = new YamlConfiguration();
		serverShops = new YamlConfiguration();
		try {
			creativeData.load(creativeDataFile);
			survivalData.load(survivalDataFile);
			pluginMessages.load(pluginMessagesFile);
			serverShops.load(serverShopsFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void startTaskTimers() {
		getServer().getScheduler().runTaskTimer(this, new AFKRunnable(this), 100L, 20L);

		updateRunnable = getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateRunnable(this), 120, 120);
		sleepTimer = getServer().getScheduler().scheduleSyncRepeatingTask(this, new SleepTimer(this), 0, 1);

		chunkTicker = getServer().getScheduler().runTaskTimerAsynchronously(this, new ChunkRandomTick(this), 200L, 1L);
	}

	private void createRecipes() {
		ItemStack     loader         = ChunkLoader.getItemStack();
		NamespacedKey chunkLoaderKey = new NamespacedKey(this, "hybridsurvival/chunk_loader");
		if (getServer().getRecipe(chunkLoaderKey) == null) {
			ShapedRecipe recipe = new ShapedRecipe(chunkLoaderKey, loader);
			recipe.shape("OSO", "SDS", "OSO");
			recipe.setIngredient('O', Material.OBSIDIAN);
			recipe.setIngredient('S', Material.SMOOTH_STONE);
			recipe.setIngredient('D', Material.DIAMOND);
			getServer().addRecipe(recipe);
		}
	}

	private void loadConfigData() {
		for (int x = 0; x < getConfig().getInt("deathChests.size"); x++) {
			final String path = "deathChests." + x + ".";
			UUID         uuid = UUID.fromString(Objects.requireNonNull(getConfig().getString(path + "uuid")));

			String world  = getConfig().getString(path + "location.world");
			int    blockX = getConfig().getInt(path + "location.x");
			int    blockY = getConfig().getInt(path + "location.y");
			int    blockZ = getConfig().getInt(path + "location.z");
			assert world != null;
			Location        location   = new Location(getServer().getWorld(world), blockX, blockY, blockZ);
			int             experience = getConfig().getInt(path + "experience");
			List<ItemStack> drops      = new ArrayList<>();
			for (int y = 0; y < getConfig().getInt(path + "drops.size"); y++) {
				drops.add(getConfig().getItemStack(path + "drops." + x));
			}
			DeathChest deathChest = new DeathChest(this, uuid, location, drops, experience);
			deathChest.spawn();
			deathChests.add(deathChest);
		}

		for (int x = 0; x < getConfig().getInt("chunkLoaders.size"); x++) {
			final String path    = "chunkLoaders." + x + ".";
			UUID         uuid    = UUID.fromString(Objects.requireNonNull(getConfig().getString(path + "uuid")));
			boolean      visible = getConfig().getBoolean(path + "visible");
			String       world   = getConfig().getString(path + "world");
			int          blockX  = getConfig().getInt(path + "location.x");
			int          blockY  = getConfig().getInt(path + "location.y");
			int          blockZ  = getConfig().getInt(path + "location.z");
			int          chunkX  = getConfig().getInt(path + "chunk.x");
			int          chunkZ  = getConfig().getInt(path + "chunk.z");
			assert world != null;
			Location location = new Location(getServer().getWorld(world), blockX, blockY, blockZ);
			ChunkLoader loader = new ChunkLoader(this, uuid, location,
			                                     getServer().getWorld(world).getChunkAt(chunkX, chunkZ));
			loader.setTagVisible(visible);
			loader.create();
			chunkLoaders.add(loader);
			loaderCount.put(uuid, loaderCount.getOrDefault(uuid, 0) + 1);
		}

	}

	private void saveConfigData() {
		getConfig().set("deathChests", null);
		saveConfig();
		getConfig().set("deathChests.size", deathChests.size());
		for (int x = 0; x < deathChests.size(); x++) {
			DeathChest chest = deathChests.get(x);
			chest.serialize(x);
			chest.remove();
		}
		deathChests.clear();
		saveConfig();

		getConfig().set("chunkLoaders", null);
		saveConfig();
		getConfig().set("chunkLoaders.size", chunkLoaders.size());
		for (int x = 0; x < chunkLoaders.size(); x++) {
			ChunkLoader loader = chunkLoaders.get(x);
			loader.serialize(x);
			loader.remove();
		}
		chunkLoaders.clear();
		saveConfig();

		try {
			serverShops.set("shops", null);
			serverShops.save(serverShopsFile);
			serverShops.set("shops.size", shopManager.getServerShops().size());
			int i = 0;
			for (var shop : shopManager.getServerShops().values()) {
				shop.serialize(i++);
			}
			shopManager.getServerShops().clear();
			serverShops.save(serverShopsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void respawn(Player player, boolean allowBed) {
		var bed = player.getBedSpawnLocation();
		if (allowBed && bed != null && !bed.getBlock().getType().isSolid() &&
		    !bed.add(0, 1, 0).getBlock().getType().isSolid()) {
			if (!bed.getChunk().isLoaded()) {
				bed.getChunk().load();
			}
			player.teleport(bed, PlayerTeleportEvent.TeleportCause.COMMAND);
		} else {
			var spawn = getConfig().getLocation("locations.spawn");
			if (spawn != null) {
				if (!spawn.getChunk().isLoaded()) {
					spawn.getChunk().load();
				}
				player.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}
		if (deaths.containsKey(player.getUniqueId())) {
			player.setGameMode(deaths.remove(player.getUniqueId()));
		}
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();

		saveResource("messages.yml", false);
		pluginMessages.options().copyDefaults(true);
		pluginMessages = reloadCustomConfig("messages.yml", pluginMessagesFile, pluginMessages);

		saveResource("shops-server.yml", false);
		serverShops.options().copyDefaults(true);
		serverShops = reloadCustomConfig("shop-server.yml", serverShopsFile, serverShops);
	}

	private FileConfiguration reloadCustomConfig(String path, File file, FileConfiguration configuration) {
		configuration = YamlConfiguration.loadConfiguration(file);

		final InputStream defConfigStream = getResource(path);
		if (defConfigStream == null) {
			return configuration;
		}

		configuration.setDefaults(
				YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		return configuration;
	}

	public boolean loadSurvivalData(final Player player) {
		String path = "players." + player.getUniqueId();
		try {
			double health     = survivalData.getDouble(path + ".health");
			int    food       = survivalData.getInt(path + ".food");
			float  saturation = (float) survivalData.getDouble(path + ".saturation");
			int    level      = survivalData.getInt(path + ".level");
			float  experience = (float) survivalData.getDouble(path + ".experience");
			double absorption = survivalData.getDouble(path + ".absorption");
			int    heldSlot   = survivalData.getInt(path + ".heldSlot");

			for (int x = 0; x < survivalData.getInt(path + ".effects.size"); x++) {
				player.addPotionEffect((PotionEffect) survivalData.get(path + ".effects." + x));
			}

			for (int i = 0; i < 45; i++) {
				player.getInventory().setItem(i, (ItemStack) survivalData.get(path + ".inventory." + i));
			}
			player.getInventory().setItemInOffHand((ItemStack) survivalData.get(path + ".inventory.offhand"));

			player.setHealth(health);
			player.setFoodLevel(food);
			player.setSaturation(saturation);
			player.setLevel(level);
			player.setAbsorptionAmount(absorption);
			player.setExp(experience);
			player.getInventory().setHeldItemSlot(heldSlot);
			player.setAllowFlight(false);
			player.setFlying(false);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean storeSurvivalData(final Player player) {
		if (player.getGameMode() == GameMode.SURVIVAL) {
			String path = "players." + player.getUniqueId();
			survivalData.set(path + ".health", player.getHealth());
			survivalData.set(path + ".food", player.getFoodLevel());
			survivalData.set(path + ".saturation", player.getSaturation());
			survivalData.set(path + ".level", player.getLevel());
			survivalData.set(path + ".experience", player.getExp());
			survivalData.set(path + ".absorption", player.getAbsorptionAmount());
			survivalData.set(path + ".heldSlot", player.getInventory().getHeldItemSlot());

			survivalData.set(path + ".effects.size", player.getActivePotionEffects().size());
			int x = 0;
			for (PotionEffect effect : player.getActivePotionEffects()) {
				survivalData.set(path + ".effects." + x++, effect.serialize());
			}

			for (int i = 0; i < 45; i++) {
				ItemStack item = player.getInventory().getItem(i) == null
				                 ? new ItemStack(Material.AIR)
				                 : player.getInventory().getItem(i);
				survivalData.set(path + ".inventory." + i, item);
			}
			survivalData.set(path + ".inventory.offhand", player.getInventory().getItemInOffHand());

			try {
				survivalData.save(survivalDataFile);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Unable to save survival data for player => " + player.getName());
				return false;
			}
			return true;
		}
		return true;
	}

	public boolean loadCreativeData(final Player player) {
		String path = "players." + player.getUniqueId();
		if (creativeData.get(path) != null) {
			try {
				for (int i = 0; i < 45; i++) {
					player.getInventory().setItem(i, (ItemStack) creativeData.get(path + ".inventory." + i));
				}
				player.getInventory().setItemInOffHand((ItemStack) creativeData.get(path + ".inventory.offhand"));

				int heldSlot = creativeData.getInt(path + ".heldSlot");

				player.getInventory().setHeldItemSlot(heldSlot);
				player.setAllowFlight(true);
			} catch (Throwable e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return true;
	}

	public boolean storeCreativeData(final Player player) {
		String path = "players." + player.getUniqueId();

		for (int i = 0; i < 45; i++) {
			ItemStack item = player.getInventory().getItem(i) == null
			                 ? new ItemStack(Material.AIR)
			                 : player.getInventory().getItem(i);
			creativeData.set(path + ".inventory." + i, item);
		}
		creativeData.set(path + ".inventory.offhand", player.getInventory().getItemInOffHand());
		creativeData.set(path + ".heldSlot", player.getInventory().getHeldItemSlot());
		try {
			creativeData.save(creativeDataFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to save creative data for player => " + player.getName());
			return false;
		}
		return true;
	}

}
