package org.fastlizard4.dragonirc.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

public class CmdDragonIRC implements CommandExecutor {

    private final DragonIRC plugin;

    public CmdDragonIRC(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dragonirc")) {
            if (!cs.hasPermission("dragonirc.dragonirc")) {
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
