package com.bgsoftware.superiorskyblock.sync.handler.create;

import com.bgsoftware.superiorskyblock.menu.MenuIslandCreation;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.util.ConcurrentUtils;
import org.jetbrains.annotations.NotNull;

public class OpenIslandCreationMenu implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.COMMON;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.OPEN_ISLAND_CREATION_MENU;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);

        PlayerUtils.getPlayer(uuid).ifPresent(player -> { // check if online
            var islandName = data.readString("island-name");
            var superior = getPlugin().getPlayers().getSuperiorPlayer(player);

            ConcurrentUtils.ensureMain(() -> {
                MenuIslandCreation.openInventory(superior, null, islandName);
            });
        });
    }
}
