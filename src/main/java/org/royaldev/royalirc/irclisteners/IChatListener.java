package org.royaldev.royalirc.irclisteners;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalirc.Config;
import org.royaldev.royalirc.RUtils;
import org.royaldev.royalirc.RoyalIRC;

public class IChatListener extends ListenerAdapter {

    private final RoyalIRC plugin;

    public IChatListener(RoyalIRC instance) {
        plugin = instance;
    }

    private String replaceVars(GenericMessageEvent e, String s) {
        s = s.replace("{name}", e.getUser().getNick());
        s = s.replace("{message}", e.getMessage());
        return s;
    }

    private String replaceVarsGeneric(Event e, String s) {
        s = s.replace("{server}", e.getBot().getServer());
        s = s.replace("{botname}", e.getBot().getNick());
        return s;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        if (e.getMessage().trim().startsWith(String.valueOf(Config.commentChar))) return;
        if (RUtils.isFantasyCommand(e.getMessage())) return;
        String message = Config.itbMessage;
        message = replaceVars(e, message);
        message = message.replace("{channel}", e.getChannel().getName());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onAction(ActionEvent e) {
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itbAction;
        message = replaceVars(e, message);
        message = message.replace("{channel}", e.getChannel().getName());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onJoin(JoinEvent e) {
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itbJoin;
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onPart(PartEvent e) {
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itbPart;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", reason);
        message = message.replace("{channel}", e.getChannel().getName());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onKick(KickEvent e) {
        String message = Config.itbKick;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = message.replace("{name}", e.getRecipient().getNick());
        message = message.replace("{message}", reason);
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{kicker}", e.getSource().getNick());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onQuit(QuitEvent e) {
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itbQuit;
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", reason);
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

}
