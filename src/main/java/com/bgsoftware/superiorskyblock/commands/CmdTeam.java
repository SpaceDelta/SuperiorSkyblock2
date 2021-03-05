package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.island.SPlayerRole;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.utils.LocaleUtils;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.utils.commands.CommandArguments;
import com.bgsoftware.superiorskyblock.utils.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.utils.islands.SortingComparators;
import com.bgsoftware.superiorskyblock.utils.registry.Registry;
import com.bgsoftware.superiorskyblock.utils.threads.Executor;
import com.bgsoftware.superiorskyblock.Locale;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CmdTeam implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("team", "showteam", "online");
    }

    @Override
    public String getPermission() {
        return "superior.island.team";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "team [" +
                Locale.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Locale.COMMAND_ARGUMENT_ISLAND_NAME.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_TEAM.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        var arg = args.length == 1 ? "" : args[1];

        if (SuperiorSkyblockPlugin.isClient) {
            var data = DataBuffer.create()
                    .write("uuid", ((Player) sender).getUniqueId().toString())
                    .write("team", arg);

            plugin.getLibrary().getMessageBus().fire(plugin, MessageType.TEAM_COMMAND_REQUEST, data);
            return;
        }

        executeOnServer(plugin, plugin.getPlayers().getSuperiorPlayer(sender), arg);
    }

    public static void executeOnServer(SuperiorSkyblockPlugin plugin, SuperiorPlayer player, String argument) {
        Island island = (argument.isEmpty() ? CommandArguments.getPlayerIsland(plugin, player) :
                CommandArguments.getIsland(plugin, player, argument)).getKey();

        if(island == null)
            return;

        Executor.async(() -> {
            java.util.Locale locale = LocaleUtils.getLocale(player);
            StringBuilder infoMessage = new StringBuilder();

            if(!Locale.ISLAND_TEAM_STATUS_HEADER.isEmpty(locale))
                infoMessage.append(Locale.ISLAND_TEAM_STATUS_HEADER.getMessage(locale, island.getOwner().getName(),
                        island.getIslandMembers(true).size(), island.getTeamLimit())).append("\n");

            List<SuperiorPlayer> members = island.getIslandMembers(true);
            members.sort(SortingComparators.ISLAND_MEMBERS_COMPARATOR);

            if(!Locale.ISLAND_TEAM_STATUS_ROLES.isEmpty(locale)){
                Registry<PlayerRole, StringBuilder> rolesStrings = Registry.createRegistry();
                plugin.getPlayers().getRoles().stream().filter(PlayerRole::isRoleLadder)
                        .forEach(playerRole -> rolesStrings.add(playerRole, new StringBuilder()));
                rolesStrings.add(SPlayerRole.lastRole(), new StringBuilder());

                String onlineStatus = Locale.ISLAND_TEAM_STATUS_ONLINE.getMessage(locale),
                        offlineStatus = Locale.ISLAND_TEAM_STATUS_OFFLINE.getMessage(locale);

                members.forEach(islandMember -> {
                    PlayerRole playerRole = islandMember.getPlayerRole();
                    long time = islandMember.getLastTimeStatus() == -1 ? -1 : ((System.currentTimeMillis() / 1000) - islandMember.getLastTimeStatus()) * 1000;
                    boolean onlinePlayer = islandMember.isOnline() && !plugin.getProviders().isVanished(islandMember.asPlayer());
                    rolesStrings.get(playerRole).append(Locale.ISLAND_TEAM_STATUS_ROLES.getMessage(locale, playerRole,
                            islandMember.getName(), onlinePlayer ? onlineStatus : offlineStatus, StringUtils.formatTime(locale, time))).append("\n");
                });

                rolesStrings.keys().stream()
                        .sorted(Collections.reverseOrder(Comparator.comparingInt(PlayerRole::getWeight)))
                        .forEach(playerRole -> infoMessage.append(rolesStrings.get(playerRole)));

                rolesStrings.delete();
            }

            if(!Locale.ISLAND_TEAM_STATUS_FOOTER.isEmpty(locale))
                infoMessage.append(Locale.ISLAND_TEAM_STATUS_FOOTER.getMessage(locale));

            Locale.sendMessage(player, infoMessage.toString(), false);
        });
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getPlayerIslandsExceptSender(plugin, sender, args[1],
                plugin.getSettings().tabCompleteHideVanished) : new ArrayList<>();
    }

}
