package org.royaldev.royalirc;

import org.bukkit.entity.Player;
import org.kitteh.vanish.staticaccess.VanishNoPacket;

public class VNPHandler {

    public static boolean isVanished(String name) {
        try {
            return VanishNoPacket.isVanished(name);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isVanished(Player p) {
        return isVanished(p.getName());
    }

}
