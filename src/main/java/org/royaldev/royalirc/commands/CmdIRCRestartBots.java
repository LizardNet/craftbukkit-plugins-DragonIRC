package org.royaldev.royalirc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class CmdIRCRestartBots implements CommandExecutor {

    private final RoyalIRC plugin;

    public CmdIRCRestartBots(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ircrestartbots")) {
            if (!cs.hasPermission("royalirc.ircrestartbots")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            plugin.bh.disconnect();
            plugin.bh.clearListenerManager();
            plugin.bh.createBots();
        }
        return false;
    }

}
