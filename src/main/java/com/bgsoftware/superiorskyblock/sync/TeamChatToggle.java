package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import net.spacedelta.lib.network.data.event.PlayerQuitNetworkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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
}
