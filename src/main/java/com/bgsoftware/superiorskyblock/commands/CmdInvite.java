package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.utils.LocaleUtils;
import com.bgsoftware.superiorskyblock.utils.events.EventsCaller;
import com.bgsoftware.superiorskyblock.utils.islands.IslandPrivileges;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.spacedelta.lib.Library;
import net.spacedelta.lib.data.DataBuffer;
import net.spacedelta.starship.StarshipPlugin;
import net.spacedelta.starship.chat.remote.RemoteChatType;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CmdInvite implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("invite", "add");
    }

    @Override
    public String getPermission() {
        return "superior.island.invite";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "invite <" + Locale.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Locale.COMMAND_DESCRIPTION_INVITE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public IslandPrivilege getPrivilege() {
        return IslandPrivileges.INVITE_MEMBER;
    }

    @Override
    public Locale getPermissionLackMessage() {
        return Locale.NO_INVITE_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Island island, String[] args) {
        if (SuperiorSkyblockPlugin.isClient) {
            superiorPlayer.asPlayer().sendMessage(SuperiorSkyblockPlugin.WRONG_SERVER);
            return;
        }

        // TODO this shows as null for cross server
        SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId());

        if (targetPlayer == null) {
            Locale.INVALID_PLAYER.send(superiorPlayer, args[1]);
            return;
        }

        if (island.isMember(targetPlayer)) {
            Locale.ALREADY_IN_ISLAND_OTHER.send(superiorPlayer);
            return;
        }

        if (island.isBanned(targetPlayer)) {
            Locale.INVITE_BANNED_PLAYER.send(superiorPlayer);
            return;
        }

        java.util.Locale locale = LocaleUtils.getLocale(superiorPlayer);
        String message;

        if (island.isInvited(targetPlayer)) {
            island.revokeInvite(targetPlayer);
            message = Locale.REVOKE_INVITE_ANNOUNCEMENT.getMessage(locale, superiorPlayer.getName(), targetPlayer.getName());
            if (targetPlayer.asOfflinePlayer().isOnline())
                Locale.GOT_REVOKED.send(targetPlayer, superiorPlayer.getName());
        } else {
            if (island.getTeamLimit() >= 0 && island.getIslandMembers(true).size() >= island.getTeamLimit()) {
                Locale.INVITE_TO_FULL_ISLAND.send(superiorPlayer);
                return;
            }

            if (!EventsCaller.callIslandInviteEvent(superiorPlayer, targetPlayer, island))
                return;

            island.inviteMember(targetPlayer);
            message = Locale.INVITE_ANNOUNCEMENT.getMessage(locale, superiorPlayer.getName(), targetPlayer.getName());

            java.util.Locale targetLocal = LocaleUtils.getLocale(targetPlayer);

            if (/*targetPlayer.asOfflinePlayer().isOnline() && */ !Locale.GOT_INVITE.isEmpty(targetLocal)) {
                TextComponent textComponent = new TextComponent(Locale.GOT_INVITE.getMessage(targetLocal, superiorPlayer.getName()));
                if (!Locale.GOT_INVITE_TOOLTIP.isEmpty(targetLocal))
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(Locale.GOT_INVITE_TOOLTIP.getMessage(targetLocal))}));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + plugin.getCommands().getLabel() + " accept " + superiorPlayer.getName()));

                var data = DataBuffer.create()
                        .write("target", targetPlayer.getUniqueId().toString())
                        .write("raw-json", ComponentSerializer.toString(textComponent));

                Library.get().getMessageBus().fire(StarshipPlugin.INSTANCE, RemoteChatType.TARGETED_JSON_MESSAGE, data);
            }
        }

        if (message != null)
            island.sendMessage(message);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Island island, String[] args) {
        if (args.length == 2) {
            return Library.get().getNetworkManager().getPlayerDataService().getOnlinePlayers()
                    .filter(s -> s.toLowerCase().contains(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
