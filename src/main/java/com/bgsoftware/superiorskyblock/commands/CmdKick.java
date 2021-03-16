package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.menu.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.utils.commands.CommandArguments;
import com.bgsoftware.superiorskyblock.utils.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.utils.islands.IslandPrivileges;
import com.bgsoftware.superiorskyblock.utils.islands.IslandUtils;
import com.bgsoftware.superiorskyblock.Locale;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class CmdKick implements IPermissibleCommand {

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
                .write("island", island.getUniqueId())
                .write("name", args[1]);

        if (SuperiorSkyblockPlugin.isClient) {
            plugin.getLibrary().getMessageBus().fire(plugin, MessageType.KICK_ISLAND_PLAYER, buffer);
        } else {
            executeKick(plugin, buffer);
        }
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
    }

    public static void executeKick(SuperiorSkyblockPlugin plugin, DataBuffer dataBuffer) {
        var uuid = UUID.fromString(dataBuffer.readString("uuid"));
        var islandUUID = UUID.fromString(dataBuffer.readString("island"));
        var name  = dataBuffer.readString("name");

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(uuid);
        SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(name);

        Island island = plugin.getGrid().getIsland(islandUUID);

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
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Island island, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getIslandMembersWithLowerRole(island, args[1], superiorPlayer.getPlayerRole()) : new ArrayList<>();
    }
}
