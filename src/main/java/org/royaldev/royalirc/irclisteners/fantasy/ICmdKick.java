package org.royaldev.royalirc.irclisteners.fantasy;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.PermissionHandler;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class ICmdKick extends ListenerAdapter {

    private final RoyalIRC plugin;

    public ICmdKick(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!RUtils.isFantasyCommand(e.getMessage())) return;
        final String command = RUtils.getFantasyCommand(e.getMessage());
        if (!command.equalsIgnoreCase("kick")) return;
        final User u = e.getUser();
        if (!PermissionHandler.atLeastMod(u.getNick()) || !PermissionHandler.atLeastHalfOp(u, e.getChannel())) {
            e.respond("You do not have permission for that.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: " + Config.fantasyChar + "kick [player] (reason)");
            return;
        }
        final String toKick = args[0];
        final String reason = (args.length > 1) ? StringUtils.join(args, " ", 1, args.length) : "Kicked by " + u.getNick();
        final Player p = plugin.getServer().getPlayer(toKick);
        if (p == null) {
            e.respond("No such player.");
            return;
        }
        RUtils.scheduleKick(p, reason);
        e.respond("Kicked " + p.getName() + ".");
    }
}
