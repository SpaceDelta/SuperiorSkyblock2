package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.utils.commands.CommandArguments;
import com.bgsoftware.superiorskyblock.utils.islands.IslandUtils;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class CmdTeamChat implements ISuperiorCommand {

    public static void toggleOnServer(@NotNull SuperiorPlayer player) {
        if (player.hasTeamChatEnabled()) {
            Locale.TOGGLED_TEAM_CHAT_OFF.send(player);
        } else {
            Locale.TOGGLED_TEAM_CHAT_ON.send(player);
        }

        player.toggleTeamChat();
    }

    public static void broadcastTeamMessage(@NotNull UUID uuid, @NotNull String message) {
        var data = DataBuffer.create()
                .write("uuid", uuid.toString())
                .write("message", message);

        SuperiorSkyblockPlugin.INSTANCE.getLibrary().getMessageBus().fire(SuperiorSkyblockPlugin.INSTANCE, MessageType.TEAM_CHAT_MESSAGE, data);
    }

    public static void sendMessageOnServer(@NotNull SuperiorPlayer player, @NotNull String message) {
        var island = player.getIsland();
        if (island == null)
            return;

        IslandUtils.sendMessage(island, Locale.TEAM_CHAT_FORMAT, new ArrayList<>(), player.getPlayerRole(), player.getName(), message);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("teamchat", "chat", "tc");
    }

    @Override
    public String getPermission() {
        return "superior.island.teamchat";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "teamchat [" + Locale.COMMAND_ARGUMENT_MESSAGE.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_TEAM_CHAT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (SuperiorSkyblockPlugin.isClient) {
            if (args.length == 1) {
                var data = DataBuffer.create()
                        .write("uuid", ((Player) sender).getUniqueId());

                plugin.getLibrary().getMessageBus().fire(plugin, MessageType.TEAM_CHAT_TOGGLE, data);
                return;
            }

            broadcastTeamMessage(
                    ((Player) sender).getUniqueId(),
                    CommandArguments.buildLongString(args, 1, true)
            );

            return;
        }

        Pair<Island, SuperiorPlayer> arguments = CommandArguments.getSenderIsland(plugin, sender);
        SuperiorPlayer superiorPlayer = arguments.getValue();

        if (args.length == 1) {
            toggleOnServer(superiorPlayer);
        } else {
            var message = CommandArguments.buildLongString(args, 1, true);
            sendMessageOnServer(superiorPlayer, message);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}
