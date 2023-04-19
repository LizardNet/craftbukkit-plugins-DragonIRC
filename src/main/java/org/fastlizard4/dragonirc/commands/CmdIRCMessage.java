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

package org.fastlizard4.dragonirc.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.User;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.DragonIRC;
import org.fastlizard4.dragonirc.DragonIRCBot;
import org.fastlizard4.dragonirc.RUtils;

public class CmdIRCMessage implements CommandExecutor {

    private final DragonIRC plugin;

    public CmdIRCMessage(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ircmessage")) {
            if (!cs.hasPermission("dragonirc.ircmessage")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 3) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String server = args[0];
            String user = args[1];
            String message = StringUtils.join(args, " ", 2, args.length);
            DragonIRCBot bot = plugin.bh.getBotByServer(server);
            if (bot == null) {
                cs.sendMessage(ChatColor.RED + "No such server!");
                return true;
            }
            final User u = bot.getBackend().getUserChannelDao().getUser(user);
            if (!plugin.bh.userInChannels(u)) {
                cs.sendMessage(ChatColor.RED + "That user is not in the channel!");
                return true;
            }
            if (u.getNick().equalsIgnoreCase(bot.getBackend().getNick())) {
                cs.sendMessage(ChatColor.RED + "You can't message the bot!");
                return true;
            }
            String send = Config.btuMessage;
            send = send.replace("{name}", cs.getName());
            send = send.replace("{message}", message);
            if (Config.parseMinecraftColors) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }
            if (Config.allowColors) {
                message = RUtils.minecraftColorstoIRCColors(message);
            } else {
                message = ChatColor.stripColor(message);
            }
            u.send().message(send);
            String confirm = Config.btuConfirm;
            confirm = confirm.replace("{name}", cs.getName());
            confirm = confirm.replace("{message}", message);
            confirm = confirm.replace("{recipient}", u.getNick());
            confirm = confirm.replace("{server}", u.getServer());
            cs.sendMessage(confirm);
            return true;
        }
        return false;
    }
}
