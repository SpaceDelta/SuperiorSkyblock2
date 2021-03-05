package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.sync.handler.ChatMessage;
import com.bgsoftware.superiorskyblock.sync.handler.Handler;
import com.bgsoftware.superiorskyblock.sync.handler.chat.TeamChatMessage;
import com.bgsoftware.superiorskyblock.sync.handler.chat.TeamChatToggle;
import com.bgsoftware.superiorskyblock.sync.handler.chat.TeamCommandRequest;
import com.bgsoftware.superiorskyblock.sync.handler.create.CreateIslandRequest;
import com.bgsoftware.superiorskyblock.sync.handler.create.OpenIslandCreationMenu;
import com.bgsoftware.superiorskyblock.sync.handler.create.SelectIslandSchematic;
import com.bgsoftware.superiorskyblock.sync.handler.teleport.IslandTeleport;
import com.bgsoftware.superiorskyblock.sync.handler.teleport.IslandTeleportRequest;
import net.spacedelta.lib.message.MessageBus;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class MessageConsumers implements Listener {

    private final SuperiorSkyblockPlugin plugin;
    private final MessageBus bus;

    public MessageConsumers(@NotNull SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
        this.bus = plugin.getLibrary().getMessageBus();

        register(
                new ChatMessage(),

                new CreateIslandRequest(),
                new OpenIslandCreationMenu(),
                new SelectIslandSchematic(),

                new IslandTeleportRequest(),
                new IslandTeleport(),

                new TeamChatMessage(),
                new TeamChatToggle(),
                new TeamCommandRequest()
        );
    }

    private void register(@NotNull Handler... handlers) {
        for (var handler : handlers) {
            var side = handler.getSupportedSide();

            if (side == Handler.SupportedSide.COMMON) {
                register(handler);
                continue;
            }

            // hacky and lazy but works so whatever ^w^
            if (side.name().equals(plugin.getSide().name())) {
                register(handler);
            }
        }
    }

    private void register(@NotNull Handler handler) {
        bus.registerHandler(plugin, handler.getMessageType(), handler);
        System.out.println("Registered sync handler: " + handler.getClass());

        if (handler instanceof Listener) {
            plugin.registerListeners((Listener) handler);
        }
    }
}
