package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.commands.IAdminIslandCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminChaosTime implements IAdminIslandCommand {

    public static boolean chaos = false;

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("chaostime");
    }

    @Override
    public String getPermission() {
        return "superior.admin.chaostime";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin chaostime";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return "Turn on island griefing.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultipleIslands() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblock plugin, CommandSender sender, String[] args) {
        if (chaos = !chaos) {
            sender.sendMessage(ChatColor.RED + "https://www.youtube.com/watch?v=lfNhRe0Qlbk");
        } else {
            sender.sendMessage(ChatColor.RED + "Pussy mode enabled");
        }
    }

}
