package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.utils.StringUtils;
import com.bgsoftware.superiorskyblock.utils.commands.CommandTabCompletes;
import net.spacedelta.lib.Environment;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CmdAdminRecalc implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recalc", "recalculate", "level");
    }

    @Override
    public String getPermission() {
        return "superior.admin.recalc";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin recalc [" +
                Locale.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Locale.COMMAND_ARGUMENT_ISLAND_NAME.getMessage(locale) + "] [-f[orce] (if stuck)]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_ADMIN_RECALC.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (args.length == 2) {
            Locale.RECALC_ALL_ISLANDS.send(sender);

            if (SuperiorSkyblockPlugin.INSTANCE.getLibrary().getEnvironment().getStage() == Environment.Stage.DEVELOPMENT) {
                plugin.getGrid().calcAllIslands(() -> Locale.RECALC_ALL_ISLANDS_DONE.send(sender));
            } else
                sender.sendMessage(ChatColor.RED + "This command is disabled on production environments.");
        } else {
            SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(args[2]);
            Island island = targetPlayer == null ? plugin.getGrid().getIsland(args[2]) : targetPlayer.getIsland();

            if (island == null) {
                if (args[2].equalsIgnoreCase(sender.getName()))
                    Locale.INVALID_ISLAND.send(sender);
                else if (targetPlayer == null)
                    Locale.INVALID_ISLAND_OTHER_NAME.send(sender, StringUtils.stripColors(args[2]));
                else
                    Locale.INVALID_ISLAND_OTHER.send(sender, targetPlayer.getName());
                return;
            }

            if (args.length == 4 && args[3].startsWith("-f")) {
                sender.sendMessage(ChatColor.RED + "Forcing!");
            } else if (island.isBeingRecalculated()) {
                Locale.RECALC_ALREADY_RUNNING_OTHER.send(sender);
                sender.sendMessage(ChatColor.RED + "If stuck, do /is recalc " + args[2] + " -f");
                return;
            }

            Locale.RECALC_PROCCESS_REQUEST.send(sender);
            island.calcIslandWorth(sender instanceof Player ? plugin.getPlayers().getSuperiorPlayer(sender) : null);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return args.length == 3 ? CommandTabCompletes.getOnlinePlayersWithIslands(plugin, args[2], false) : new ArrayList<>();
    }

}
