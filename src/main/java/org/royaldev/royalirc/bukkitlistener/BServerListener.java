package org.royaldev.royalirc.bukkitlistener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RoyalIRC;

public class BServerListener implements Listener {

    private final RoyalIRC plugin;

    public BServerListener(RoyalIRC instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAction(ServerCommandEvent e) {
        boolean isAction = false;
        String commandUsed = "";
        for (String start : Config.actionAliases) {
            start = start.substring(1);
            if (!e.getCommand().startsWith(start)) continue;
            commandUsed = start;
            isAction = true;
            break;
        }
        if (!isAction) return;
        String message = Config.btiAction;
        message = message.replace("{name}", e.getSender().getName());
        message = message.replace("{message}", e.getCommand().substring(commandUsed.length()));
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSay(ServerCommandEvent e) {
        boolean isAction = false;
        String commandUsed = "";
        for (String start : Config.sayAliases) {
            start = start.substring(1);
            if (!e.getCommand().startsWith(start)) continue;
            commandUsed = start;
            isAction = true;
            break;
        }
        if (!isAction) return;
        String message = Config.btiSay;
        message = message.replace("{name}", e.getSender().getName());
        message = message.replace("{message}", e.getCommand().substring(commandUsed.length()));
        plugin.bh.sendMessage(message);
    }

}
