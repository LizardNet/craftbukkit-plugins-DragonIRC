package org.royaldev.royalirc;

import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;

public class RoyalIRCBot {

    final private PircBotX bot;

    public RoyalIRCBot(final RoyalIRC plugin, ConfigurationSection cs, ListenerManager<PircBotX> lm) {
        final String hostname = cs.getString("hostname");
        final int port = cs.getInt("port", 6667);
        final String password = cs.getString("server_password", "");
        Configuration.Builder<PircBotX> cb = new Configuration.Builder<PircBotX>();
        cb.setServer(hostname, port)
                .setAutoNickChange(true)
                .setMessageDelay(cs.getLong("message_delay", 1000L))
                .setName(cs.getString("nick", "RoyalIRCBot"))
                .setFinger(cs.getString("finger", "RoyalDev IRC Link"))
                .setLogin(cs.getString("login", "RoyalIRC"))
                .setVersion("RoyalIRC" + RoyalIRC.version)
                .setListenerManager(lm);
        if (!password.isEmpty()) cb.setServerPassword(password);
        if (cs.getString("auth").equalsIgnoreCase("NickServ")) cb.setNickservPassword(cs.getString("auth_password"));
        for (String channel : cs.getStringList("channels")) {
            String[] parts = channel.trim().split(" ");
            if (parts.length > 1) cb.addAutoJoinChannel(parts[0], parts[1]);
            else cb.addAutoJoinChannel(parts[0]);
        }
        bot = new PircBotX(cb.buildConfiguration());
        plugin.getLogger().info("Attempting to connect to " + hostname + ":" + port + "...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bot.startBot();
                } catch (Exception e) {
                    plugin.getLogger().warning("Could not connect to " + hostname + ":" + port + "!");
                }
            }
        }).start();
    }

    public PircBotX getBackend() {
        return bot;
    }

}
