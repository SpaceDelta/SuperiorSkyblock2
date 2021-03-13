package com.bgsoftware.superiorskyblock.sync.handler.accept;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.commands.CmdAccept;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IslandAccept implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.SERVER;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.ISLAND_ACCEPT_JOIN;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        PlayerUtils.getSuperiorPlayer(UUID.fromString(data.readString("uuid"))).ifPresent(player -> {
            var otherPlayer = data.readString("other-player");
            CmdAccept.executeOnServer(SuperiorSkyblockPlugin.INSTANCE, player, otherPlayer);
        });
    }
}
