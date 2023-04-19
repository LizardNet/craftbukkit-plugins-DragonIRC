/**
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, William Luc Ritchie, and the LizardNet
 * CraftBukkit Plugins Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015 by Andrew "FastLizard4" Adams, William Luc Ritchie, and the
 * LizardNet Development Team. Some rights reserved.
 *
 * License GPLv3+: GNU General Public License version 3 or later (at your choice):
 * <http://gnu.org/licenses/gpl.html>. This is free software: you are free to
 * change and redistribute it at your will provided that your redistribution, with
 * or without modifications, is also licensed under the GNU GPL. (Although not
 * required by the license, we also ask that you attribute us!) There is NO
 * WARRANTY FOR THIS SOFTWARE to the extent permitted by law.
 *
 * This is an open source project. The source Git repositories, which you are
 * welcome to contribute to, can be found here:
 * <https://gerrit.fastlizard4.org/r/gitweb?p=craftbukkit-plugins%2FDragonIRC.git;a=summary>
 * <https://git.fastlizard4.org/gitblit/summary/?r=craftbukkit-plugins/DragonIRC>
 *
 * Gerrit Code Review for the project:
 * <https://gerrit.fastlizard4.org/r/#/q/project:craftbukkit-plugins/DragonIRC,n,z>
 *
 * Alternatively, the project source code can be found on the PUBLISH-ONLY mirror
 * on GitHub: <https://github.com/LizardNet/craftbukkit-plugins-DragonIRC>
 *
 * Note: Pull requests and patches submitted to GitHub will be transferred by a
 * developer to Gerrit before they are acted upon.
 */

package org.fastlizard4.dragonirc.irclisteners.privcommands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import org.fastlizard4.dragonirc.PermissionHandler;
import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IPCmdAuthenticate extends ListenerAdapter {

    private final DragonIRC plugin;

    public IPCmdAuthenticate(DragonIRC instance) {
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
