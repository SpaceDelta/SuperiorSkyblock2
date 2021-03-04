package com.bgsoftware.superiorskyblock.sync;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.spacedelta.lib.data.DataBuffer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class PlayerUtils {

    private static final SuperiorSkyblockPlugin PLUGIN = SuperiorSkyblockPlugin.INSTANCE;

    @NotNull
    public static UUID readUuid(@NotNull DataBuffer buffer) {
        return UUID.fromString(buffer.readString("uuid"));
    }

    @NotNull
    public static Optional<Player> getPlayer(@NotNull UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @NotNull
    public static Optional<SuperiorPlayer> getSuperiorPlayer(@NotNull UUID uuid) {
        return Optional.ofNullable(PLUGIN.getPlayers().getSuperiorPlayer(uuid));
    }
}
