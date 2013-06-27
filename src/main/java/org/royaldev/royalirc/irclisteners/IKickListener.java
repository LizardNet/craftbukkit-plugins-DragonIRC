package org.royaldev.royalirc.irclisteners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.royaldev.royalirc.Config;

public class IKickListener extends ListenerAdapter {

    @Override
    public void onKick(KickEvent e) {
        if (!Config.rejoinOnKick) return;
        if (!e.getRecipient().getNick().equalsIgnoreCase(e.getBot().getName())) return;
        try {
            Thread.sleep(Config.rejoinWaitTime * 1000L);
        } catch (InterruptedException ignored) {
        }
        e.getBot().joinChannel(e.getChannel().getName());
    }
}
