package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.island.data.SPlayerDataHandler;
import com.bgsoftware.superiorskyblock.sync.MessageConsumers;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.utils.commands.CommandArguments;
import com.bgsoftware.superiorskyblock.utils.threads.Executor;
import com.bgsoftware.superiorskyblock.Locale;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.lib.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CmdTeleport implements ISuperiorCommand {

    @Override
    public List<String> getAliases(){
        return Arrays.asList("tp", "teleport", "go", "home");
    }

    @Override
    public String getPermission() {
        return "superior.island.teleport";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "teleport";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_TELEPORT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (SuperiorSkyblockPlugin.isClient) {
            var data = DataBuffer.create()
                    .write("uuid", ((Player) sender).getUniqueId().toString());

            plugin.getLibrary().getMessageBus().fire(plugin, MessageType.ISLAND_TELEPORT_REQUEST, data);
            return;
        }

        executeOnServer(plugin.getPlayers().getSuperiorPlayer(sender));
    }

    public static void executeOnServer(@NotNull SuperiorPlayer player) {
        var plugin = SuperiorSkyblockPlugin.INSTANCE;
        Pair<Island, SuperiorPlayer> arguments = CommandArguments.getPlayerIsland(plugin, player);

        Island island = arguments.getKey();

        if(island == null)
            return;

        /*
        if(plugin.getSettings().homeWarmup > 0 && !superiorPlayer.hasBypassModeEnabled() && !superiorPlayer.hasPermission("superior.admin.bypass.warmup")) {
            Locale.TELEPORT_WARMUP.send(superiorPlayer, StringUtils.formatTime(superiorPlayer.getUserLocale(), plugin.getSettings().homeWarmup));
            ((SPlayerDataHandler) superiorPlayer.getDataHandler()).setTeleportTask(Executor.sync(() ->
                    teleportToIsland(superiorPlayer, island), plugin.getSettings().homeWarmup / 50));
        }
        else {
         */
        if (player.isOnline()) {
            teleportToIsland(player, island);
            return;
        }

        var data = DataBuffer.create()
                .write("uuid", player.getUniqueId().toString());

        plugin.getLibrary().getMessageBus().fire(plugin, MessageType.ISLAND_TELEPORT, data);
        //     teleportToIsland(superiorPlayer, island);
        // }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public static void teleportToIsland(SuperiorPlayer superiorPlayer, Island island){
        ((SPlayerDataHandler) superiorPlayer.getDataHandler()).setTeleportTask(null);
        superiorPlayer.teleport(island, result -> {
            if(result)
                Locale.TELEPORTED_SUCCESS.send(superiorPlayer);
            else
                Locale.TELEPORTED_FAILED.send(superiorPlayer);
        });
    }

}
