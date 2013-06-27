package org.royaldev.royalirc.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.User;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;
import org.royaldev.royalirc.RoyalIRCBot;

public class CmdIRCMessage implements CommandExecutor {

    private final RoyalIRC plugin;

    public CmdIRCMessage(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ircmessage")) {
            if (!cs.hasPermission("royalirc.ircmessage")) {
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
            RoyalIRCBot bot = plugin.bh.getBotByServer(server);
            if (bot == null) {
                cs.sendMessage(ChatColor.RED + "No such server!");
                return true;
            }
            final User u = bot.getBackend().getUser(user);
            if (!plugin.bh.userInChannels(u)) {
                cs.sendMessage(ChatColor.RED + "That user is not in the channel!");
                return true;
            }
            String send = Config.btuMessage;
            send = send.replace("{name}", cs.getName());
            send = send.replace("{message}", message);
            if (Config.parseColors) message = ChatColor.translateAlternateColorCodes('&', message);
            if (Config.allowColors) message = RUtils.minecraftColorstoIRCColors(message);
            else message = ChatColor.stripColor(message);
            u.sendMessage(send);
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
