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

package org.fastlizard4.dragonirc.irclisteners.fantasy;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

public class ICmdKick extends ListenerAdapter {

    private final DragonIRC plugin;

    public ICmdKick(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!RUtils.isFantasyCommand(e.getMessage())) return;
        final String command = RUtils.getFantasyCommand(e.getMessage());
        if (!command.equalsIgnoreCase("kick")) return;
        final User u = e.getUser();
        if (!PermissionHandler.atLeastMod(u.getNick()) || !PermissionHandler.atLeastHalfOp(u, e.getChannel())) {
            e.respond("You do not have permission for that.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: " + Config.fantasyChar + "kick [player] (reason)");
            return;
        }
        final String toKick = args[0];
        final String reason = (args.length > 1) ? StringUtils.join(args, " ", 1, args.length) : "Kicked by " + u.getNick();
        final Player p = plugin.getServer().getPlayer(toKick);
        if (p == null) {
            e.respond("No such player.");
            return;
        }
        RUtils.scheduleKick(p, reason);
        e.respond("Kicked " + p.getName() + ".");
    }
}
