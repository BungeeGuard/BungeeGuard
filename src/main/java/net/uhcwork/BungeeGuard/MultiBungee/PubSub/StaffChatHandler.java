package net.uhcwork.BungeeGuard.MultiBungee.PubSub;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubHandler;
import net.uhcwork.BungeeGuard.MultiBungee.PubSubMessageEvent;
import net.uhcwork.BungeeGuard.Permissions.Group;
import net.uhcwork.BungeeGuard.Utils.MyBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Part of net.uhcwork.BungeeGuard.MultiBungee.PubSub (bungeeguard)
 * Date: 07/09/2014
 * Time: 18:46
 * May be open-source & be sold (by mguerreiro, of course !)
 */
public class StaffChatHandler {
    @PubSubHandler("staffChat")
    public static void staffChat(Main plugin, PubSubMessageEvent e) {
        String serverName = e.getArg(0);
        String senderName = e.getArg(1);
        String message = ChatColor.translateAlternateColorCodes('&', e.getArg(2));
        Group g = plugin.getPermissionManager().getMainGroup(senderName);

        BaseComponent[] wholeMessage = new MyBuilder(ChatColor.RED + "[")
                .append(ChatColor.RED + Main.getServerManager().getPrettyName(serverName))
                .append(ChatColor.RED + "]")
                .append(g.getColor() + " ■ ").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(g.getColor() + g.getName())))
                .append(ChatColor.RED + senderName + ": ")
                .append(ChatColor.RED + message)
                .create();

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("bungee.staffchat")) {
                player.sendMessage(wholeMessage);
            }
        }
    }

    @PubSubHandler("notifyStaff")
    public static void notifyStaff(Main plugin, PubSubMessageEvent e) {
        for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            if (p.hasPermission("bungee.notify")) {
                p.sendMessage(TextComponent.fromLegacyText(e.getMessage()));
            }
        }
    }

    @PubSubHandler("gtp")
    public static void gtp(final Main plugin, PubSubMessageEvent e) {
        String playerName = e.getArg(0);
        final String to_player = e.getArg(1);

        final MultiBungee MB = Main.getMB();
        final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);

        if (p == null) {
            return;
        }
        ServerInfo SI = MB.getServerFor(to_player);
        p.connect(SI, new Callback<Boolean>() {
            @Override
            public void done(Boolean success, Throwable throwable) {
                if (!success) {
                    return;
                }
                ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (p.hasPermission("bukkit.command.teleport") || p.hasPermission("minecraft.command.tp")) {
                            p.chat("/tp " + p.getName() + " " + to_player);
                        } else {
                            MB.runCommand(to_player, "/tp " + p.getName() + " " + to_player);
                        }
                    }
                }, 100, TimeUnit.MILLISECONDS);
            }
        });
    }

    @PubSubHandler("runCommand")
    public static void runCommand(final Main plugin, PubSubMessageEvent e) {
        String playerName = e.getArg(0);
        final String command = e.getArg(1);

        final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(playerName);

        if (p == null) {
            return;
        }

        p.chat(command);
    }

    @PubSubHandler("maintenance")
    public static void setMaintenance(final Main plugin, PubSubMessageEvent e) {
        String serverName = e.getArg(0);
        boolean restricted = e.getArg(1).equals("+");
        Main.getServerManager().setRestricted(serverName, restricted);
    }
}
