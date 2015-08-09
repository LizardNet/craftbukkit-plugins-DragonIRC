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
