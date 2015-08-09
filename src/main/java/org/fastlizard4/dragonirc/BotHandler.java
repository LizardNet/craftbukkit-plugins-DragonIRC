package org.fastlizard4.dragonirc;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.ListenerManager;
import org.pircbotx.hooks.managers.ThreadedListenerManager;
import org.fastlizard4.dragonirc.irclisteners.IChatListener;
import org.fastlizard4.dragonirc.irclisteners.IChatRelay;
import org.fastlizard4.dragonirc.irclisteners.IKickListener;
import org.fastlizard4.dragonirc.irclisteners.fantasy.ICmdKick;
import org.fastlizard4.dragonirc.irclisteners.fantasy.ICmdPlayers;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdAuthenticate;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdCommand;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdDeauthenticate;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdJoin;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdMessage;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdPart;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdPrivmsg;
import org.fastlizard4.dragonirc.irclisteners.privcommands.IPCmdRaw;

import java.util.ArrayList;
import java.util.List;

public class BotHandler {

    private final DragonIRC plugin;
    private final List<DragonIRCBot> bots = new ArrayList<DragonIRCBot>();
    private final ListenerManager<PircBotX> lm = new ThreadedListenerManager<PircBotX>();

    public BotHandler(DragonIRC instance) {
        plugin = instance;
    }

    private String convertMessages(String s) {
        if (Config.parseMinecraftColors) s = ChatColor.translateAlternateColorCodes('&', s);
        if (Config.allowColors) s = RUtils.minecraftColorstoIRCColors(s);
        else s = ChatColor.stripColor(s);
        return s;
    }

    public void createBots() {
        if (lm.getListeners().size() < 1) {
            lm.addListener(new IChatListener(plugin));
            lm.addListener(new IChatRelay(plugin));
            lm.addListener(new IKickListener());
            lm.addListener(new ICmdPlayers(plugin));
            lm.addListener(new ICmdKick(plugin));
            lm.addListener(new IPCmdMessage(plugin));
            lm.addListener(new IPCmdCommand(plugin));
            lm.addListener(new IPCmdAuthenticate(plugin));
            lm.addListener(new IPCmdDeauthenticate());
            lm.addListener(new IPCmdPrivmsg());
            lm.addListener(new IPCmdRaw());
            lm.addListener(new IPCmdJoin());
            lm.addListener(new IPCmdPart());
        }
        ConfigurationSection servers = plugin.getConfig().getConfigurationSection("servers");
        for (String server : servers.getKeys(false)) {
            plugin.getLogger().info("Starting bot " + server + "...");
            final DragonIRCBot rib = new DragonIRCBot(plugin, servers.getConfigurationSection(server), lm);
            synchronized (bots) {
                bots.add(rib);
            }
            plugin.getLogger().info("Bot initiated.");
        }
    }

    public void sendMessage(String message) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) c.send().message(message);
            }
        }
    }

    public void sendMessage(String message, Channel c) {
        message = convertMessages(message);
        c.send().message(message);
    }

    public void sendMessageToOtherChannels(String message, Channel dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    if (RUtils.sameChannels(dontSendTo, c)) continue;
                    c.send().message(message);
                }
            }
        }
    }

    public void sendMessageToOtherServers(String message, String dontSendTo) {
        message = convertMessages(message);
        synchronized (bots) {
            for (DragonIRCBot bot : bots) {
                if (bot.getBackend().getServerInfo().getServerName().equals(dontSendTo)) continue;
                for (Channel c : bot.getBackend().getUserBot().getChannels()) {
                    c.send().message(message);
                }
            }
        }
    }

    public void disconnect() {
        try {
            synchronized (bots) {
                for (final DragonIRCBot bot : bots) {
                    bot.getBackend().sendIRC().quitServer("DragonIRC disabled.");
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void clearListenerManager() {
        final Listener[] ls = lm.getListeners().toArray(new Listener[lm.getListeners().size()]);
        for (Listener l : ls) lm.removeListener(l);
    }

    public List<DragonIRCBot> getBots() {
        return bots;
    }

    public DragonIRCBot getBotByServer(String server) {
        for (DragonIRCBot rib : bots) {
            if (!rib.getBackend().getConfiguration().getServerHostname().equalsIgnoreCase(server)) continue;
            return rib;
        }
        return null;
    }

    public boolean userInChannels(User u) {
        for (Channel uC : u.getChannels()) {
            for (Channel bC : u.getBot().getUserBot().getChannels()) {
                if (!uC.getName().equalsIgnoreCase(bC.getName())) continue;
                return true;
            }
        }
        return false;
    }

}
