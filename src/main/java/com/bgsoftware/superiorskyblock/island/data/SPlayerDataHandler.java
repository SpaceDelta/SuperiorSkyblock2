package com.bgsoftware.superiorskyblock.island.data;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.PlayerDataHandler;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.utils.database.DatabaseObject;
import com.bgsoftware.superiorskyblock.utils.database.Query;
import com.bgsoftware.superiorskyblock.utils.database.StatementHolder;
import com.bgsoftware.superiorskyblock.utils.islands.IslandSerializer;
import org.bukkit.scheduler.BukkitTask;

import java.util.Locale;

public final class SPlayerDataHandler extends DatabaseObject implements PlayerDataHandler {

    private final SuperiorPlayer superiorPlayer;

    private boolean immuneToPvP = false;
    private boolean immuneToTeleport = false;
    private boolean leavingFlag = false;

    private BukkitTask teleportTask = null;

    public SPlayerDataHandler(SuperiorPlayer superiorPlayer) {
        this.superiorPlayer = superiorPlayer;
    }

    @Override
    public void saveTextureValue() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_TEXTURE.getStatementHolder(this)
                .setString(superiorPlayer.getTextureValue())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void savePlayerName() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_NAME.getStatementHolder(this)
                .setString(superiorPlayer.getName())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveUserLocale() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Locale userLocale = superiorPlayer.getUserLocale();
        Query.PLAYER_SET_LANGUAGE.getStatementHolder(this)
                .setString(userLocale.getLanguage() + "-" + userLocale.getCountry())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveIslandLeader() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_LEADER.getStatementHolder(this)
                .setString(superiorPlayer.getIslandLeader().getUniqueId().toString())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void savePlayerRole() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_ROLE.getStatementHolder(this)
                .setString(superiorPlayer.getPlayerRole().getId() + "")
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveToggledBorder() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_TOGGLED_BORDER.getStatementHolder(this)
                .setBoolean(superiorPlayer.hasWorldBorderEnabled())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveDisbands() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_DISBANDS.getStatementHolder(this)
                .setInt(superiorPlayer.getDisbands())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveToggledPanel() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_TOGGLED_PANEL.getStatementHolder(this)
                .setBoolean(superiorPlayer.hasToggledPanel())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveIslandFly() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_ISLAND_FLY.getStatementHolder(this)
                .setBoolean(superiorPlayer.hasIslandFlyEnabled())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveBorderColor() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_BORDER.getStatementHolder(this)
                .setString(superiorPlayer.getBorderColor().name())
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveLastTimeStatus() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_LAST_STATUS.getStatementHolder(this)
                .setString(superiorPlayer.getLastTimeStatus() + "")
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public void saveMissions() {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_SET_MISSIONS.getStatementHolder(this)
                .setString(IslandSerializer.serializeMissions(superiorPlayer.getCompletedMissionsWithAmounts()))
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(true);
    }

    @Override
    public StatementHolder setUpdateStatement(StatementHolder statementHolder) {
        return statementHolder.setString(superiorPlayer.getIslandLeader().getUniqueId().toString())
                .setString(superiorPlayer.getName())
                .setString(superiorPlayer.getPlayerRole().getId() + "")
                .setString(superiorPlayer.getTextureValue())
                .setInt(superiorPlayer.getDisbands())
                .setBoolean(superiorPlayer.hasToggledPanel())
                .setBoolean(superiorPlayer.hasIslandFlyEnabled())
                .setString(superiorPlayer.getBorderColor().name())
                .setString(superiorPlayer.getLastTimeStatus() + "")
                .setString(IslandSerializer.serializeMissions(superiorPlayer.getCompletedMissionsWithAmounts()))
                .setString(superiorPlayer.getUserLocale().getLanguage() + "-" + superiorPlayer.getUserLocale().getCountry())
                .setBoolean(superiorPlayer.hasWorldBorderEnabled())
                .setString(superiorPlayer.getUniqueId().toString());
    }

    @Override
    public void executeUpdateStatement(boolean async) {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        setUpdateStatement(Query.PLAYER_UPDATE.getStatementHolder(this)).execute(async);
    }

    @Override
    public void executeInsertStatement(boolean async) {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_INSERT.getStatementHolder(this)
                .setString(superiorPlayer.getUniqueId().toString())
                .setString(superiorPlayer.getIslandLeader().getUniqueId().toString())
                .setString(superiorPlayer.getName())
                .setString(superiorPlayer.getPlayerRole().getId() + "")
                .setString(superiorPlayer.getTextureValue())
                .setInt(superiorPlayer.getDisbands())
                .setBoolean(superiorPlayer.hasToggledPanel())
                .setBoolean(superiorPlayer.hasIslandFlyEnabled())
                .setString(superiorPlayer.getBorderColor().name())
                .setString(superiorPlayer.getLastTimeStatus() + "")
                .setString(IslandSerializer.serializeMissions(superiorPlayer.getCompletedMissionsWithAmounts()))
                .setString(superiorPlayer.getUserLocale().getLanguage() + "-" + superiorPlayer.getUserLocale().getCountry())
                .setBoolean(superiorPlayer.hasWorldBorderEnabled())
                .execute(async);
    }

    @Override
    public void executeDeleteStatement(boolean async) {
        if (SuperiorSkyblockPlugin.isClient) {
            return;
        }

        Query.PLAYER_DELETE.getStatementHolder(this)
                .setString(superiorPlayer.getUniqueId().toString())
                .execute(async);
    }

    public boolean isImmunedToPvP() {
        return immuneToPvP;
    }

    public void setImmunedToPvP(boolean immunedToPvP) {
        SuperiorSkyblockPlugin.debug("Action: Set PvP Immune, Player: " + superiorPlayer.getName() + ", Immune: " + immunedToPvP);
        this.immuneToPvP = immunedToPvP;
    }

    public boolean isLeavingFlag() {
        return leavingFlag;
    }

    public void setLeavingFlag(boolean leavingFlag) {
        SuperiorSkyblockPlugin.debug("Action: Set Leaving Flag, Player: " + superiorPlayer.getName() + ", Flag: " + leavingFlag);
        this.leavingFlag = leavingFlag;
    }

    public BukkitTask getTeleportTask() {
        return teleportTask;
    }

    public void setTeleportTask(BukkitTask teleportTask) {
        if (this.teleportTask != null)
            this.teleportTask.cancel();
        this.teleportTask = teleportTask;
    }

    public boolean isImmunedToTeleport() {
        return immuneToTeleport;
    }

    public void setImmunedToTeleport(boolean immuneToTeleport) {
        this.immuneToTeleport = immuneToTeleport;
        SuperiorSkyblockPlugin.debug("Action: Set Teleport Immune, Player: " + superiorPlayer.getName() + ", Immune: " + immuneToTeleport);
    }

}
