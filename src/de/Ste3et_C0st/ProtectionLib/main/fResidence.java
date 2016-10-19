package de.Ste3et_C0st.ProtectionLib.main;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;

public class fResidence extends ProtectinObj {
	Player p;
	Location loc;
	
	public fResidence(Plugin pl){
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
	
	private boolean canBuild(Plugin p){
		if(p==null){return true;}
		ClaimedResidence residence = Residence.getResidenceManager().getByLoc(this.loc);
		if(residence==null) return true;
		if(residence.getOwner().equalsIgnoreCase(this.p.getName())) return true;
		FlagPermissions perms = Residence.getPermsByLocForPlayer(this.loc, this.p);
		boolean hasplace = perms.playerHas(this.p.getName(), 
		this.loc.getWorld().getName(), "place", perms.playerHas(this.p.getName(), this.loc.getWorld().getName(), "build", true));
		return hasplace;
	}
	
	private boolean isOwner(Plugin p){
		if(p==null){return true;}
		ClaimedResidence residence = Residence.getResidenceManager().getByLoc(this.loc);
		if(residence==null) return true;
		if(residence.getOwner().equalsIgnoreCase(this.p.getName())) return true;
		return false;
	}
}
