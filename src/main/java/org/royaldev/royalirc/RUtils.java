package org.royaldev.royalirc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.Colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RUtils {

    private static final List<String> fantasyCommands = new ArrayList<String>() {
        {
            add("players");
            add("kick");
        }
    };

    public static boolean isFantasyCommand(String message) {
        final String firstWord = getFirstWord(message);
        return firstWord.startsWith(String.valueOf(Config.fantasyChar)) && fantasyCommands.contains(firstWord.substring(1));
    }

    public static String getFantasyCommand(String message) {
        return message.trim().split(" ")[0].substring(1);
    }

    public static String getFirstWord(String message) {
        return message.trim().split(" ")[0];
    }

    public static void dispNoPerms(CommandSender cs) {
        cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
    }

    public static String colorize(String s) {
        if (s == null) return null;
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void scheduleKick(final Player p, final String reason) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RoyalIRC.instance, new Runnable() {
            @Override
            public void run() {
                p.kickPlayer(reason);
            }
        });
    }

    public static boolean sameChannels(Channel a, Channel b) {
        return a.getName().equalsIgnoreCase(b.getName()) && a.getBot().getServer().equalsIgnoreCase(b.getBot().getServer());
    }

    public static boolean containsChannel(Collection<Channel> cs, Channel lookFor) {
        for (Channel c : cs) {
            if (!c.getName().equalsIgnoreCase(lookFor.getName()) || !c.getBot().getServer().equalsIgnoreCase(lookFor.getBot().getServer()))
                continue;
            return true;
        }
        return false;
    }

    public static String ircColorsToMinecraftColors(String s) {
        s = s.replace(Colors.BLACK, ChatColor.BLACK.toString());
        s = s.replace(Colors.BLUE, ChatColor.BLUE.toString());
        s = s.replace(Colors.BOLD, ChatColor.BOLD.toString());
        s = s.replace(Colors.BROWN, ChatColor.DARK_RED.toString());
        s = s.replace(Colors.CYAN, ChatColor.BLUE.toString());
        s = s.replace(Colors.DARK_BLUE, ChatColor.DARK_BLUE.toString());
        s = s.replace(Colors.DARK_GRAY, ChatColor.DARK_GRAY.toString());
        s = s.replace(Colors.DARK_GREEN, ChatColor.DARK_GREEN.toString());
        s = s.replace(Colors.GREEN, ChatColor.GREEN.toString());
        s = s.replace(Colors.LIGHT_GRAY, ChatColor.GRAY.toString());
        s = s.replace(Colors.MAGENTA, ChatColor.DARK_PURPLE.toString());
        s = s.replace(Colors.NORMAL, ChatColor.RESET.toString());
        s = s.replace(Colors.OLIVE, ChatColor.DARK_GREEN.toString());
        s = s.replace(Colors.PURPLE, ChatColor.LIGHT_PURPLE.toString());
        s = s.replace(Colors.RED, ChatColor.RED.toString());
        s = s.replace(Colors.REVERSE, "");
        s = s.replace(Colors.TEAL, ChatColor.AQUA.toString());
        s = s.replace(Colors.UNDERLINE, ChatColor.UNDERLINE.toString());
        s = s.replace(Colors.WHITE, ChatColor.WHITE.toString());
        s = s.replace(Colors.YELLOW, ChatColor.YELLOW.toString());
        s = Colors.removeFormattingAndColors(s); // catch any stragglers
        return s;
    }

    public static String minecraftColorstoIRCColors(String s) {
        s = s.replace(ChatColor.BLACK.toString(), Colors.BLACK);
        s = s.replace(ChatColor.BLUE.toString(), Colors.BLUE);
        s = s.replace(ChatColor.BOLD.toString(), Colors.BOLD);
        s = s.replace(ChatColor.DARK_RED.toString(), Colors.BROWN);
        s = s.replace(ChatColor.BLUE.toString(), Colors.CYAN);
        s = s.replace(ChatColor.DARK_BLUE.toString(), Colors.DARK_BLUE);
        s = s.replace(ChatColor.DARK_GRAY.toString(), Colors.DARK_GRAY);
        s = s.replace(ChatColor.DARK_GREEN.toString(), Colors.DARK_GREEN);
        s = s.replace(ChatColor.GREEN.toString(), Colors.GREEN);
        s = s.replace(ChatColor.GRAY.toString(), Colors.LIGHT_GRAY);
        s = s.replace(ChatColor.DARK_PURPLE.toString(), Colors.MAGENTA);
        s = s.replace(ChatColor.RESET.toString(), Colors.NORMAL);
        s = s.replace(ChatColor.DARK_GREEN.toString(), Colors.OLIVE);
        s = s.replace(ChatColor.LIGHT_PURPLE.toString(), Colors.PURPLE);
        s = s.replace(ChatColor.RED.toString(), Colors.RED);
        s = s.replace(ChatColor.AQUA.toString(), Colors.TEAL);
        s = s.replace(ChatColor.UNDERLINE.toString(), Colors.UNDERLINE);
        s = s.replace(ChatColor.WHITE.toString(), Colors.WHITE);
        s = s.replace(ChatColor.YELLOW.toString(), Colors.YELLOW);
        s = s.replace(ChatColor.RESET.toString(), Colors.NORMAL);
        s = s.replace(ChatColor.GOLD.toString(), Colors.YELLOW);
        s = ChatColor.stripColor(s); // catch any stragglers
        return s;
    }

}
