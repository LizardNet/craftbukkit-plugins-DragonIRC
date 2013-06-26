package org.royaldev.royalirc.irclisteners.fantasy;

import org.bukkit.entity.Player;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;
import org.royaldev.royalirc.VNPHandler;

public class ICmdPlayers extends ListenerAdapter {

    private final RoyalIRC plugin;

    public ICmdPlayers(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!RUtils.isFantasyCommand(e.getMessage())) return;
        final String command = RUtils.getFantasyCommand(e.getMessage());
        if (!command.equalsIgnoreCase("players")) return;
        Player[] online = plugin.getServer().getOnlinePlayers();
        int vanished = 0;
        StringBuilder sb = new StringBuilder("Players (%s/%s): ");
        for (Player p : online) {
            if (plugin.vanishLoaded() && VNPHandler.isVanished(p)) {
                vanished++;
                continue;
            }
            sb.append(p.getName());
            sb.append(", ");
        }
        int visible = online.length - vanished;
        String toSend = String.format(sb.toString(), visible, plugin.getServer().getMaxPlayers());
        if (visible > 0) toSend = toSend.substring(0, toSend.length() - 2);
        e.respond(toSend);
    }
}
