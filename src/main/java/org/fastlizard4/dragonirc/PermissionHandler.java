/*
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, William Luc Ritchie, and the LizardNet
 * CraftBukkit Plugins Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015-2023 by Andrew "FastLizard4" Adams, William Luc Ritchie, and the
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

package org.fastlizard4.dragonirc;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;

public class PermissionHandler {

    private static final List<String> authed = new ArrayList<>();

    public static boolean isAuthenticated(String name, String server) {
        final String entry = name + ":" + server.toLowerCase();
        synchronized (authed) {
            return authed.contains(entry);
        }
    }

    public static void setAuthenticated(String name, String server, boolean isAuthed) {
        final String entry = name + ":" + server.toLowerCase();
        synchronized (authed) {
            if (!isAuthed) {
                authed.remove(entry);
            } else {
                authed.add(entry);
            }
        }
    }

    public static boolean atLeastHalfOp(User u, Channel c) {
        return c.isHalfOp(u) || c.isOp(u) || c.isOwner(u) || c.isSuperOp(u) || u.isIrcop();
    }

    public static boolean atLeastOp(User u, Channel c) {
        return c.isOp(u) || c.isOwner(u) || c.isSuperOp(u) || u.isIrcop();
    }

    public static boolean isMod(String name) {
        return Config.mods.contains(name);
    }

    public static boolean isAuthedOrAdmin(String name, String server) {
        return isAuthenticated(name, server) || isAdmin(name);
    }

    public static boolean isAdmin(String name) {
        return Config.admins.contains(name);
    }

    public static boolean atLeastMod(String name) {
        return isMod(name) || isAdmin(name);
    }

}
