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
        if (e.getMessage().startsWith(String.valueOf(Config.fantasyChar))) return;
        String message = Config.itbPrivmsg;
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
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", e.getReason());
        message = message.replace("{channel}", e.getChannel().getName());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onKick(KickEvent e) {
        String message = Config.itbKick;
        message = message.replace("{name}", e.getRecipient().getNick());
        message = message.replace("{message}", e.getReason());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{kicker}", e.getSource().getNick());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

    @Override
    public void onQuit(QuitEvent e) {
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itbQuit;
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", e.getReason());
        message = replaceVarsGeneric(e, message);
        plugin.sendToMinecraft(message);
    }

}
