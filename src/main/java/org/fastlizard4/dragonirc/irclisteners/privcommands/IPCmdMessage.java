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

package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.DragonIRC;
import org.fastlizard4.dragonirc.RUtils;

public class IPCmdMessage extends ListenerAdapter {

    private final DragonIRC plugin;

    public IPCmdMessage(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        if (!e.getMessage().trim().split(" ")[0].equalsIgnoreCase("message")) {
            return;
        }
        final User u = e.getUser();
        String[] args = e.getMessage().split(" ");
        args = ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 2) {
            e.respond("Usage: message [name] [message]");
            return;
        }
        final String target = args[0];
        String message = StringUtils.join(args, " ", 1, args.length);
        final Player t = plugin.getServer().getPlayer(target);
        if (t == null) {
            e.respond("No such player.");
            return;
        }
        if (Config.allowColors) {
            message = RUtils.ircColorsToMinecraftColors(message);
        } else {
            message = Colors.removeFormattingAndColors(message);
        }
        String send = Config.ituMessage;
        send = send.replace("{server}", e.getBot().getServerInfo().getServerName());
        send = send.replace("{name}", u.getNick());
        send = send.replace("{message}", message);
        t.sendMessage(send);
    }
}
