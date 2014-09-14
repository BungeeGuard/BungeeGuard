package fr.greenns.BungeeGuard.Party;

import fr.greenns.BungeeGuard.Main;
import fr.greenns.BungeeGuard.MultiBungee.PubSub.PubSubBase;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 21:25
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyOwnerSetHandler extends PubSubBase {
    Main plugin;

    public PartyOwnerSetHandler(Main plugin) {
        {
            this.plugin = plugin;
        }
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String partyName = args[0];
        UUID u = UUID.fromString(args[1]);
        Party p = plugin.getPM().getParty(partyName);
        if (p == null)
            return;
        p.setOwner(u);
    }
}
