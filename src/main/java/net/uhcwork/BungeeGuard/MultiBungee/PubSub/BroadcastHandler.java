package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Utils.PrettyLinkComponent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BroadcastHandler {
    @PubSubHandler("broadcast")
    public static void broadcast(PubSubMessageEvent e) {
        Set<String> servers;
        if (e.getArg(0).equalsIgnoreCase("*")) {
            servers = ProxyServer.getInstance().getServers().keySet();
        } else {
            servers = new HashSet<>(Arrays.asList(e.getArg(0).split(";")));
        }
        ServerInfo SI;
        String msg = ChatColor.translateAlternateColorCodes('&', e.getArg(1));
        for (String server : servers) {
            SI = ProxyServer.getInstance().getServerInfo(server);
            if (SI == null)
                return;
            for (ProxiedPlayer p : SI.getPlayers()) {
                p.sendMessage(new TextComponent(" "));
                p.sendMessage(PrettyLinkComponent.fromLegacyText(ChatColor.AQUA + "[" + ChatColor.GREEN + "***" + ChatColor.AQUA + "]" + ChatColor.GRAY + " " + msg));
                p.sendMessage(new TextComponent(" "));
            }
        }
    }
}