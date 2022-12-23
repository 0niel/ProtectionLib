package de.Ste3et_C0st.ProtectionLib.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.ProtectionLib.exception.ProtectionCreateException;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fBentobox;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fDiceChunk;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fFabledSkyblock;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fFactionsUUID;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fGriefPrevention;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fGriefdefenderAPI;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fKingdoms;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fLandLord;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fLands;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fSuperiorSkyblock;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fTowny;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fWorldGuardv7;
import de.Ste3et_C0st.ProtectionLib.main.plugins.fuSkyblock;
import de.Ste3et_C0st.ProtectionLib.main.utils.Metrics;
import de.Ste3et_C0st.ProtectionLib.main.utils.Version;

public class ProtectionLib extends JavaPlugin{
	
	private List<ProtectionClass> protectList = new ArrayList<ProtectionClass>();
	private static ProtectionLib instance;
	public static ProtectionLib getInstance(){return instance;}
	
	private boolean isVaultEnable = false;
	private List<protectionObj> protectionClass = new ArrayList<protectionObj>();
	private ProtectionVaultPermission permissions = null;
	private List<UUID> playerList = new ArrayList<UUID>();
	private boolean metrics = true;
	private final static int plugin_metricsID = 11939;
	
	@Override
	public void onEnable(){
		instance = this;
		if(Bukkit.getPluginManager().isPluginEnabled("Vault")){
			isVaultEnable = true;
			permissions = new ProtectionVaultPermission();
		}
		getCommand("protectionlib").setExecutor(new command());
		addWatchers();
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		this.metrics = this.getConfig().getBoolean("config.metrics", true);
		
		if(this.metrics) {
			Metrics metrics = new Metrics(this, plugin_metricsID);
			metrics.addCustomChart(new Metrics.AdvancedPie("protectionlib_hooked_plugins", () -> {
				Map<String, Integer> map = new HashMap<>();
				
				this.protectionClass.stream().filter(protectionObj::isEnabled).forEach(entry -> {
					String name = entry.getClass().getSimpleName().replace(".java", "").replace(".class", "");
					map.put(name.substring(1), 1);
				});
				
				return map;
			}));
		}
	}
	
	public void onDisable(){
		instance = null;
	}
	
	public void addWatchers() {
		this.hookIntoPlugins();
		protectionClass.stream().forEach(entry -> entry.update());
	}
	
	private void hookIntoPlugins() {
		HashMap<Class<? extends protectionObj>, ProtectionPluginFilter> protectionClasses = generatePluginMap();
		
		protectionClasses.entrySet().stream().filter(entry -> entry.getValue().match()).forEach(entry -> {
			try {
				final String pluginName = entry.getValue().getPluginName();
				final ProtectionClass ppL = new ProtectionClass(pluginName);
				final protectionObj object = entry.getKey().getDeclaredConstructor(Plugin.class).newInstance(Bukkit.getPluginManager().getPlugin(pluginName));
				this.protectionClass.add(object);
				this.protectList.add(ppL);
			}catch (Exception e) {
				getLogger().info("ProtectionClass: " + entry.getKey().getSimpleName() + " throw an Exception!");
				e.printStackTrace();
			}
		});
		
	}
	
	private HashMap<Class<? extends protectionObj>, ProtectionPluginFilter> generatePluginMap() {
		HashMap<Class<? extends protectionObj>, ProtectionPluginFilter> protectetionMap = new HashMap<>();
		protectetionMap.put(fBentobox.class, new ProtectionPluginFilter("BentoBox"));
		protectetionMap.put(fDiceChunk.class, new ProtectionPluginFilter("DiceChunk"));
		protectetionMap.put(fFabledSkyblock.class, new ProtectionPluginFilter("FabledSkyblock"));
		protectetionMap.put(fFactionsUUID.class, new ProtectionPluginFilter("Factions").containsAuthor("mbaxter"));
		protectetionMap.put(fGriefdefenderAPI.class, new ProtectionPluginFilter("GriefDefender"));
		protectetionMap.put(fGriefPrevention.class, new ProtectionPluginFilter("GriefPrevention"));
		protectetionMap.put(fKingdoms.class, new ProtectionPluginFilter("Kingdoms"));
		protectetionMap.put(fLandLord.class, new ProtectionPluginFilter("LandLord"));
		protectetionMap.put(fSuperiorSkyblock.class, new ProtectionPluginFilter("SuperiorSkyblock2"));
		protectetionMap.put(fLands.class, new ProtectionPluginFilter("Lands"));
		protectetionMap.put(fTowny.class, new ProtectionPluginFilter("Towny"));
		protectetionMap.put(fuSkyblock.class, new ProtectionPluginFilter("uSkyblock"));
		protectetionMap.put(fWorldGuardv7.class, new ProtectionPluginFilter("WorldGuard").isVersion(7));

		try {
			this.registerModules("LocalLibary", protectetionMap);
			this.registerModules("PlotSquaredV3Module", protectetionMap);
			this.registerModules("PlotSquaredV4Module", protectetionMap);
			
			if(new Version(System.getProperty("java.version").split("_")[0]).compareTo(new Version("17.0")) >= 0) {
				this.registerModules("PlotSquaredV6Module", protectetionMap);
			}else {
				this.getLogger().info("ProtectionLib-Module: PlotSquaredV6Module can't initialized unsuported java version!");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return protectetionMap;
	}
	
	private void registerModules(String modul, HashMap<Class<? extends protectionObj>, ProtectionPluginFilter> hashMap) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String name = "de.Ste3et_C0st.ProtectionLib.main.module." + modul;
		Class<?> clazz = Class.forName(name);
		if(Objects.isNull(clazz)) {
			this.getLogger().info("ProtectionLib-Module: " + modul + " not found!");
			return;
		}
		
		ProtectionModule protectionModule = (ProtectionModule) clazz.newInstance();
		hashMap.putAll(protectionModule.generatePluginMap());
		this.getLogger().info("ProtectionLib-Module: " + modul + " hooked!");
	}
	
	public void addPrivateProtectionPlugin(String pluginName, protectionObj protectionClass) {
		ProtectionClass ppL = new ProtectionClass(pluginName);
		if(!this.protectList.contains(ppL)) {
			this.protectList.add(ppL);
			if(ppL.isLoaded()) {
				if(!this.protectionClass.contains(protectionClass)) {
					this.protectionClass.add(protectionClass);
				}else {
					try {
						throw(new ProtectionCreateException("", null));
					} catch (ProtectionCreateException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void clearWatchers() {
		HandlerList.unregisterAll(getInstance());
		this.protectionClass.clear();
		this.protectList.clear();
	}
	
	public boolean hasPermissions(Player p){
		if(p.isOp()) return true;
		return isVaultEnable ? permissions.permission.has(p, "ProtectionLib.admin") : p.hasPermission("ProtectionLib.admin");
	}
	
	public boolean toogleDebug(UUID uuid) {
		if(this.playerList.contains(uuid)) {
			this.playerList.remove(uuid);
			return false;
		}else {
			this.playerList.add(uuid);
			return true;
		}
	}
	
	public List<protectionObj> getWatchers(){
		return this.protectionClass;
	}
	
	public boolean canBuild(Location loc, Player player){
		if(hasPermissions(player)) return true;
		return canBuild(loc, player, player);
	}
	
	public boolean canBuild(Location loc, Player player, Player sender){
		if(playerList.contains(sender.getUniqueId())) {
			if(getWatchers().isEmpty()) {
				player.sendMessage("§c§lProtectionLib is not hooked to any Plugin !");
			}else {
				protectionClass.stream().forEach(protection -> {
					if(protection.isEnabled()) {
						player.sendMessage("§f[§6canBuild§f->§a"+ protection.getClass().getSimpleName()+"§f] " + protection.getPlugin().getName() + ": " + protection.canBuild(player, loc));
						getLogger().log(Level.INFO, "ProtectionLib canBuild->" + protection.getClass().getSimpleName() + ": " + protection.canBuild(player, loc) + " for " + player.getName());
					}else {
						player.sendMessage("§f[§6canBuild§f->§c"+ protection.getClass().getSimpleName()+"§f] " + protection.getPlugin().getName() + ": §cdisabled");
					}
				});
			}
		}
		return !this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.canBuild(player, loc) == false).findFirst().isPresent();
	}
	
	public boolean isOwner(Location loc, Player player){
		if(hasPermissions(player)) return true;
		return isOwner(loc, player, player);
	}
	
	public boolean isOwner(Location loc, Player player, Player sender){
		if(playerList.contains(sender.getUniqueId())) {
			if(getWatchers().isEmpty()) {
				player.sendMessage("§c§lProtectionLib is not hooked to any Plugin !");
			}else {
				protectionClass.stream().forEach(protection -> {
					if(protection.isEnabled()) {
						sender.sendMessage("§f[§6isOwner§f->§a"+protection.getClass().getSimpleName()+"§f] " +protection.getPlugin().getName() + ": " + protection.isOwner(player, loc));
						getLogger().log(Level.INFO, "ProtectionLib canBuild->" + protection.getClass().getSimpleName() + ": " + protection.isOwner(player, loc) + " for " + player.getName());
					}else {
						sender.sendMessage("§f[§6isOwner§f->§c"+protection.getClass().getSimpleName()+"§f] " +protection.getPlugin().getName() + ": §cdisabled");
					}
				});
			}
		}
		return !this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.isOwner(player, loc) == false).findFirst().isPresent();
	}
	
	public boolean isProtectedRegion(Location location) {
		return this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.isProtectedRegion(location)).findFirst().isPresent();
	}
	
	public boolean registerFlag(Plugin plugin, String str, boolean defaultValue) {
		if(getWatchers().isEmpty()) {
			this.getLogger().warning("ProtectionLib is not hooked to any Plugin !");
		    return false;
		}else {
			AtomicBoolean feedback = new AtomicBoolean(false);
			protectionClass.stream().forEach(protection -> {
				boolean b = protection.registerFlag(plugin, str, defaultValue);
				if(b) {
					feedback.set(true);
					this.getLogger().info("ProtectionLib: " + protection.getPlugin().getName() + " register Customflag " + str + " by " + plugin.getName());
				}
			});
			return feedback.get();
		}
	}
	
	public List<UUID> getDebugList(){
		return this.playerList;
	}
	
	public boolean queryFlag(String string, Player player, Location location) {
		return !this.protectionClass.stream().filter(protection -> protection.queryFlag(string, player, location) == false).findFirst().isPresent();
	}
}
