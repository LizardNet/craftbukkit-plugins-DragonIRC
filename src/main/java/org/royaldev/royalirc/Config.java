package org.royaldev.royalirc;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class Config {

    private final RoyalIRC plugin;

    public Config(RoyalIRC instance) {
        plugin = instance;
        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            if (!config.getParentFile().mkdirs()) plugin.getLogger().warning("Could not create config.yml directory.");
            plugin.saveDefaultConfig();
        }
        reloadConfiguration();
    }

    public void reloadConfiguration() {
        plugin.reloadConfig();
        FileConfiguration c = plugin.getConfig();

        itbPrivmsg = RUtils.colorize(c.getString("messages.itb.privmsg"));
        itbAction = RUtils.colorize(c.getString("messages.itb.action"));
        itbJoin = RUtils.colorize(c.getString("messages.itb.join"));
        itbPart = RUtils.colorize(c.getString("messages.itb.part"));
        itbQuit = RUtils.colorize(c.getString("messages.itb.quit"));
        itbKick = RUtils.colorize(c.getString("messages.itb.kick"));

        btiMessage = RUtils.colorize(c.getString("messages.bti.message"));
        btiAction = RUtils.colorize(c.getString("messages.bti.action"));
        btiLogin = RUtils.colorize(c.getString("messages.bti.login"));
        btiQuit = RUtils.colorize(c.getString("messages.bti.quit"));
        btiKick = RUtils.colorize(c.getString("messages.bti.kick"));
        btiSay = RUtils.colorize(c.getString("messages.bti.say"));

        itiMessage = c.getString("messages.iti.message");
        itiAction = c.getString("messages.iti.action");
        itiJoin = c.getString("messages.iti.join");
        itiPart = c.getString("messages.iti.part");
        itiQuit = c.getString("messages.iti.quit");
        itiKick = c.getString("messages.iti.kick");

        defaultReason = c.getString("messages.default_reason");

        actionAliases = c.getStringList("commands.bukkit.action");
        admins = c.getStringList("settings.admins");
        sayAliases = c.getStringList("commands.bukkit.say");
        mods = c.getStringList("settings.mods");

        commentChar = c.getString("settings.comment_character").charAt(0);
        fantasyChar = c.getString("settings.fantasy_character").charAt(0);

        linkChannels = c.getBoolean("settings.link_channels");
        allowColors = c.getBoolean("settings.colors.allow_colors");
        parseColors = c.getBoolean("settings.colors.parse_colors");
    }

    public static String itbPrivmsg;
    public static String itbAction;
    public static String itbJoin;
    public static String itbPart;
    public static String itbQuit;
    public static String itbKick;

    public static String btiMessage;
    public static String btiAction;
    public static String btiLogin;
    public static String btiQuit;
    public static String btiKick;
    public static String btiSay;

    public static String itiMessage;
    public static String itiAction;
    public static String itiJoin;
    public static String itiPart;
    public static String itiQuit;
    public static String itiKick;

    public static String defaultReason;

    public static List<String> actionAliases;
    public static List<String> admins;
    public static List<String> sayAliases;
    public static List<String> mods;

    public static char commentChar;
    public static char fantasyChar;

    public static boolean linkChannels;
    public static boolean allowColors;
    public static boolean parseColors;

}

