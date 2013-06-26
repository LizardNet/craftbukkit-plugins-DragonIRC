package org.royaldev.royalirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.royaldev.royalirc.RUtils;

public class IPCmdRaw extends ListenerAdapter {

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        String command = e.getMessage().trim().split(" ")[0];
        if (!command.equalsIgnoreCase("raw")) return;
        final User u = e.getUser();
        if (!RUtils.isAdmin(u.getNick())) {
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
        e.getBot().sendRawLine(rawCommand);
        e.respond("Attempted to send raw IRC line: " + rawCommand);
    }
}
