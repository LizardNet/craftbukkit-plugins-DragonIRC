package org.royaldev.royalirc.bukkitlistener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RoyalIRC;
import org.royaldev.royalirc.VNPHandler;

public class BChatListener implements Listener {

    private final RoyalIRC plugin;

    public BChatListener(RoyalIRC instance) {
        plugin = instance;
    }

    private String replaceVars(PlayerEvent e, String s) {
        s = s.replace("{name}", e.getPlayer().getName());
        return s;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled() || e.getRecipients().isEmpty()) return;
        String message = Config.btiMessage;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage());
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAction(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) return;
        boolean isAction = false;
        String commandUsed = "";
        for (String start : Config.actionAliases) {
            if (!e.getMessage().startsWith(start)) continue;
            isAction = true;
            commandUsed = start;
            break;
        }
        if (!isAction) return;
        String message = Config.btiAction;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getMessage().substring(commandUsed.length()));
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) { // login too early for vanish
        if (plugin.vanishLoaded() && VNPHandler.isVanished(e.getPlayer())) return;
        String message = Config.btiLogin;
        message = replaceVars(e, message);
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.vanishLoaded() && VNPHandler.isVanished(e.getPlayer())) return;
        String message = Config.btiQuit;
        message = replaceVars(e, message);
        plugin.bh.sendMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e) {
        if (plugin.vanishLoaded() && VNPHandler.isVanished(e.getPlayer())) return;
        String message = Config.btiKick;
        message = replaceVars(e, message);
        message = message.replace("{message}", e.getReason());
        plugin.bh.sendMessage(message);
    }

}
