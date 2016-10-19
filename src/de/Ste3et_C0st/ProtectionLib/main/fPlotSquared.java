package de.Ste3et_C0st.ProtectionLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.plotsquared.bukkit.events.PlotClearEvent;

public class fPlotSquared extends ProtectinObj implements Listener{
	Player p;
	Location loc;
	
	public fPlotSquared(Plugin pl){
		super(pl);
		Bukkit.getPluginManager().registerEvents(this, ProtectionLib.getInstance());
	}
	
	@EventHandler
	private void onClear(PlotClearEvent e){
		RegionWrapper reg = e.getPlot().getArea().getRegion();
		World world = Bukkit.getWorld(e.getWorld());
		Location loc1 = new Location(world, reg.maxX, reg.maxY, reg.maxZ);
		Location loc2 = new Location(world, reg.minX, reg.minY, reg.minZ);
		RegionClearEvent event = new RegionClearEvent(loc1, loc2);
		Bukkit.getPluginManager().callEvent(event);
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
	
	@SuppressWarnings("deprecation")
	private boolean canBuild(Plugin p){
		if(p==null){return true;}
		PlotAPI pAPI = new PlotAPI(ProtectionLib.getInstance());
		if(pAPI.isPlotWorld(this.loc.getWorld())){
			com.intellectualcrafters.plot.object.Location loc = new com.intellectualcrafters.plot.object.Location(this.loc.getWorld().getName(), 
					(int) this.loc.getX(), 
					(int) this.loc.getY(), 
					(int) this.loc.getZ(), 
					this.loc.getYaw(), 
					this.loc.getPitch());
			if(loc.isPlotArea()){
				Plot plot = pAPI.getPlot(this.loc);
				if(plot!=null){
					if(plot.isAdded(this.p.getUniqueId())) return true;
					if(plot.isOwner(this.p.getUniqueId())) return true;
				}
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean isOwner(Plugin p){
		if(p==null){return true;}
		PlotAPI pAPI = new PlotAPI(ProtectionLib.getInstance());
		if(pAPI.isPlotWorld(this.loc.getWorld())){
			com.intellectualcrafters.plot.object.Location loc = new com.intellectualcrafters.plot.object.Location(this.loc.getWorld().getName(), 
					(int) this.loc.getX(), 
					(int) this.loc.getY(), 
					(int) this.loc.getZ(), 
					this.loc.getYaw(), 
					this.loc.getPitch());
			if(loc.isPlotArea()){
				Plot plot = pAPI.getPlot(this.loc);
				if(plot!=null) if(plot.isOwner(this.p.getUniqueId())) return true;
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
}
