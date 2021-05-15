package com.bgsoftware.superiorskyblock.tasks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.admin.CmdAdminIslandRegen;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class RegenerationCheckerTask implements Runnable {

    private final World world;

    private final List<Player> generating;
    private List<Player> lastTick;

    public RegenerationCheckerTask() {
        world = Bukkit.getWorlds().get(0);

        lastTick = Lists.newArrayList();
        generating = Lists.newArrayList();

        /*
        Task.builder()
                .repeat(5, TimeUnit.SECONDS)
                .execute(this)
                .schedule();

         */
    }

    @Override
    public void run() {

        Lists.newArrayList(world.getPlayers()).forEach(player -> {
            if (generating.contains(player))
                return;

            if (lastTick.contains(player)) {
                final SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
                if (superiorPlayer.hasBypassModeEnabled()
                        || SuperiorSkyblockAPI.getSuperiorSkyblock().getGrid().hasActiveCreateRequest(superiorPlayer)) {
                    return;
                }

                final Island island = superiorPlayer.getIsland();
                if (island == null) {
                    player.sendMessage(ChatColor.RED + "Please create an island with /is create");
                } else {
                    generating.add(player);

                    superiorPlayer.teleport(island, result -> {
                        if (result) {
                            generating.remove(player);
                        } else {
                            CmdAdminIslandRegen.regenerate(
                                    superiorPlayer,
                                    island,
                                    createOutcome -> {
                                        generating.remove(player);
                                        if (createOutcome) {
                                            superiorPlayer.teleport(island);
                                        }
                                    }
                            );
                        }
                    });

                }
            }

        });

        lastTick = Lists.newArrayList(world.getPlayers());
    }

}
