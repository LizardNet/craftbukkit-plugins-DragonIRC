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

package org.fastlizard4.dragonirc.irclisteners.fantasy;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import org.fastlizard4.dragonirc.DragonIRC;
import org.fastlizard4.dragonirc.RUtils;

public class ICmdPlayers extends ListenerAdapter {

    private final DragonIRC plugin;

    public ICmdPlayers(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!RUtils.isFantasyCommand(e.getMessage())) {
            return;
        }
        final String command = RUtils.getFantasyCommand(e.getMessage());
        if (!command.equalsIgnoreCase("players")) {
            return;
        }
        Collection<? extends Player> online = plugin.getServer().getOnlinePlayers();
        StringBuilder sb = new StringBuilder("Players (%s/%s): ");
        for (Player p : online) {
            sb.append(p.getName());
            sb.append(", ");
        }
        int visible = online.size();
        String toSend = String.format(sb.toString(), visible, plugin.getServer().getMaxPlayers());
        if (visible > 0) {
            toSend = toSend.substring(0, toSend.length() - 2);
        }
        e.respond(toSend);
    }
}
