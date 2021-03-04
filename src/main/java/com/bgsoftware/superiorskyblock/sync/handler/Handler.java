package com.bgsoftware.superiorskyblock.sync.handler;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import net.spacedelta.lib.message.MessageHandler;
import net.spacedelta.lib.plugin.PluginSide;
import org.jetbrains.annotations.NotNull;

public interface Handler extends MessageHandler {

    default SuperiorSkyblockPlugin getPlugin() {
        return SuperiorSkyblockPlugin.INSTANCE;
    }

    default boolean isClient() {
        return SuperiorSkyblockPlugin.INSTANCE.getSide() == PluginSide.CLIENT;
    }

    @NotNull
    SupportedSide getSupportedSide();

    @NotNull
    MessageType getMessageType();

    enum SupportedSide {

        CLIENT,
        SERVER,
        COMMON
    }
}
