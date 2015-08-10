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

package org.fastlizard4.dragonirc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;
import org.pircbotx.User;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;
import org.fastlizard4.dragonirc.DragonIRCBot;

public class CmdIRCKick implements CommandExecutor {

    private final DragonIRC plugin;

    public CmdIRCKick(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("irckick")) {
            if (!cs.hasPermission("dragonirc.irckick")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 3) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String server = args[0];
            String channel = args[1];
            String user = args[2];
            String reason = (args.length > 3) ? StringUtils.join(args, " ", 3, args.length) : Config.defaultReason;
            final DragonIRCBot rib = plugin.bh.getBotByServer(server);
            if (rib == null) {
                cs.sendMessage(ChatColor.RED + "No such server!");
                return true;
            }
            Channel c = null;
            for (Channel ch : rib.getBackend().getUserBot().getChannels()) {
                if (!ch.getName().equalsIgnoreCase(channel)) continue;
                c = ch;
                break;
            }
            if (c == null) {
                cs.sendMessage(ChatColor.RED + "Bot is not in that channel!");
                return true;
            }
            final User u = rib.getBackend().getUserChannelDao().getUser(user);
            if (!c.getUsers().contains(u)) {
                cs.sendMessage(ChatColor.RED + "User not in that channel!");
                return true;
            }
            if (!PermissionHandler.atLeastOp(rib.getBackend().getUserBot(), c))
                cs.sendMessage(ChatColor.RED + "Not chanop in that channel; still trying kick.");
            c.send().kick(u, cs.getName() + ": " + reason);
            cs.sendMessage(ChatColor.BLUE + "Attempted to kick " + ChatColor.GRAY + u.getNick() + ChatColor.BLUE + " from " + ChatColor.GRAY + c.getName() + ChatColor.BLUE + " for " + ChatColor.GRAY + reason + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }
}
