package com.bgsoftware.superiorskyblock.sync.handler;

import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

public class ChatMessage implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.COMMON;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.CHAT_MESSAGE;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);

        PlayerUtils.getPlayer(uuid).ifPresent(player -> {
            player.sendMessage(data.readString("message"));
        });
    }
}
