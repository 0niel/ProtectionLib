package de.Ste3et_C0st.ProtectionLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.api.ILocation;
import com.worldcretornica.plotme_core.bukkit.api.BukkitLocation;
import com.worldcretornica.plotme_core.bukkit.event.PlotResetEvent;

public class fPlotMe extends ProtectinObj {
	Player p;
	Location loc;
	
	public fPlotMe(Plugin pl){
		super(pl);
	}
	
	public boolean canBuild(Plugin p, Player player, Location loc){
		this.p = player;
		this.loc = loc;
		return canBuild(p);
	}
	
	public boolean isOwner(Plugin p, Player player, Location loc){
		this.p = player;
		this.loc = loc;
		return isOwner(p);
	}
	
	@EventHandler
	private void onClear(PlotResetEvent e){
		Location loc1 = e.getLowerBound();
		Location loc2 = e.getUpperBound();
		RegionClearEvent event = new RegionClearEvent(loc1, loc2);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	private boolean canBuild(Plugin p){
		if(p==null){return true;}
		PlotMeCoreManager manager = PlotMeCoreManager.getInstance();
		ILocation loc = new BukkitLocation(this.loc);
		if(!manager.isPlotWorld(loc)){return true;}
		String id = manager.getPlotId(loc);
		if(id==null) return false;
		Plot plot = manager.getPlotById(id, loc.getWorld());
		if(plot==null) return false;
		if(plot.isAllowed(this.p.getUniqueId())) return true;
		if(plot.getOwnerId().equals(this.p.getUniqueId())) return true;
		return false;
	}
	
	private boolean isOwner(Plugin p){
		if(p==null){return true;}
		PlotMeCoreManager manager = PlotMeCoreManager.getInstance();
		ILocation loc = new BukkitLocation(this.loc);
		if(!manager.isPlotWorld(loc)){return true;}
		String id = manager.getPlotId(loc);
		if(id==null) return false;
		Plot plot = manager.getPlotById(id, loc.getWorld());
		if(plot==null) return false;
		if(plot.getOwnerId().equals(this.p.getUniqueId())) return true;
		return false;
	}
}
