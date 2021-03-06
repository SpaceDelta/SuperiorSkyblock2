package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.menu.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.utils.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.utils.islands.IslandPrivileges;
import com.bgsoftware.superiorskyblock.utils.islands.IslandUtils;
import net.spacedelta.lib.data.DataBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class CmdKick implements IPermissibleCommand {

    public static void executeKick(SuperiorSkyblockPlugin plugin, UUID uuid, Island island, String name) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(uuid);
        SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(name);

        if (targetPlayer == null)
            return;

        if (!IslandUtils.checkKickRestrictions(superiorPlayer, island, targetPlayer))
            return;

        if (plugin.getSettings().kickConfirm) {
            MenuConfirmKick.openInventory(superiorPlayer, null, targetPlayer);
        } else {
            IslandUtils.handleKickPlayer(superiorPlayer, island, targetPlayer);
        }
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("kick", "remove");
    }

    @Override
    public String getPermission() {
        return "superior.island.kick";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "kick <" + Locale.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_KICK.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public IslandPrivilege getPrivilege() {
        return IslandPrivileges.KICK_MEMBER;
    }

    @Override
    public Locale getPermissionLackMessage() {
        return Locale.NO_KICK_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Island island, String[] args) {
        var buffer = DataBuffer.create()
                .write("uuid", superiorPlayer.asPlayer().getUniqueId().toString())
                .write("island", superiorPlayer.getIslandLeader().getUniqueId().toString()) // this has to be the leader
                .write("name", args[1]);

        if (SuperiorSkyblockPlugin.isClient) {
            superiorPlayer.asPlayer().sendMessage(SuperiorSkyblockPlugin.WRONG_SERVER);
        } else {
            executeKick(plugin, superiorPlayer.getUniqueId(), island, args[1]);
            return;
        }
        /*
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if(targetPlayer == null)
            return;

        if(!IslandUtils.checkKickRestrictions(superiorPlayer, island, targetPlayer))
            return;

        if(plugin.getSettings().kickConfirm) {
            MenuConfirmKick.openInventory(superiorPlayer, null, targetPlayer);
        }
        else {
            IslandUtils.handleKickPlayer(superiorPlayer, island, targetPlayer);
        }

         */
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Island island, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getIslandMembersWithLowerRole(island, args[1], superiorPlayer.getPlayerRole()) : new ArrayList<>();
    }
}
