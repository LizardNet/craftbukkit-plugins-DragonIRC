/**
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, TLUL, and the LizardNet CraftBukkit Plugins
 * Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015 by Andrew "FastLizard4" Adams, TLUL, and the LizardNet
 * CraftBukkit Plugins Development Team. Some rights reserved.
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

package org.fastlizard4.dragonirc.bukkitlistener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.DragonIRC;

public class BChatListener implements Listener {

    private final DragonIRC plugin;

    public BChatListener(DragonIRC instance) {
        plugin = instance;
    }

    private String replaceVars(PlayerEvent e, String s) {
        s = s.replace("{name}", e.getPlayer().getName());
        return s;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (Config.useSyncChat) return;
        if (e.isCancelled() || e.getRecipients().isEmpty()) return;
        String message = Config.btiMessage;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage());
        plugin.bh.sendMessage(message);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void syncOnChat(PlayerChatEvent e) {
        if (!Config.useSyncChat) return;
        if (e.isCancelled() || e.getRecipients().isEmpty()) return;
        String message = Config.btiMessage;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage());
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAction(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) return;
        boolean isAction = false;
        String commandUsed = "";
        for (String start : Config.actionAliases) {
            if (!e.getMessage().startsWith(start)) continue;
            isAction = true;
            commandUsed = start;
            break;
        }
        if (!isAction) return;
        String message = Config.btiAction;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage().substring(commandUsed.length()));
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSay(PlayerCommandPreprocessEvent e) {
        boolean isAction = false;
        String commandUsed = "";
        for (String start : Config.sayAliases) {
            if (!e.getMessage().startsWith(start)) continue;
            commandUsed = start;
            isAction = true;
            break;
        }
        if (!isAction) return;
        String message = Config.btiSay;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage().substring(commandUsed.length()));
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) { // login too early for vanish
        String message = Config.btiLogin;
        message = replaceVars(e, message);
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        String message = Config.btiQuit;
        message = replaceVars(e, message);
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e) {
        String message = Config.btiKick;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = replaceVars(e, message);
        message = message.replace("{message}", reason.replaceAll("\\r?\\n", " "));
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if (!Config.reportPlayerDeaths) return;
        String message = Config.btiDeath;
        message = message.replace("{name}", e.getEntity().getDisplayName());
        message = message.replace("{message}", e.getDeathMessage());
        plugin.bh.sendMessage(message);
    }

}
