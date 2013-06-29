package org.royaldev.royalirc.irclisteners.privcommands;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.royaldev.royalirc.PermissionHandler;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IPCmdAuthenticate extends ListenerAdapter {

    private final RoyalIRC plugin;

    public IPCmdAuthenticate(RoyalIRC instance) {
        plugin = instance;
    }

    private String hash(String data) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(data.getBytes());
        byte byteData[] = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByteData : byteData) sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent e) {
        final String command = RUtils.getFirstWord(e.getMessage());
        if (!command.equalsIgnoreCase("authenticate")) return;
        final User u = e.getUser();
        if (!PermissionHandler.atLeastMod(u.getNick())) {
            e.respond("You are not allowed to do this.");
            return;
        }
        String[] args = e.getMessage().split(" ");
        args = (String[]) ArrayUtils.subarray(args, 1, args.length);
        if (args.length < 1) {
            e.respond("Usage: authenticate [key]");
            return;
        }
        final String salt = plugin.getConfig().getString("settings.auth.keys.salt", "");
        String key;
        try {
            key = hash(StringUtils.join(args, " ") + salt);
        } catch (NoSuchAlgorithmException ex) {
            e.respond("Internal exception: " + ex.getMessage());
            return;
        }
        final String checkAgainst = plugin.getConfig().getString("settings.auth.keys.users." + u.getNick(), "");
        if (checkAgainst.isEmpty()) {
            e.respond("There is no key stored for you!");
            return;
        }
        if (!checkAgainst.equals(key)) {
            e.respond("Invalid key!");
            return;
        }
        PermissionHandler.setAuthenticated(u.getNick(), u.getServer(), true);
        e.respond("Authenticated!");
    }
}
