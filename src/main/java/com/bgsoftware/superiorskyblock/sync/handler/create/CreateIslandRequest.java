package com.bgsoftware.superiorskyblock.sync.handler.create;

import com.bgsoftware.superiorskyblock.commands.CmdCreate;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

public class CreateIslandRequest implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.SERVER;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.CREATE_ISLAND_REQUEST;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);
        CmdCreate.executeOnServer(getPlugin(), uuid);
    }
}
