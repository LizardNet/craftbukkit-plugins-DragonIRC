package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;

public class IPCmdRaw extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("raw")) return;
        final User u = e.getUser();
        if (!PermissionHandler.isAuthedOrAdmin(u.getNick(), u.getServer())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().trim().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: raw [command]");
            return;
        }
        String rawCommand = StringUtils.join(args, " ");
        e.getBot().sendRaw().rawLine(rawCommand);
        e.respond("Attempted to send raw IRC line: " + rawCommand);
    }
}
