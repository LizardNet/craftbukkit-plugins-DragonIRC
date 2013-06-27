package org.royaldev.royalirc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;
import org.royaldev.royalirc.RoyalIRCBot;

public class CmdIRCKick implements CommandExecutor {

    private final RoyalIRC plugin;

    public CmdIRCKick(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("irckick")) {
            if (!cs.hasPermission("royalirc.irckick")) {
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
            final RoyalIRCBot rib = plugin.bh.getBotByServer(server);
            if (rib == null) {
                cs.sendMessage(ChatColor.RED + "No such server!");
                return true;
            }
            final Channel c = rib.getBackend().getChannel(channel);
            if (!RUtils.containsChannel(rib.getBackend().getChannels(), c)) {
                cs.sendMessage(ChatColor.RED + "Not in that channel!");
                return true;
            }
            final User u = rib.getBackend().getUser(user);
            if (!c.getUsers().contains(u)) {
                cs.sendMessage(ChatColor.RED + "User not in that channel!");
                return true;
            }
            if (!RUtils.atLeastOp(rib.getBackend().getUserBot(), c))
                cs.sendMessage(ChatColor.RED + "Not chanop in that channel; still trying kick.");
            rib.getBackend().kick(c, u, cs.getName() + ": " + reason);
            cs.sendMessage(ChatColor.BLUE + "Attempted to kick " + ChatColor.GRAY + u.getNick() + ChatColor.BLUE + " from " + ChatColor.GRAY + c.getName() + ChatColor.BLUE + " for " + ChatColor.GRAY + reason + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }
}
