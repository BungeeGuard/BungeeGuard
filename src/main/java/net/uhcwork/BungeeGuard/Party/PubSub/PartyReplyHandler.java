package net.uhcwork.BungeeGuard.Party.PubSub;

import com.google.gson.reflect.TypeToken;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.PubSub.PubSubBase;
import net.uhcwork.BungeeGuard.Party.Party;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Part of net.uhcwork.BungeeGuard (bungeeguard)
 * Date: 10/09/2014
 * Time: 18:21
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class PartyReplyHandler extends PubSubBase {
    private final Main plugin;

    public PartyReplyHandler(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, String message, String[] args) {
        String data = args[0];
        Type type = new TypeToken<Map<String, Party>>() {
        }.getType();

        plugin.getPM().setParties(plugin.gson.<Map<String, Party>>fromJson(data, type));
    }
}