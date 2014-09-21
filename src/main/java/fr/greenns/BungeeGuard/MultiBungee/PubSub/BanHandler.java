package fr.greenns.BungeeGuard.MultiBungee.PubSub;

import fr.greenns.BungeeGuard.Ban.BanManager;
import fr.greenns.BungeeGuard.Main;

import java.util.UUID;

/**
 * Part of fr.greenns.BungeeGuard.Ban
 * Date: 30/08/2014
 * Time: 15:46
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class BanHandler extends PubSubBase {
    @Override
    public void handle(String channel, String message, String[] args) {
        BanManager BM = Main.plugin.getBM();
        BM.ban(UUID.fromString(args[1]), args[2], Long.parseLong(args[3]), args[4], args[5],
                UUID.fromString(args[6]), false);
    }

    @Override
    public boolean ignoreSelfMessage() {
        return true;
    }
}
