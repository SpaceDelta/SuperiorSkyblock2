package com.bgsoftware.superiorskyblock.sync.handler.chat;

import com.bgsoftware.superiorskyblock.commands.CmdTeamChat;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

public class TeamChatMessage implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.COMMON;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.TEAM_CHAT_MESSAGE;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);

        PlayerUtils.getSuperiorPlayer(uuid).ifPresent(player -> {
            CmdTeamChat.sendMessageOnServer(player, data.readString("message"));
        });
    }
}
