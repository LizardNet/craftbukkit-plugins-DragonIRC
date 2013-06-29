package org.royaldev.royalirc;

import org.pircbotx.Channel;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.List;

public class PermissionHandler {

    private static final List<String> authed = new ArrayList<String>();

    public static boolean isAuthenticated(String name, String server) {
        final String entry = name + ":" + server.toLowerCase();
        synchronized (authed) {
            return authed.contains(entry);
        }
    }

    public static void setAuthenticated(String name, String server, boolean isAuthed) {
        final String entry = name + ":" + server.toLowerCase();
        synchronized (authed) {
            if (!isAuthed) authed.remove(entry);
            else authed.add(entry);
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
