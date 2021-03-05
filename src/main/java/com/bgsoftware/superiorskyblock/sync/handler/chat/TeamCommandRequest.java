package com.bgsoftware.superiorskyblock.sync.handler.chat;

import com.bgsoftware.superiorskyblock.commands.CmdTeam;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamCommandRequest implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.SERVER;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.TEAM_COMMAND_REQUEST;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);

        PlayerUtils.getSuperiorPlayer(uuid).ifPresent(player -> {
            var team = data.readString("team");
            CmdTeam.executeOnServer(getPlugin(), player, team);
        });
    }
}
