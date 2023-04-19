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

import java.util.regex.Pattern;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.Colors;

import org.fastlizard4.dragonirc.commands.CmdDragonIRC;
import org.fastlizard4.dragonirc.commands.CmdIRCKick;
import org.fastlizard4.dragonirc.commands.CmdIRCMessage;
import org.fastlizard4.dragonirc.commands.CmdIRCRestartBots;

public class DragonIRC extends JavaPlugin {

    public static DragonIRC instance;
    public static String version;

    private final Pattern versionPattern = Pattern.compile(
            "(\\d+\\.\\d+\\.\\d+)(\\-SNAPSHOT)?(\\-local\\-(\\d{8}\\.\\d{6})|\\-(\\d+))?");

    public Config c;
    public BotHandler bh = null;

    public boolean vanishLoaded() {
        return getServer().getPluginManager().getPlugin("VanishNoPacket") != null;
    }

    public void sendToMinecraft(String s) {
        getServer().getScheduler().runTask(this, () -> doSendToMinecraft(s));
    }

    /**
     * Wrapper to call {@link org.bukkit.Bukkit#broadcastMessage(String)} from the main thread. It cannot safely be
     * called asynchronously, because it broadcasts the message to command blocks, which could update their data values,
     * which must happen on the main thread. Yes, really.
     *
     * @see <a href="https://github.com/PaperMC/Paper/issues/7511">Paper #7511</a>
     */
    private void doSendToMinecraft(String s) {
        if (Config.allowColors) {
            s = RUtils.ircColorsToMinecraftColors(s);
        } else {
            s = Colors.removeFormattingAndColors(s);
        }
        getServer().broadcastMessage(s);
    }

    @Override
    public void onEnable() {
        instance = this;
        c = new Config(this);
        if (Config.defaultConfig) {
            getLogger().warning("DragonIRC is disabled until the config is edited.");
            setEnabled(false);
            return;
        }
        version = getDescription().getVersion();
        bh = new BotHandler(this);
        bh.createBots();

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BukkitListener(this), this);

        getCommand("dragonirc").setExecutor(new CmdDragonIRC(this));
        getCommand("ircmessage").setExecutor(new CmdIRCMessage(this));
        getCommand("irckick").setExecutor(new CmdIRCKick(this));
        getCommand("ircrestartbots").setExecutor(new CmdIRCRestartBots(this));

        //-- Hidendra's Metrics --//
        /* TODO handle metrics with jar shading, like it's supposed to be
        try {
            Matcher matcher = versionPattern.matcher(version);
            matcher.matches();
            // 1 = base version
            // 2 = -SNAPSHOT
            // 5 = build #
            String versionMinusBuild = (matcher.group(1) == null) ? "Unknown" : matcher.group(1);
            String build = (matcher.group(5) == null) ? "local build" : matcher.group(5);
            if (matcher.group(2) == null) build = "release";
            final Metrics m = new Metrics(this);
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
        */
    }

    @Override
    public void onDisable() {
        if (bh != null) {
            bh.disconnect();
        }
    }

}
