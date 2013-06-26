package org.royaldev.royalirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.royaldev.royalirc.Config;

public class IPCmdPrivmsg extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        if (!e.getMessage().trim().split(" ")[0].equalsIgnoreCase("privmsg")) return;
        if (!Config.admins.contains(e.getUser().getNick())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 2) {
            e.respond("Usage: privmsg [user] [message]");
            return;
        }
        String sendTo = args[0];
        String message = StringUtils.join(args, " ", 1, args.length);
        final User u = e.getBot().getUser(sendTo);
        u.sendMessage(message);
        e.respond("Sent message \"" + message + "\" to " + u.getNick() + ".");
    }
}
