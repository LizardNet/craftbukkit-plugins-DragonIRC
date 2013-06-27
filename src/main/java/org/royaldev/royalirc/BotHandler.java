package org.royaldev.royalirc;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.royaldev.royalirc.irclisteners.IChatListener;
import org.royaldev.royalirc.irclisteners.IChatRelay;
import org.royaldev.royalirc.irclisteners.fantasy.ICmdKick;
import org.royaldev.royalirc.irclisteners.fantasy.ICmdPlayers;
import org.royaldev.royalirc.irclisteners.privcommands.IPCmdPrivmsg;
import org.royaldev.royalirc.irclisteners.privcommands.IPCmdRaw;

import java.util.ArrayList;
import java.util.List;

public class BotHandler {

    private final RoyalIRC plugin;
    private final List<RoyalIRCBot> bots = new ArrayList<RoyalIRCBot>();
    private final ListenerManager<PircBotX> lm = new ThreadedListenerManager<PircBotX>();

    public BotHandler(RoyalIRC instance) {
        plugin = instance;
    }

    private String convertMessages(String s) {
        if (Config.parseColors) s = ChatColor.translateAlternateColorCodes('&', s);
        if (Config.allowColors) s = RUtils.minecraftColorstoIRCColors(s);
        else s = ChatColor.stripColor(s);
        return s;
    }

    public void createBots() {
        if (lm.getListeners().size() < 1) {
            lm.addListener(new IChatListener(plugin));
            lm.addListener(new IChatRelay(plugin));
            lm.addListener(new ICmdPlayers(plugin));
            lm.addListener(new ICmdKick(plugin));
            lm.addListener(new IPCmdPrivmsg());
            lm.addListener(new IPCmdRaw());
        }
        ConfigurationSection servers = plugin.getConfig().getConfigurationSection("servers");
        for (String server : servers.getKeys(false)) {
            plugin.getLogger().info("Starting bot " + server + "...");
            final RoyalIRCBot rib = new RoyalIRCBot(plugin, servers.getConfigurationSection(server));
            plugin.getLogger().info("Adding listeners to bot " + server + "...");
            rib.getBackend().setListenerManager(lm);
            synchronized (bots) {
                bots.add(rib);
            }
            plugin.getLogger().info("Bot initiated.");
        }
    }

    public void sendMessage(String message) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getChannels()) {
                    c.sendMessage(message);
                }
            }
        }
    }

    public void sendMessage(String message, Channel c) {
        message = convertMessages(message);
        c.sendMessage(message);
    }

    public void sendMessageToOtherChannels(String message, Channel dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getChannels()) {
                    if (c.equals(dontSendTo)) continue;
                    c.sendMessage(message);
                }
            }
        }
    }

    public void sendMessageToOtherServers(String message, String dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                if (bot.getBackend().getServer().equals(dontSendTo)) continue;
                for (Channel c : bot.getBackend().getChannels()) {
                    c.sendMessage(message);
                }
            }
        }
    }

    public void disconnect() {
        synchronized (bots) {
            for (RoyalIRCBot bot : bots) {
                bot.getBackend().quitServer("RoyalIRC disabled.");
            }
        }
    }

}
