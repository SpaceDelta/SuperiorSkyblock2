package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminIslandCommand;
import com.bgsoftware.superiorskyblock.schematics.BaseSchematic;
import com.bgsoftware.superiorskyblock.utils.chunks.ChunkPosition;
import com.bgsoftware.superiorskyblock.utils.islands.IslandUtils;
import com.bgsoftware.superiorskyblock.utils.threads.Executor;
import net.spacedelta.lib.util.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CmdAdminIslandRegen implements IAdminIslandCommand {

    public static void regenerate(SuperiorPlayer player, Island island, Consumer<Boolean> outcome) {
        final Location center = player.getIsland().getCenter(World.Environment.NORMAL);

        Schematic schematic = SuperiorSkyblockPlugin.INSTANCE.getSchematics().getSchematic(island.getSchematicName());
        schematic.pasteSchematic(island, center.getBlock().getRelative(BlockFace.DOWN).getLocation(), () -> {
            Set<ChunkPosition> loadedChunks = ((BaseSchematic) schematic).getLoadedChunks();

            island.setTeleportLocation(((BaseSchematic) schematic).getTeleportLocation(center));

            player.teleport(island, result -> {
                if (result && center.getBlock().getRelative(0, -2, 0).getType() != Material.AIR) {
                    SuperiorSkyblockPlugin.INSTANCE.info("Regenerated island for " + player.getUniqueId());
                    Executor.sync(() -> IslandUtils.resetChunksExcludedFromList(island, loadedChunks), 10L);
                    outcome.accept(true);
                } else {
                    SuperiorSkyblockPlugin.INSTANCE.warn("Failed to regenerate island for " + player.getUniqueId());
                    outcome.accept(false);
                }
            });

        }, ex -> {
            SuperiorSkyblockPlugin.INSTANCE.error("Failed to regenerate island for " + player.getUniqueId(), ex);
            ex.printStackTrace();
            Locale.CREATE_ISLAND_FAILURE.send(player);
            outcome.accept(false);
        });

    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("regen");
    }

    @Override
    public String getPermission() {
        return "superior.admin.regen";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin regen <name>";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return "Regenerate an empty island.";
    }

    @Override
    public int getMinArgs() {
        return 3;
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
    public boolean supportMultipleIslands() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer targetPlayer, Island island, String[] args) {
        targetPlayer.teleport(island, result -> {
            if (result) {
                sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " island was fine, they were teleported.");
            } else {
                plugin.warn("Island of " + targetPlayer.getName() + " has no safe blocks, regenerating...");
                regenerate(targetPlayer, island, outcome -> {
                    sender.sendMessage(ChatColor.RED + "Failed to regen island.");
                });

                final Player player = targetPlayer.asPlayer();
                if (player != null) {
                    SoundUtils.playSound(player, SoundUtils.Feedback.ERROR);
                    player.sendMessage(ChatColor.RED + "Sorry, it looks like there was a problem generating your island.");
                    player.sendMessage(ChatColor.RED + "We are going to keep trying and hopefully you will get to your island soon.");
                }

            }
        });
    }

}
