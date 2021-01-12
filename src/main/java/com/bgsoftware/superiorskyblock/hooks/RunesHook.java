package com.bgsoftware.superiorskyblock.hooks;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import net.spacedelta.runes.RunesPlugin;
import net.spacedelta.runes.rune.model.Rune;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class RunesHook {

    public static boolean PRESENT;

    private static RunesPlugin plugin;

    public static void register(SuperiorSkyblockPlugin skyblockPlugin) {
        plugin = (RunesPlugin) skyblockPlugin.getServer().getPluginManager().getPlugin("SDRunes");
        PRESENT = true;
    }

    public static void hasRune(Player player, String id, Consumer<Boolean> result) {
        plugin.getDataManager().getPlayer(player,
                data -> result.accept(data.getEquippedRunes().keySet().stream()
                .anyMatch(equipped -> equipped.getName().equalsIgnoreCase(id))));
    }

}
