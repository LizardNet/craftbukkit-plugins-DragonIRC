package org.royaldev.royalirc.irclisteners.fantasy;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class ICmdKick extends ListenerAdapter {

    private final RoyalIRC plugin;

    public ICmdKick(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!e.getMessage().startsWith(String.valueOf(Config.fantasyChar))) return;
        String command = e.getMessage().trim().split(" ")[0].substring(1);
        if (!command.equalsIgnoreCase("kick")) return;
        if (!Config.mods.contains(e.getUser().getNick()) && !Config.admins.contains(e.getUser().getNick())) {
            e.respond("You do not have permission for this.");
            return;
        }
        if (Config.mods.contains(e.getUser().getNick()) && !e.getChannel().isHalfOp(e.getUser())) {
            e.respond("You do not have permission for this.");
            return;
        }
        if (Config.admins.contains(e.getUser().getNick()) && !e.getChannel().isOp(e.getUser())) {
            e.respond("You do not have permission for this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: " + Config.fantasyChar + "kick [player] (reason)");
            return;
        }
        final String toKick = args[0];
        final String reason = (args.length > 1) ? StringUtils.join(args, " ", 1, args.length) : "Kicked by " + e.getUser().getNick();
        final Player p = plugin.getServer().getPlayer(toKick);
        if (p == null) {
            e.respond("No such player.");
            return;
        }
        RUtils.scheduleKick(p, reason);
        e.respond("Kicked " + p.getName() + ".");
    }
}
