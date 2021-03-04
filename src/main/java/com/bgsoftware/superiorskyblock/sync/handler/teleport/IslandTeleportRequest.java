package com.bgsoftware.superiorskyblock.sync.handler.teleport;

import com.bgsoftware.superiorskyblock.commands.CmdTeleport;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

public class IslandTeleportRequest implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.SERVER;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.ISLAND_TELEPORT_REQUEST;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);
        PlayerUtils.getSuperiorPlayer(uuid).ifPresent(CmdTeleport::executeOnServer);
    }
}
