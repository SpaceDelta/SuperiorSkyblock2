package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.commands.CmdCreate;
import com.bgsoftware.superiorskyblock.menu.MenuIslandCreation;
import net.spacedelta.lib.message.MessageBus;
import net.spacedelta.lib.plugin.PluginSide;
import net.spacedelta.lib.util.ConcurrentUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class MessageConsumers {

    private final SuperiorSkyblockPlugin plugin;
    private final MessageBus bus;
    private final boolean isClient;

    public MessageConsumers(@NotNull SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
        this.bus = plugin.getLibrary().getMessageBus();
        this.isClient = plugin.getSide() == PluginSide.CLIENT;

        if (isClient) {
            registerClient();
        } else {
            registerServer();
        }

        registerCommon();
    }

    public void registerClient() {

    }

    public void registerServer() {
        // When a player requests island creation
        bus.registerHandler(plugin, MessageType.CREATE_ISLAND_REQUEST, data -> {
            CmdCreate.executeServer(plugin, data);
        });
    }

    public void registerCommon() {
        // Chat messages
        bus.registerHandler(plugin, MessageType.CHAT_MESSAGE, data -> {
            getPlayer(data.readString("uuid")).ifPresent(player -> {
                player.sendMessage(data.readString("message"));
            });
        });

        // Island creation schematic menu
        bus.registerHandler(plugin, MessageType.OPEN_ISLAND_CREATION_MENU, data -> {
            getPlayer(data.readString("uuid")).ifPresent(player -> {
                var islandName = data.readString("island-name");
                var sPlayer = plugin.getPlayers().getSuperiorPlayer(player);

                ConcurrentUtils.ensureMain(() -> {
                    MenuIslandCreation.openInventory(sPlayer, null, islandName);
                });
            });
        });
    }

    @NotNull
    private Optional<Player> getPlayer(@NotNull String uuidString) {
        var uuid = UUID.fromString(uuidString);
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
}
