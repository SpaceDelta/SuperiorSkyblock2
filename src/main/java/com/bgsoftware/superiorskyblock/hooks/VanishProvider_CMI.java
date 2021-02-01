package com.bgsoftware.superiorskyblock.hooks;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.listeners.PlayersListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class VanishProvider_CMI implements VanishProvider, Listener {

    private static boolean alreadyEnabled = false;

    private final SuperiorSkyblockPlugin plugin;

    public VanishProvider_CMI(SuperiorSkyblockPlugin plugin){
        this.plugin = plugin;

        if(false && !alreadyEnabled){
            alreadyEnabled = true;
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        SuperiorSkyblockPlugin.log("Hooked into CMI for support of vanish status of players.");
    }

    @Override
    public boolean isVanished(Player player) {
        // Start SpaceDelta
        return false; // CMI.getInstance().getVanishManager().getAllVanished().contains(player.getUniqueId());
    }

    /*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerVanish(CMIPlayerVanishEvent e){
        PlayersListener.handlePlayerQuit(plugin.getPlayers().getSuperiorPlayer(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerUnvanish(CMIPlayerUnVanishEvent e){
        PlayersListener.handlePlayerJoin(plugin.getPlayers().getSuperiorPlayer(e.getPlayer()));
    }
     */
    // End SpaceDelta

}
