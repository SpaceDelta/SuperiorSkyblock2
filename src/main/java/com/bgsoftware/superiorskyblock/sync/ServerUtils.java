package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ServerUtils {

    private static final SuperiorSkyblockPlugin PLUGIN = SuperiorSkyblockPlugin.INSTANCE;

    @NotNull
    public static String getMainServerId() {
        return "dev3";
    }

    public static void sendPlayerToServer(@NotNull String playerName, @NotNull String serverId) {
        var out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(playerName);
        out.writeUTF(serverId);
        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(PLUGIN, "BungeeCord", out.toByteArray());
    }
}
