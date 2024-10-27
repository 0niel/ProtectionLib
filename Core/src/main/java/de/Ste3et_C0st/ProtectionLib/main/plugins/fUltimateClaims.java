package de.Ste3et_C0st.ProtectionLib.main.plugins;

import com.craftaro.ultimateclaims.UltimateClaims;
import com.craftaro.ultimateclaims.claim.Claim;
import com.craftaro.ultimateclaims.claim.ClaimManager;
import com.craftaro.ultimateclaims.member.ClaimPerm;
import de.Ste3et_C0st.ProtectionLib.main.protectionObj;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class fUltimateClaims extends protectionObj {

    private final ClaimManager claimManager;

    public fUltimateClaims(Plugin plugin) {
        super(plugin);
        UltimateClaims ultimateClaims = (UltimateClaims) plugin.getServer().getPluginManager().getPlugin("UltimateClaims");
        this.claimManager = ultimateClaims != null ? ultimateClaims.getClaimManager() : null;
    }

    public boolean canBuild(Player player, Location loc) {
        if (claimManager == null) return true;
        Claim claim = claimManager.getClaim(loc.getChunk());
        if (claim == null) return true;

        if (player.isOp()) return true;

        return claim.playerHasPerms(player, ClaimPerm.PLACE);
    }

    public boolean isOwner(Player player, Location loc) {
        if (claimManager == null) return true;
        Claim claim = claimManager.getClaim(loc.getChunk());
        if (claim == null) return false;

        // Check if the player is the owner of the claim
        return claim.getOwner().getPlayer().getUniqueId().equals(player.getUniqueId());

    }

    public boolean isProtectedRegion(Location location) {
        if (claimManager == null) return false;
        return Objects.nonNull(claimManager.getClaim(location.getChunk()));
    }
}
