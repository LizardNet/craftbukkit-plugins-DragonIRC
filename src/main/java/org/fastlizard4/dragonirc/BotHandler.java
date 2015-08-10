/**
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, William Luc Ritchie, and the LizardNet
 * CraftBukkit Plugins Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015 by Andrew "FastLizard4" Adams, William Luc Ritchie, and the
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

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.fastlizard4.dragonirc.irclisteners.IChatListener;
import org.fastlizard4.dragonirc.irclisteners.IChatRelay;
import org.fastlizard4.dragonirc.irclisteners.IKickListener;
import org.fastlizard4.dragonirc.irclisteners.fantasy.ICmdKick;
import org.fastlizard4.dragonirc.irclisteners.fantasy.ICmdPlayers;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdAuthenticate;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdCommand;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdDeauthenticate;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdJoin;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdMessage;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdPart;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdPrivmsg;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdRaw;

import java.util.ArrayList;
import java.util.List;

public class BotHandler {

    private final DragonIRC plugin;
    private final List<DragonIRCBot> bots = new ArrayList<>();
    private final ListenerManager<PircBotX> lm = new ThreadedListenerManager<>();

    public BotHandler(DragonIRC instance) {
        plugin = instance;
    }

    private String convertMessages(String s) {
        if (Config.parseMinecraftColors) s = ChatColor.translateAlternateColorCodes('&', s);
        if (Config.allowColors) s = RUtils.minecraftColorstoIRCColors(s);
        else s = ChatColor.stripColor(s);
        return s;
    }

    public void createBots() {
        if (lm.getListeners().size() < 1) {
            lm.addListener(new IChatListener(plugin));
            lm.addListener(new IChatRelay(plugin));
            lm.addListener(new IKickListener());
            lm.addListener(new ICmdPlayers(plugin));
            lm.addListener(new ICmdKick(plugin));
            lm.addListener(new IPCmdMessage(plugin));
            lm.addListener(new IPCmdCommand(plugin));
            lm.addListener(new IPCmdAuthenticate(plugin));
            lm.addListener(new IPCmdDeauthenticate());
            lm.addListener(new IPCmdPrivmsg());
            lm.addListener(new IPCmdRaw());
            lm.addListener(new IPCmdJoin());
            lm.addListener(new IPCmdPart());
        }
        ConfigurationSection servers = plugin.getConfig().getConfigurationSection("servers");
        for (String server : servers.getKeys(false)) {
            plugin.getLogger().info("Starting bot " + server + "...");
            final DragonIRCBot rib = new DragonIRCBot(plugin, servers.getConfigurationSection(server), lm);
            synchronized (bots) {
                bots.add(rib);
            }
            plugin.getLogger().info("Bot initiated.");
        }
    }

    public void sendMessage(String message) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) c.send().message(message);
            }
        }
    }

    public void sendMessage(String message, Channel c) {
        message = convertMessages(message);
        c.send().message(message);
    }

    public void sendMessageToOtherChannels(String message, Channel dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    if (RUtils.sameChannels(dontSendTo, c)) continue;
                    c.send().message(message);
                }
            }
        }
    }

    public void sendMessageToOtherServers(String message, String dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                if (bot.getBackend().getServerInfo().getServerName().equals(dontSendTo)) continue;
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    c.send().message(message);
                }
            }
        }
    }

    public void disconnect() {
        try {
            synchronized (bots) {
                for (final DragonIRCBot bot : bots) {
                    bot.getBackend().sendIRC().quitServer("DragonIRC disabled.");
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void clearListenerManager() {
        final Listener[] ls = lm.getListeners().toArray(new Listener[lm.getListeners().size()]);
        for (Listener l : ls) lm.removeListener(l);
    }

    public List<DragonIRCBot> getBots() {
        return bots;
    }

    public DragonIRCBot getBotByServer(String server) {
        for (DragonIRCBot rib : bots) {
            if (!rib.getBackend().getConfiguration().getServerHostname().equalsIgnoreCase(server)) continue;
            return rib;
        }
        return null;
    }

    public boolean userInChannels(User u) {
        for (Channel uC : u.getChannels()) {
            for (Channel bC : u.getBot().getUserBot().getChannels()) {
                if (!uC.getName().equalsIgnoreCase(bC.getName())) continue;
                return true;
            }
        }
        return false;
    }

}
