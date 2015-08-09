package org.fastlizard4.dragonirc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

public class CmdIRCRestartBots implements CommandExecutor {

    private final DragonIRC plugin;

    public CmdIRCRestartBots(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ircrestartbots")) {
            if (!cs.hasPermission("dragonirc.ircrestartbots")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.bh.disconnect();
            plugin.bh.clearListenerManager();
            plugin.bh.createBots();
            return true;
        }
        return false;
    }

}
