package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CmdTeamChat;
import com.bgsoftware.superiorskyblock.utils.islands.IslandUtils;
import net.spacedelta.lib.network.data.event.PlayerQuitNetworkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamChatToggle implements Listener {

    private static final SuperiorSkyblockPlugin PLUGIN = SuperiorSkyblockPlugin.INSTANCE;
    private static Map<UUID, Boolean> TOGGLES;

    public TeamChatToggle() {
        TOGGLES = new ConcurrentHashMap<>();
        PLUGIN.registerListeners(this);
        PLUGIN.getLibrary().getMessageBus().registerHandler(PLUGIN, MessageType.TEAM_CHAT_TOGGLE_SYNC, data -> {
            var uuid = UUID.fromString(data.readString("uuid"));
            var state = data.read("state", Boolean.class);

            TOGGLES.put(uuid, state);
        });
    }

    public static boolean isToggled(@NotNull UUID uuid) {
        return TOGGLES.getOrDefault(uuid, false);
    }

    @EventHandler
    public void onLeave(PlayerQuitNetworkEvent event) {
        TOGGLES.remove(event.getUuid());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAsyncChat(AsyncPlayerChatEvent e) {
        if (isToggled(e.getPlayer().getUniqueId())) {
            SuperiorPlayer superiorPlayer = PLUGIN.getPlayers().getSuperiorPlayer(e.getPlayer());
            Island island = superiorPlayer.getIsland();

            if (SuperiorSkyblockPlugin.isClient) {
                // presume no island ?
                // TODO if it just isn't cached yet will cause annoyance.
                if (IslandLevelCache.getLevel(e.getPlayer().getUniqueId()) == 0) {
                    TOGGLES.remove(e.getPlayer().getUniqueId());
                    return;
                }

                CmdTeamChat.broadcastTeamMessage(e.getPlayer().getUniqueId(), e.getMessage());
                e.setCancelled(true);
                return;
            }

            if (island == null) {
                superiorPlayer.toggleTeamChat();
                return;
            }

            e.setCancelled(true);
            IslandUtils.sendMessage(island, Locale.TEAM_CHAT_FORMAT, new ArrayList<>(), superiorPlayer.getPlayerRole(), superiorPlayer.getName(), e.getMessage());
            Locale.SPY_TEAM_CHAT_FORMAT.send(Bukkit.getConsoleSender(), superiorPlayer.getPlayerRole(), superiorPlayer.getName(), e.getMessage());
            for (Player _onlinePlayer : Bukkit.getOnlinePlayers()) {
                SuperiorPlayer onlinePlayer = PLUGIN.getPlayers().getSuperiorPlayer(_onlinePlayer);
                if (onlinePlayer.hasAdminSpyEnabled())
                    Locale.SPY_TEAM_CHAT_FORMAT.send(onlinePlayer, superiorPlayer.getPlayerRole(), superiorPlayer.getName(), e.getMessage());
            }
        }
    }

}
