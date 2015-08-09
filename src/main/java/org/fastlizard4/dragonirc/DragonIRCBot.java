package org.fastlizard4.dragonirc;

import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;

public class DragonIRCBot {

    final private PircBotX bot;

    public DragonIRCBot(final DragonIRC plugin, ConfigurationSection cs, ListenerManager<PircBotX> lm) {
        final String hostname = cs.getString("hostname");
        final int port = cs.getInt("port", 6667);
        final String password = cs.getString("server_password", "");
        Configuration.Builder<PircBotX> cb = new Configuration.Builder<PircBotX>();
        cb.setServer(hostname, port)
                .setAutoNickChange(true)
                .setMessageDelay(cs.getLong("message_delay", 1000L))
                .setName(cs.getString("nick", "DragonIRCBot"))
                .setFinger(cs.getString("finger", "DragonIRC Link"))
                .setLogin(cs.getString("login", "DragonIRC"))
                .setVersion("DragonIRC" + DragonIRC.version)
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
