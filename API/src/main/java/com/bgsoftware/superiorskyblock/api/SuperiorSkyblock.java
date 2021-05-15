package com.bgsoftware.superiorskyblock.api;

import com.bgsoftware.superiorskyblock.api.handlers.*;

public interface SuperiorSkyblock {

    /**
     * Get the grid of the core.
     */
    GridManager getGrid();

    /**
     * Get the blocks manager of the core.
     */
    BlockValuesManager getBlockValues();

    /**
     * Get the schematics manager of the core.
     */
    SchematicManager getSchematics();

    /**
     * Get the players manager of the core.
     */
    PlayersManager getPlayers();

    /**
     * Get the missions manager of the core.
     */
    MissionsManager getMissions();

    /**
     * Get the menus manager of the core.
     */
    MenusManager getMenus();

    /**
     * Get the keys manager of the core.
     */
    KeysManager getKeys();

    /**
     * Get the providers manager of the core.
     */
    ProvidersManager getProviders();

    /**
     * Get the upgrades manager of the core.
     */
    UpgradesManager getUpgrades();

    /**
     * Get the commands manager of the core.
     */
    CommandsManager getCommands();

    /**
     * Get the objects factory of the plugin.
     */
    FactoriesManager getFactory();

}
