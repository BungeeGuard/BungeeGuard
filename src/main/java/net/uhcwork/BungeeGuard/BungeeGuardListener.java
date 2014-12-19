package net.uhcwork.BungeeGuard;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.packet.Handshake;
import net.uhcwork.BungeeGuard.Managers.PartyManager;
import net.uhcwork.BungeeGuard.Managers.ServerManager;
import net.uhcwork.BungeeGuard.Models.BungeeBan;
import net.uhcwork.BungeeGuard.Models.BungeeLitycs;
import net.uhcwork.BungeeGuard.Models.BungeeMute;
import net.uhcwork.BungeeGuard.Permissions.Permissions;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;
import net.uhcwork.BungeeGuard.Persistence.VoidRunner;
import net.uhcwork.BungeeGuard.Utils.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BungeeGuardListener implements Listener {
    private static final ServerPing.PlayerInfo[] playersPing;
    private static final Map<UUID, BungeeLitycs> bungeelitycs = new ConcurrentHashMap<>();
    private static final String BASE_MOTD = "           §f§l» §b§lUHCGames§6§l.com §a§l[BETA] §f§l«\n";
    private final Main plugin;
    private final BaseComponent[] header = new ComponentBuilder("MC.UHCGames.COM")
            .color(ChatColor.GOLD)
            .bold(true).create();
    private final BaseComponent[] footer = new ComponentBuilder("Store")
            .color(ChatColor.RED)
            .bold(true)
            .append(".UHCGames.com")
            .bold(true)
            .color(ChatColor.AQUA).create();
    ServerManager SM;

    static {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         " + ChatColor.RESET + "" + ChatColor.BOLD + "«" + ChatColor.GOLD + "" + ChatColor.BOLD + " UHC " + ChatColor.AQUA + "" + ChatColor.BOLD + "Network " + ChatColor.RESET + "" + ChatColor.BOLD + "»" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         ");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Un serveur de jeux UltraHardCore !");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "  Stress, Difficulté, Travail d'équipe");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "      Vous allez aimer UHCGames !");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.RED + "Kill The Patrick");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.YELLOW + "Ultra HungerGames");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.BLUE + "Rush");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.AQUA + "Fatality");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.LIGHT_PURPLE + "Tower");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.GREEN + "Monster Defense");
        lines.add(ChatColor.GRAY + "Et bien d'autres jeux ...");


        ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ServerPing.PlayerInfo(lines.get(i), "");
        }
        playersPing = players;
    }

    private String fullNotVIP = "" + ChatColor.YELLOW + ChatColor.BOLD + "Le serveur est plein" +
            ChatColor.GOLD + ChatColor.BOLD + "\nVous pourrez le rejoindre en devenant VIP !" +
            ChatColor.RED + ChatColor.BOLD + "\nAchetez-le sur " +
            ChatColor.WHITE + ChatColor.BOLD + "https://store.uhcgames.com/";
    private Method handshakeMethod = null;
    private Set<UUID> firstJoin = new HashSet<>();

    public BungeeGuardListener(Main plugin) {
        this.plugin = plugin;
        try {
            Class<?> initialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
            handshakeMethod = initialHandler.getDeclaredMethod("getHandshake");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        SM = Main.getServerManager();
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        if (Main.getMB().getPlayerCount() > plugin.getConfig().getMaxPlayers()) {
            if (!Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.join_full")) {
                event.setCancelled(true);
                event.setCancelReason(fullNotVIP);
                return;
            }
        }
        String hostString = event.getConnection().getVirtualHost().getHostString().toLowerCase();
        if (!Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.can.bypass_host") &&
                !plugin.getConfig().getForcedHosts().containsKey(hostString)) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "" + ChatColor.BOLD + "Merci de vous connecter avec " + '\n' + ChatColor.WHITE + "" + ChatColor.BOLD + "MC" + ChatColor.AQUA + "" + ChatColor.BOLD + ".uhcgames.com");
        } else {
            ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getWalletManager().getAccount(event.getConnection().getUniqueId());
                }
            }, 10, TimeUnit.MILLISECONDS);

            BungeeBan ban = plugin.getSanctionManager().findBan(event.getConnection().getUniqueId());
            if (ban != null) {
                if (ban.isBanned()) {
                    event.setCancelled(true);
                    event.setCancelReason(ban.getBanMessage());
                    return;
                }
                plugin.getSanctionManager().unban(ban, "TimeEnd", "Automatique", true);
                Main.getMB().unban(event.getConnection().getUniqueId());
            }
        }
        firstJoin.add(event.getConnection().getUniqueId());
    }

    private void showWelcomeTitle(ProxiedPlayer p) {
        Title title = ProxyServer.getInstance().createTitle();
        title.fadeOut(25);
        title.title(TextComponent.fromLegacyText(ChatColor.GOLD + "UHCGames"));
        title.subTitle(TextComponent.fromLegacyText(ArrayUtils.rand(plugin.getConfig().getWelcomeSubtitles())));
        title.send(p);
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (firstJoin.contains(p.getUniqueId())) {
            showWelcomeTitle(p);
            firstJoin.remove(p.getUniqueId());
        }
        p.setTabHeader(header, footer);
        if (e.getTarget().getName().equalsIgnoreCase("hub")) {
            System.out.println("Recuperation du meilleur lobby pour " + p.getName());
            String l = Main.getServerManager().getBestLobbyFor(p);
            if (l != null) {
                e.setTarget(plugin.getProxy().getServerInfo(l));
                System.out.println("Lobby selectionné: " + l);
            } else {
                if (ProxyServer.getInstance().getServerInfo("limbo").getPlayers().size() < 70) {
                    e.setTarget(ProxyServer.getInstance().getServerInfo("limbo"));
                }
                e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants").create());
            }
        } else if (!e.getTarget().getName().startsWith("lobby")) {
            final PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && party.isOwner(p)) {
                Main.getMB().summonParty(party.getName(), e.getTarget().getName());
            }
        }
        if ((p.getServer() != null && p.getServer().getInfo().equals(e.getTarget())) || !e.getTarget().canAccess(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        String lowerMessage = e.getMessage().toLowerCase();
        if (lowerMessage.startsWith("connected with") && lowerMessage.endsWith("minechat")) {
            e.setCancelled(true);
            return;
        }
        if (!p.hasPermission("bungee.can.repeat_message")) {
            plugin.getAntiSpamListener().onChat(e);
        }
        if (!p.hasPermission("bungee.admin")) {
            if (Permissions.miniglob(plugin.getForbiddenCommands(), lowerMessage)) {
                e.setMessage("");
                e.setCancelled(true);
                return;
            }
        }
        if (e.isCancelled())
            return;
        if (!lowerMessage.startsWith("/") && (e.getSender() instanceof ProxiedPlayer)) {
            if (e.isCommand()) {
                return;
            }
            BungeeMute mute = plugin.getSanctionManager().findMute(p.getUniqueId());
            if (mute != null) {
                if (mute.isMute()) {
                    p.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
                    e.setCancelled(true);
                    return;
                } else {
                    plugin.getSanctionManager().unmute(mute, "TimeEnd", "Automatique", true);
                    Main.getMB().unmutePlayer(p.getUniqueId());
                }
            }
            if (p.hasPermission("bungee.staffchat")) {
                boolean isDefault = p.hasPermission("bungee.staffchat.default");
                if (e.getMessage().startsWith("!!") != isDefault) {
                    // Active staffchat si "!!message" et pas sur lobby
                    // ou si "message" et sur lobby (équivaut à un XOR mais en plus propre)l
                    e.setCancelled(true);
                    String message;
                    if (!isDefault)
                        message = e.getMessage().substring(2);
                    else
                        message = e.getMessage();
                    Main.getMB().staffChat(p.getServer().getInfo().getName(), p.getName(), message);
                    e.setMessage("");

                    return;
                }
                if (isDefault)
                    e.setMessage(e.getMessage().substring(2));
            }

            PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && party.isPartyChat(p)) {
                Main.getMB().partyChat(party.getName(), p.getUniqueId(), e.getMessage());
                e.setCancelled(true);
            }
            if (plugin.isSilenced(p.getServer().getInfo().getName())) {
                if (!p.hasPermission("bungee.bypasschat")) {
                    e.setCancelled(true);
                    p.sendMessage(new TextComponent(ChatColor.RED + "Le chat est désactivé temporairement !"));
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (plugin.getPartyManager().inParty(p)) {
            Main.getMB().playerLeaveParty(plugin.getPartyManager().getPartyByPlayer(p), p);
        }
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            final BungeeLitycs old_bl = bungeelitycs.get(p.getUniqueId());
            old_bl.leave(p);
            plugin.executePersistenceRunnable(new SaveRunner(old_bl));
            bungeelitycs.remove(p.getUniqueId());
        }
        SM.resetLastLobby(p.getUniqueId());
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e) {
        ServerPing sp = e.getResponse();
        sp.getPlayers().setMax(plugin.getConfig().getMaxPlayers());
        sp.getPlayers().setOnline(Main.getMB().getPlayerCount());
        sp.setDescription(BASE_MOTD + plugin.getConfig().getMotd());
        e.getResponse().getPlayers().setSample(playersPing);
        e.setResponse(sp);
    }

    @EventHandler
    public void onKick(final ServerKickEvent e) {
        ProxiedPlayer p = e.getPlayer();
        String reason = "";
        ServerInfo kickedFrom = e.getKickedFrom();

        for (BaseComponent b : e.getKickReasonComponent()) {
            reason += b.toPlainText() + "\n";
        }
        reason = reason.trim();

        if (!(reason.contains("ban") || reason.contains("Full") || reason.contains("fly") ||
                reason.contains("Nos services") || reason.contains("kické") ||
                reason.contains("bannis") || reason.contains("maintenance") ||
                reason.contains("kick") || reason.contains("VIP"))) {

            if (reason.contains("closed")) {
                Main.getServerManager().setOffline(kickedFrom.getName());
            }
            String l = Main.getServerManager().getBestLobbyFor(p);
            ServerInfo server = plugin.getProxy().getServerInfo(l);
            p.setReconnectServer(server);

            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "[BungeeGuard] " + p.getName() + " a perdu la connection (" + e.getState().toString() + " - " + reason + ")"));
            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "[BungeeGuard] " + p.getName() + " Redirigé vers " + Main.getServerManager().getPrettyName(server.getName())));

            p.setReconnectServer(server);
            e.setCancelled(true);
            e.setCancelServer(server);
            return;
        } else {
            p.disconnect(e.getKickReasonComponent());
        }
        if (e.isCancelled())
            return;
        if (plugin.getPartyManager().inParty(p)) {
            Main.getMB().playerLeaveParty(plugin.getPartyManager().getPartyByPlayer(p), p);
        }
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            final BungeeLitycs old_bl = bungeelitycs.get(p.getUniqueId());
            old_bl.leave(p);
            plugin.executePersistenceRunnable(new SaveRunner(old_bl));
            bungeelitycs.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onTabCompleteEvent(TabCompleteEvent e) {
        String[] args = e.getCursor().split(" ");

        final String checked = (args.length > 0 ? args[args.length - 1] : e.getCursor()).toLowerCase();
        for (String playerName : Main.getMB().getHumanPlayersOnline()) {
            if (playerName.toLowerCase().startsWith(checked)) {
                e.getSuggestions().add(playerName);
            }
        }
    }

    @EventHandler
    public void onPermCheck(PermissionCheckEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            boolean hasPerm;
            if (e.getPermission().startsWith("bungeecord.server.")) {
                String serverName = e.getPermission().substring("bungeecord.server.".length());
                hasPerm = !Main.getServerManager().isRestricted(serverName) || Permissions.hasPerm(p.getUniqueId(), "bungee.server." + serverName);
            } else
                hasPerm = Permissions.hasPerm(p.getUniqueId(), e.getPermission());
            e.setHasPermission(hasPerm);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (e.isCancelled())
            return;

        try {
            Handshake h = (Handshake) handshakeMethod.invoke(p.getPendingConnection());
            Map<String, Object> data = new HashMap<>();
            data.put("server_id", e.getTarget().getName());
            data.put("groupes", plugin.getPermissionManager().getGroupes(p.getUniqueId()));
            if (SM.isLobby(e.getTarget()))
                data.put("first-join", SM.setLastLobby(p.getUniqueId(), e.getTarget()));
            h.setHost(Main.getGson().toJson(data));
        } catch (IllegalAccessException | InvocationTargetException e1) {
            System.out.println("Erreur passage hostname: " + e1.getMessage());
        }
        final BungeeLitycs old_bl;
        if (bungeelitycs.containsKey(p.getUniqueId())) {
            old_bl = bungeelitycs.get(p.getUniqueId());
            bungeelitycs.remove(p.getUniqueId());
        } else
            old_bl = null;
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                if (old_bl != null) {
                    old_bl.leave(p);
                    old_bl.saveIt();
                }
                BungeeLitycs bl = new BungeeLitycs();
                bl.join(p, e.getTarget());
                bl.saveIt();
                bungeelitycs.put(p.getUniqueId(), bl);
            }
        });
    }
}
