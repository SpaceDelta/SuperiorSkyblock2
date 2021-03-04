package com.bgsoftware.superiorskyblock.sync.handler.teleport;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.commands.CmdTeleport;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.ServerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import com.bgsoftware.superiorskyblock.utils.commands.CommandArguments;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.schedule.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IslandTeleport implements Handler, Listener {

    private final Queue<UUID> teleportQueue;

    public IslandTeleport() {
        this.teleportQueue = new ConcurrentLinkedQueue<>();
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

        PlayerUtils.getPlayer(uuid).ifPresent(player -> {
            if (isClient()) {
                ServerUtils.sendPlayerToServer(player.getName(), SuperiorSkyblockPlugin.MAIN_SERVER);
            } else {
                teleportQueue.add(player.getUniqueId());
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (isClient()) {
            return;
        }

        Task.builder().delay(10L).execute(() -> {
            var uuid = event.getPlayer().getUniqueId();

            if (teleportQueue.contains(uuid)) {
                PlayerUtils.getSuperiorPlayer(uuid).ifPresent(player -> {
                    CmdTeleport.teleportToIsland(player, CommandArguments.getPlayerIsland(getPlugin(), player).getKey());
                });

                teleportQueue.remove(uuid);
            }
        }).schedule(getPlugin());
    }
}
