package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import net.spacedelta.lib.network.data.event.PlayerQuitNetworkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IslandLevelCache implements Listener {

    private static final SuperiorSkyblockPlugin PLUGIN = SuperiorSkyblockPlugin.INSTANCE;
    private static Map<UUID, Long> LEVEL_CACHE;

    public IslandLevelCache() {
        LEVEL_CACHE = new ConcurrentHashMap<>();
        PLUGIN.registerListeners(this);
        PLUGIN.getLibrary().getMessageBus().registerHandler(PLUGIN, MessageType.ISLAND_LEVEL_SYNC, data -> {
            var uuid = UUID.fromString(data.readString("uuid"));
            var level = data.readNumber("level").longValue();

            LEVEL_CACHE.put(uuid, level);
        });
    }

    public static long getLevel(@NotNull UUID uuid) {
        return LEVEL_CACHE.getOrDefault(uuid, 0L);
    }

    @EventHandler
    public void onChangeLevel() {

    }

    @EventHandler
    public void onLeave(PlayerQuitNetworkEvent event) {
        LEVEL_CACHE.remove(event.getUuid());
    }
}
