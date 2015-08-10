/**
 * DRAGONIRC
 * by Andrew "FastLizard4" Adams, TLUL, and the LizardNet CraftBukkit Plugins
 * Development Team (see AUTHORS.txt file)
 *
 * BASED UPON:
 * RoyalIRC by RoyalDev, <https://github.com/RoyalDev/RoyalIRC>, GPL v3
 *
 * Copyright (C) 2015 by Andrew "FastLizard4" Adams, TLUL, and the LizardNet
 * CraftBukkit Plugins Development Team. Some rights reserved.
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

package org.fastlizard4.dragonirc.irclisteners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import org.fastlizard4.dragonirc.Config;
import org.fastlizard4.dragonirc.RUtils;
import org.fastlizard4.dragonirc.DragonIRC;

public class IChatRelay extends ListenerAdapter {

    private final DragonIRC plugin;

    public IChatRelay(DragonIRC instance) {
        plugin = instance;
    }

    @Override
    public void onMessage(MessageEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        if (RUtils.isFantasyCommand(e.getMessage())) return;
        String message = Config.itiMessage;
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
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
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
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
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onKick(KickEvent e) {
        if (!Config.linkChannels) return;
        if (e.getRecipient().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiKick;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
        message = message.replace("{name}", e.getRecipient().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", reason);
        message = message.replace("{kicker}", e.getUser().getNick());
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onPart(PartEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiPart;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{channel}", e.getChannel().getName());
        message = message.replace("{message}", reason);
        plugin.bh.sendMessageToOtherChannels(message, e.getChannel());
    }

    @Override
    public void onQuit(QuitEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiQuit;
        final String reason = (e.getReason().isEmpty()) ? Config.defaultReason : e.getReason();
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
        message = message.replace("{name}", e.getUser().getNick());
        message = message.replace("{message}", reason);
        plugin.bh.sendMessageToOtherServers(message, e.getBot().getConfiguration().getServerHostname());
    }

    @Override
    public void onNickChange(NickChangeEvent e) {
        if (!Config.linkChannels) return;
        if (e.getUser().getNick().equals(e.getBot().getNick())) return;
        String message = Config.itiNick;
        message = message.replace("{server}", e.getBot().getConfiguration().getServerHostname());
        message = message.replace("{name}", e.getOldNick());
        message = message.replace("{newname}", e.getNewNick());
        plugin.bh.sendMessageToOtherServers(message, e.getBot().getConfiguration().getServerHostname());
    }
}
