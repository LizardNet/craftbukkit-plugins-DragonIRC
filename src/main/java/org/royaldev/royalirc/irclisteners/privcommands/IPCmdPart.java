package org.royaldev.royalirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.royaldev.royalirc.RUtils;

public class IPCmdPart extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("part")) return;
        final User u = e.getUser();
        if (!RUtils.isAdmin(u.getNick())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: part [channel]");
            return;
        }
        final String channel = args[0];
        if (!channel.startsWith("#")) {
            e.respond("Channel must start with \"#\" to be valid!");
            return;
        }
        final Channel c = e.getBot().getChannel(channel);
        if (!e.getBot().channelExists(c.getName())) {
            e.respond("Not in that channel.");
            return;
        }
        e.getBot().partChannel(c);
        e.respond("Attempted to part channel " + channel + ".");
    }
}
