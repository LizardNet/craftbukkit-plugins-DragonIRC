/*
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, William Luc Ritchie, and the LizardNet
 * CraftBukkit Plugins Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015-2023 by Andrew "FastLizard4" Adams, William Luc Ritchie, and the
 * LizardNet Development Team. Some rights reserved.
 *
 * License GPLv3+: GNU General Public License version 3 or later (at your choice):
 * <http://gnu.org/licenses/gpl.html>. This is free software: you are free to
 * change and redistribute it at your will provided that your redistribution, with
 * or without modifications, is also licensed under the GNU GPL. (Although not
 * required by the license, we also ask that you attribute us!) There is NO
 * WARRANTY FOR THIS SOFTWARE to the extent permitted by law.
 *
 * This is an open source project. The source Git repositories, which you are
 * welcome to contribute to, can be found here:
 * <https://gerrit.fastlizard4.org/r/gitweb?p=craftbukkit-plugins%2FDragonIRC.git;a=summary>
 * <https://git.fastlizard4.org/gitblit/summary/?r=craftbukkit-plugins/DragonIRC>
 *
 * Gerrit Code Review for the project:
 * <https://gerrit.fastlizard4.org/r/#/q/project:craftbukkit-plugins/DragonIRC,n,z>
 *
 * Alternatively, the project source code can be found on the PUBLISH-ONLY mirror
 * on GitHub: <https://github.com/LizardNet/craftbukkit-plugins-DragonIRC>
 *
 * Note: Pull requests and patches submitted to GitHub will be transferred by a
 * developer to Gerrit before they are acted upon.
 */

package org.fastlizard4.dragonirc;

import java.util.stream.Stream;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class BukkitListener implements Listener {

    private final DragonIRC plugin;

    public BukkitListener(DragonIRC instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (Config.useSyncChat || e.isCancelled() || e.getRecipients().isEmpty()) {
            return;
        }

        sendMessage(Config.btiMessage, e.getPlayer().getDisplayName(), e.getMessage());
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void syncOnChat(PlayerChatEvent e) {
        if (!Config.useSyncChat || e.isCancelled() || e.getRecipients().isEmpty()) {
            return;
        }

        sendMessage(Config.btiMessage, e.getPlayer().getDisplayName(), e.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) {
            return;
        }

        String command = e.getMessage();
        Stream.concat(Config.actionAliases.stream(), Config.sayAliases.stream())
                .filter(command::startsWith)
                .findFirst()
                .ifPresent(s -> {
                    String format = Config.actionAliases.contains(s) ? Config.btiAction : Config.btiSay;
                    sendMessage(format, e.getPlayer().getDisplayName(), command.substring(s.length()));
                });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) { // login too early for vanish
        sendMessage(Config.btiLogin, e.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        sendMessage(Config.btiQuit, e.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e) {
        if (e.isCancelled()) {
            return;
        }

        String reason = e.getReason().isEmpty() ? Config.defaultReason : e.getReason();
        sendMessage(Config.btiKick, e.getPlayer().getDisplayName(), reason.replaceAll("\\r?\\n", " "));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        sendMessage(Config.btiDeath, e.getEntity().getDisplayName(), e.getDeathMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent e) {
        String command = e.getCommand();
        Stream.concat(Config.actionAliases.stream(), Config.sayAliases.stream())
                .filter(s -> command.startsWith(s.substring(1)))
                .findFirst()
                .ifPresent(s -> {
                    String format = Config.actionAliases.contains(s) ? Config.btiAction : Config.btiSay;
                    sendMessage(format, e.getSender().getName(), command.substring(s.length() - 1));
                });
    }

    private void sendMessage(String format, String... substitutions) {
        if (format == null || format.isEmpty()) {
            return;
        }

        format = format.replace("{name}", "%1$s");
        format = format.replace("{message}", "%2$s");
        plugin.bh.sendMessage(String.format(format, substitutions));
    }
}
