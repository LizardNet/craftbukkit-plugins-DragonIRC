package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

public class IPCmdMessage extends ListenerAdapter {

    private final DragonIRC plugin;

    public IPCmdMessage(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        if (!e.getMessage().trim().split(" ")[0].equalsIgnoreCase("message")) return;
        final User u = e.getUser();
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 2) {
            e.respond("Usage: message [name] [message]");
            return;
        }
        final String target = args[0];
        String message = StringUtils.join(args, " ", 1, args.length);
        final Player t = plugin.getServer().getPlayer(target);
        if (t == null) {
            e.respond("No such player.");
            return;
        }
        if (Config.allowColors) message = RUtils.ircColorsToMinecraftColors(message);
        else message = Colors.removeFormattingAndColors(message);
        String send = Config.ituMessage;
        send = send.replace("{server}", e.getBot().getServerInfo().getServerName());
        send = send.replace("{name}", u.getNick());
        send = send.replace("{message}", message);
        t.sendMessage(send);
    }
}
