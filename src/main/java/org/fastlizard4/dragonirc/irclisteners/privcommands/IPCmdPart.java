package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;

public class IPCmdPart extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("part")) return;
        final User u = e.getUser();
        if (!PermissionHandler.isAuthedOrAdmin(u.getNick(), u.getServer())) {
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
        Channel c = null;
        for (Channel ch : e.getBot().getUserBot().getChannels()) {
            if (!ch.getName().equalsIgnoreCase(channel)) continue;
            c = ch;
            break;
        }
        if (c == null) {
            e.respond("Not in that channel.");
            return;
        }
        c.send().part();
        e.respond("Attempted to part channel " + channel + ".");
    }
}
