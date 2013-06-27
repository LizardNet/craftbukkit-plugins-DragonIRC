package org.royaldev.royalirc;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Colors;
import org.royaldev.royalirc.bukkitlistener.BChatListener;
import org.royaldev.royalirc.bukkitlistener.BServerListener;
import org.royaldev.royalirc.commands.CmdRoyalIRC;

public class RoyalIRC extends JavaPlugin {

    public Config c;
    public BotHandler bh;

    public static RoyalIRC instance;
    public static String version;

    public boolean vanishLoaded() {
        return getServer().getPluginManager().getPlugin("VanishNoPacket") != null;
    }

    public void sendToMinecraft(String s) {
        if (Config.allowColors) s = RUtils.ircColorsToMinecraftColors(s);
        else s = Colors.removeFormattingAndColors(s);
        getServer().broadcastMessage(s);
    }

    @Override
    public void onEnable() {
        instance = this;
        c = new Config(this);
        version = getDescription().getVersion();
        bh = new BotHandler(this);
        bh.createBots();

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BChatListener(this), this);
        pm.registerEvents(new BServerListener(this), this);

        getCommand("royalirc").setExecutor(new CmdRoyalIRC(this));
    }

    @Override
    public void onDisable() {
        try {
            bh.disconnect();
        } catch (Exception ignored) {
        }
    }

}
