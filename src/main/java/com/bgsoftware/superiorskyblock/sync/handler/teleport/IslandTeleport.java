package com.bgsoftware.superiorskyblock.sync.handler.teleport;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.player.SSuperiorPlayer;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.ServerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.schedule.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IslandTeleport implements Handler, Listener {

    private final Map<UUID, Island> teleportQueue;

    public IslandTeleport() {
        this.teleportQueue = new ConcurrentHashMap<>();
    }

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.COMMON;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.ISLAND_TELEPORT;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);
        var islandId = UUID.fromString(data.readString("island-uuid"));

        if (isClient()) {
            PlayerUtils.getPlayer(uuid).ifPresent(player -> {
                if (isClient()) {
                    ServerUtils.sendPlayerToServer(player, ServerUtils.getMainServerId());
                }
            });

        } else {
            var island = getPlugin().getGrid().getIslandByUUID(islandId);
            teleportQueue.put(uuid, island);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (isClient()) {
            return;
        }

        Task.builder().delay(1L).execute(() -> {
            var uuid = event.getPlayer().getUniqueId();

            if (teleportQueue.containsKey(uuid)) {
                PlayerUtils.getSuperiorPlayer(uuid).ifPresent(player -> {
                    ((SSuperiorPlayer) player).teleportOnServer(teleportQueue.get(uuid), result -> {
                    });
                });

                teleportQueue.remove(uuid);
            }
        }).schedule(getPlugin());
    }
}
