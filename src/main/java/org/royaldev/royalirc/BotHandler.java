package org.royaldev.royalirc;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Channel;

import java.util.ArrayList;
import java.util.List;

public class BotHandler {

    private final RoyalIRC plugin;
    private final List<RoyalIRCBot> bots = new ArrayList<RoyalIRCBot>();

    public BotHandler(RoyalIRC instance) {
        plugin = instance;
    }

    private String convertMessages(String s) {
        if (Config.parseColors) s = ChatColor.translateAlternateColorCodes('&', s);
        if (Config.allowColors) s = RUtils.minecraftColorstoIRCColors(s);
        return s;
    }

    public void createBots() {
        ConfigurationSection servers = plugin.getConfig().getConfigurationSection("servers");
        for (String server : servers.getKeys(false)) {
            plugin.getLogger().info("Starting bot " + server + "...");
            RoyalIRCBot rib = new RoyalIRCBot(plugin, servers.getConfigurationSection(server));
            synchronized (bots) {
                bots.add(rib);
            }
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
