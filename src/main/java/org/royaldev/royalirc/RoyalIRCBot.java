package org.royaldev.royalirc;

import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.PircBotX;

public class RoyalIRCBot {

    final private PircBotX bot;

    public RoyalIRCBot(RoyalIRC plugin, ConfigurationSection cs) {
        bot = new PircBotX();
        bot.setAutoNickChange(true);
        bot.setMessageDelay(cs.getLong("message_delay", 1000L));
        bot.setName(cs.getString("nick", "RoyalIRCBot"));
        bot.setFinger(cs.getString("finger", "RoyalDev IRC Link"));
        bot.setLogin(cs.getString("login", "RoyalIRC"));
        bot.setVersion("RoyalIRC " + RoyalIRC.version);
        final String hostname = cs.getString("hostname");
        final int port = cs.getInt("port", 6667);
        plugin.getLogger().info("Attempting to connect to " + hostname + ":" + port + "...");
        try {
            bot.connect(cs.getString("hostname"), cs.getInt("port"));
            plugin.getLogger().info("Connected!");
        } catch (Exception e) {
            plugin.getLogger().warning("Could not connect to " + hostname + ":" + port + "!");
            return;
        }
        if (cs.getString("auth").equalsIgnoreCase("NickServ")) {
            plugin.getLogger().info("Identifying with NickServ...");
            bot.identify(cs.getString("auth_password"));
        }
        for (String channel : cs.getStringList("channels")) {
            String[] parts = channel.trim().split(" ");
            if (parts.length > 1) {
                plugin.getLogger().info("Joining " + parts[0] + " with key...");
                bot.joinChannel(channel, parts[1]);
            } else {
                plugin.getLogger().info("Joining " + parts[0] + "...");
                bot.joinChannel(channel);
            }
        }
    }

    public PircBotX getBackend() {
        return bot;
    }

}
