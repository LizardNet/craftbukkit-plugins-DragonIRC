package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;

public class IPCmdPrivmsg extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("privmsg")) return;
        final User u = e.getUser();
        if (!PermissionHandler.isAuthedOrAdmin(u.getNick(), u.getServer())) {
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
        final User t = e.getBot().getUserChannelDao().getUser(sendTo);
        t.send().message(message);
        e.respond("Sent message \"" + message + "\" to " + t.getNick() + ".");
    }
}
