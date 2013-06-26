package org.royaldev.royalirc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class CmdRoyalIRC implements CommandExecutor {

    private final RoyalIRC plugin;

    public CmdRoyalIRC(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("royalirc")) {
            if (!cs.hasPermission("royalirc.royalirc")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.c.reloadConfiguration();
            cs.sendMessage(ChatColor.BLUE + "Reloaded config for " + ChatColor.GRAY + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + ChatColor.BLUE + ".");
            return true;
        }
        return false;
    }

}
