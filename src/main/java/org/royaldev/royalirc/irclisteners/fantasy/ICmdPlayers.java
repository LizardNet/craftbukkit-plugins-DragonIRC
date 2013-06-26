package org.royaldev.royalirc.irclisteners.fantasy;

import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RoyalIRC;
import org.royaldev.royalirc.VNPHandler;

public class ICmdPlayers extends ListenerAdapter {

    private final RoyalIRC plugin;

    public ICmdPlayers(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        final String message = e.getMessage();
        if (!message.startsWith(String.valueOf(Config.fantasyChar))) return;
        if (!message.substring(1).equalsIgnoreCase("players")) return;
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
        final Channel c = e.getChannel();
        int visible = online.length - vanished;
        final String toSend = String.format(sb.toString(), visible, plugin.getServer().getMaxPlayers());
        if (visible < 1) plugin.bh.sendMessage(toSend, c);
        else plugin.bh.sendMessage(toSend.substring(0, toSend.length() - 2), c);
    }

}
