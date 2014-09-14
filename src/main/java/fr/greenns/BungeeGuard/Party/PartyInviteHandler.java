package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.PubSubBase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:23
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyInviteHandler extends PubSubBase {
    Main plugin;

    public PartyInviteHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        // party.getName(), "" + joueur
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party party = plugin.getPM().getParty(partyName);
        party.addInvitation(u);

        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(u);
        if (p == null)
            return;

        TextComponent TC = new TextComponent("Ceci est une invitation à rejoindre la Party ");
        TC.setColor(ChatColor.GRAY);
        TC.addExtra(party.getDisplay());
        p.sendMessage(TC);

        p.sendMessage(new ComponentBuilder("  >> ").color(ChatColor.YELLOW)
                .append("Cliquez ici pour accepter").color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + partyName))
                .append(" << ").color(ChatColor.YELLOW)
                .create());
    }
}
