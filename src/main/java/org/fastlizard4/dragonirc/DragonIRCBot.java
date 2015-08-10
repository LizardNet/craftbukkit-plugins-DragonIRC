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
        Configuration.Builder<PircBotX> cb = new Configuration.Builder<>();
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
        new Thread(() -> {
            try {
                bot.startBot();
            } catch (Exception e) {
                plugin.getLogger().warning("Could not connect to " + hostname + ":" + port + "!");
            }
        }).start();
    }

    public PircBotX getBackend() {
        return bot;
    }

}
