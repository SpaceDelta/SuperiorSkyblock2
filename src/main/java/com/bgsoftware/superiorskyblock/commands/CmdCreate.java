package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.sync.MessageType;
import com.bgsoftware.superiorskyblock.utils.commands.CommandTabCompletes;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class CmdCreate implements ISuperiorCommand {

    private final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    public static void executeOnServer(SuperiorSkyblockPlugin plugin, UUID uuid) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(uuid);

        if (superiorPlayer.getIsland() != null) {
            CmdTeleport.executeOnServer(superiorPlayer); // tp to island if we already have one
            // Locale.ALREADY_IN_ISLAND.send(superiorPlayer);
            return;
        }

        if (plugin.getGrid().hasActiveCreateRequest(superiorPlayer)) {
            Locale.ISLAND_CREATE_PROCESS_FAIL.send(superiorPlayer);
            return;
        }

        String islandName = "", schematicName = null;

        /*
        if(plugin.getSettings().islandNamesRequiredForCreation) {
            if (args.length >= 2) {
                islandName = args[1];
                if(!StringUtils.isValidName(superiorPlayer, null, islandName))
                    return;
            }
        }

        if(plugin.getSettings().schematicNameArgument && args.length == (plugin.getSettings().islandNamesRequiredForCreation ? 3 : 2)){
            schematicName = args[plugin.getSettings().islandNamesRequiredForCreation ? 2 : 1];
            Schematic schematic = plugin.getSchematics().getSchematic(schematicName);
            if(schematic == null || schematicName.endsWith("_nether") || schematicName.endsWith("_the_end")){
                Locale.INVALID_SCHEMATIC.send(superiorPlayer, schematicName);
                return;
            }
        }
         */

        // if(schematicName == null) {
        var buffer1 = DataBuffer.create()
                .write("uuid", superiorPlayer.asOfflinePlayer().getUniqueId().toString())
                .write("island-name", islandName);

        plugin.getLibrary().getMessageBus().fire(plugin, MessageType.OPEN_ISLAND_CREATION_MENU, buffer1);
        //     MenuIslandCreation.openInventory(superiorPlayer, null, islandName);
        // }
        // else{
        //     MenuIslandCreation.simulateClick(superiorPlayer, islandName, schematicName, false);
        // }
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("create");
    }

    @Override
    public String getPermission() {
        return "superior.island.create";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        StringBuilder usage = new StringBuilder("create");

        if (plugin.getSettings().islandNamesRequiredForCreation)
            usage.append(" <").append(Locale.COMMAND_ARGUMENT_ISLAND_NAME.getMessage(locale)).append(">");

        if (plugin.getSettings().schematicNameArgument)
            usage.append(" [").append(Locale.COMMAND_ARGUMENT_SCHEMATIC_NAME.getMessage(locale)).append("]");

        return usage.toString();
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_CREATE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return plugin.getSettings().islandNamesRequiredForCreation ? 2 : 1;
    }

    @Override
    public int getMaxArgs() {
        int args = 3;

        if (!plugin.getSettings().islandNamesRequiredForCreation)
            args--;

        if (!plugin.getSettings().schematicNameArgument)
            args--;

        return args;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        var uuid = ((Player) sender).getUniqueId();

        if (SuperiorSkyblockPlugin.isClient) {
            var buffer = DataBuffer.create()
                    .write("uuid", uuid.toString())
                    .write("raw-args", args);

            plugin.getLibrary().getMessageBus().fire(plugin, MessageType.CREATE_ISLAND_REQUEST, buffer);
        } else {
            executeOnServer(plugin, uuid);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        int argumentLength = plugin.getSettings().islandNamesRequiredForCreation ? 3 : 2;
        return plugin.getSettings().schematicNameArgument && args.length == argumentLength ?
                CommandTabCompletes.getSchematics(plugin, args[argumentLength - 1]) : new ArrayList<>();
    }

}
