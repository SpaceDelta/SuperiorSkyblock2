package com.bgsoftware.superiorskyblock.hooks;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public final class BlocksProvider_MergedSpawner implements BlocksProvider {

    private static boolean registered = false;

    public BlocksProvider_MergedSpawner() {
        if (false && !registered) { // SpaceDelta
            Bukkit.getPluginManager().registerEvents(new BlocksProvider_MergedSpawner.StackerListener(), SuperiorSkyblockPlugin.getPlugin());
            registered = true;
            SuperiorSkyblockPlugin.log("Using MergedSpawner as a spawners provider.");
        }
    }

    public static boolean isRegistered() {
        return registered;
    }

    @Override
    public Pair<Integer, String> getSpawner(Location location) {
        int blockCount = -1;
        if (Bukkit.isPrimaryThread()) {
            // Start SpaceDelta
            // MergedSpawnerAPI spawnerAPI = MergedSpawnerAPI.getInstance();
            // blockCount = spawnerAPI.getCountFor(location.getBlock());
            // End SpaceDelta
        }
        return new Pair<>(blockCount, null);
    }

    @Override
    public String getSpawnerType(ItemStack itemStack) {
        return "PIG"; // return MergedSpawnerAPI.getInstance().getEntityType(itemStack).name();
    }

    @SuppressWarnings("unused")
    private static class StackerListener implements Listener {

        // Start SpaceDelta
        /*
        private final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerStack(MergedSpawnerPlaceEvent e){
            Island island = plugin.getGrid().getIslandAt(e.getBlock().getLocation());
            int increaseAmount = e.getNewCount() - e.getOldCount();
            if(island != null)
                island.handleBlockPlace(e.getBlock(), increaseAmount);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onSpawnerUnstack(MergedSpawnerBreakEvent e){
            Island island = plugin.getGrid().getIslandAt(e.getBlock().getLocation());
            int decreaseAmount = e.getOldCount() - e.getNewCount();
            if(island != null)
                island.handleBlockBreak(Key.of(Materials.SPAWNER.toBukkitType() + ":" + e.getSpawnerType()), decreaseAmount);
        }
         */
        // End SpaceDelta
    }

}
