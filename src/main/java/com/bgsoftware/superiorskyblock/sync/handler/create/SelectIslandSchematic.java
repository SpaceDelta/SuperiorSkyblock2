package com.bgsoftware.superiorskyblock.sync.handler.create;

import com.bgsoftware.superiorskyblock.menu.MenuIslandCreation;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.sync.PlayerUtils;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import com.google.gson.internal.LinkedTreeMap;
import net.spacedelta.lib.data.DataBuffer;
import org.jetbrains.annotations.NotNull;

public class SelectIslandSchematic implements Handler {

    @NotNull
    @Override
    public SupportedSide getSupportedSide() {
        return SupportedSide.SERVER;
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.SELECT_ISLAND_SCHEMATIC;
    }

    @Override
    public void handle(@NotNull DataBuffer data) {
        var uuid = PlayerUtils.readUuid(data);
        var schematic = data.readString("schematic");
        var islandName = data.readString("island-name");
        var rightClick = data.read("right-click", Boolean.class);
        // var menuData = data.read("menu-data", LinkedTreeMap.class); // stupid gson maps

        PlayerUtils.getSuperiorPlayer(uuid).ifPresent(player -> {
            MenuIslandCreation.clickSchematic(
                    schematic,
                    player,
                    islandName,
                    rightClick,
                    false,
                    null,
                    new LinkedTreeMap<>()
            );
        });
    }
}
