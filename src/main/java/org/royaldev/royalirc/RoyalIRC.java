package org.royaldev.royalirc;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Colors;
import org.royaldev.royalirc.bukkitlistener.BChatListener;
import org.royaldev.royalirc.bukkitlistener.BServerListener;
import org.royaldev.royalirc.commands.CmdRoyalIRC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoyalIRC extends JavaPlugin {

    public Config c;
    public BotHandler bh;

    public static RoyalIRC instance;
    public static String version;

    private final Pattern versionPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+)(\\-SNAPSHOT)?(\\-local\\-(\\d{8}\\.\\d{6})|\\-(\\d+))?");
    private Metrics m;

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

        //-- Hidendra's Metrics --//

        try {
            Matcher matcher = versionPattern.matcher(version);
            matcher.matches();
            // 1 = base version
            // 2 = -SNAPSHOT
            // 5 = build #
            String versionMinusBuild = (matcher.group(1) == null) ? "Unknown" : matcher.group(1);
            String build = (matcher.group(5) == null) ? "local build" : matcher.group(5);
            if (matcher.group(2) == null) build = "release";
            m = new Metrics(this);
            Metrics.Graph g = m.createGraph("Version"); // get our custom version graph
            g.addPlotter(
                    new Metrics.Plotter(versionMinusBuild + "~=~" + build) {
                        @Override
                        public int getValue() {
                            return 1; // this value doesn't matter
                        }
                    }
            ); // add the donut graph with major version inside and build outside
            m.addGraph(g); // add the graph
            if (!m.start())
                getLogger().info("You have Metrics off! I like to keep accurate usage statistics, but okay. :(");
            else getLogger().info("Metrics enabled. Thank you!");
        } catch (Exception ignore) {
            getLogger().warning("Could not start Metrics!");
        }
    }

    @Override
    public void onDisable() {
        try {
            bh.disconnect();
        } catch (Exception ignored) {
        }
    }

}
