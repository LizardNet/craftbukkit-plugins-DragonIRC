package org.royaldev.royalirc.irclisteners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class IChatRelay extends ListenerAdapter {

    private final RoyalIRC plugin;

    public IChatRelay(RoyalIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        if (RUtils.isFantasyCommand(e.getMessage())) return;
        String message = Config.itiMessage;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", e.getMessage());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onAction(ActionEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiAction;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", e.getMessage());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onJoin(JoinEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiJoin;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onKick(KickEvent e) {
        if (!Config.linkChannels) return;
        if (e.getRecipient().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiKick;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getRecipient().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", e.getReason());
        message = message.replace("{kicker}", e.getSource().getNick());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onPart(PartEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiPart;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", e.getReason());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onQuit(QuitEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiQuit;
        message = message.replace("{server}", e.getBot().getServer());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", e.getReason());
        plugin.bh.sendMessageToOtherServers(message, e.getBot().getServer());
    }
}
