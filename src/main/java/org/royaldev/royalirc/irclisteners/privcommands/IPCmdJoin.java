package org.royaldev.royalirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.royaldev.royalirc.RUtils;

public class IPCmdJoin extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("join")) return;
        final User u = e.getUser();
        if (!RUtils.isAdmin(u.getNick())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: join [channel]");
            return;
        }
        final String channel = args[0];
        if (!channel.startsWith("#")) {
            e.respond("Channel must start with \"#\" to be valid!");
            return;
        }
        e.getBot().joinChannel(channel);
        e.respond("Attempted to join channel " + channel + ".");
    }
}
