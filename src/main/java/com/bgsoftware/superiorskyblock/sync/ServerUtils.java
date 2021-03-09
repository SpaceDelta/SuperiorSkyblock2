package com.bgsoftware.superiorskyblock.sync;

import net.spacedelta.lib.Library;
import net.spacedelta.lib.util.ConcurrentUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerUtils {

    @NotNull
    public static String getMainServerId() {
        return "sky04";
    }

    public static void sendPlayerToServer(@NotNull Player player, @NotNull String serverId) {
        var server = Library.get().getNetworkManager().getServerData().get(serverId);

        if (server != null) {
            ConcurrentUtils.ensureMain(() -> server.sendPlayer(player));
        }
    }
}
