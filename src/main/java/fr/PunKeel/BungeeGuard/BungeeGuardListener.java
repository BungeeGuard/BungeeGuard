package fr.PunKeel.BungeeGuard;

import com.imaginarycode.minecraft.redisbungee.events.PlayerJoinedNetworkEvent;
import com.imaginarycode.minecraft.redisbungee.events.PlayerLeftNetworkEvent;
import fr.PunKeel.BungeeGuard.Managers.FriendManager;
import fr.PunKeel.BungeeGuard.Managers.PartyManager;
import fr.PunKeel.BungeeGuard.Managers.ServerManager;
import fr.PunKeel.BungeeGuard.Models.BungeeBan;
import fr.PunKeel.BungeeGuard.Models.BungeeLitycs;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import fr.PunKeel.BungeeGuard.Permissions.Group;
import fr.PunKeel.BungeeGuard.Permissions.Permissions;
import fr.PunKeel.BungeeGuard.Persistence.SaveRunner;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import fr.PunKeel.BungeeGuard.Utils.ArrayUtils;
import net.md_5.bungee.api.*;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BungeeGuardListener implements Listener {
    private static final ServerPing.PlayerInfo[] playersPing;
    private static final Map<UUID, BungeeLitycs> bungeelitycs = new ConcurrentHashMap<>();
    private static final String BASE_MOTD = "           §f§l» §b§lUHCGames§6§l.com §a§l[BETA] §f§l«\n";
    private static final String maintenance
            = ChatColor.RED + "Une maintenance est actuellement en cours.\n" +
            ChatColor.RED + "Merci de repasser plus tard.\n" +
            ChatColor.GOLD + "UHCGames";
    private static final String MOTD_FULL = "" + ChatColor.RED + ChatColor.BOLD + "[Serveur PLEIN] " +
            ChatColor.YELLOW + ChatColor.BOLD + "Accessible aux VIP et plus !";

    private static final String fullNotVIP = "" + ChatColor.YELLOW + ChatColor.BOLD + "Le serveur est plein" +
            ChatColor.GOLD + ChatColor.BOLD + "\nVous pourrez le rejoindre en devenant VIP !" +
            ChatColor.RED + ChatColor.BOLD + "\nAchetez-le sur " +
            ChatColor.WHITE + ChatColor.BOLD + "https://store.uhcgames.com/";

    private static final String FRIEND_LOGIN = ChatColor.AQUA + "[" + ChatColor.RED + "❤" + ChatColor.AQUA + "] " + ChatColor.YELLOW + "%s" + ChatColor.AQUA + " vient de se connecter.";
    private static final String FRIEND_LOGOUT = ChatColor.AQUA + "[" + ChatColor.RED + "❤" + ChatColor.AQUA + "] " + ChatColor.YELLOW + "%s" + ChatColor.RED + " vient de se déconnecter.";
    private static final BaseComponent[] header = new ComponentBuilder("Vous jouez sur ")
            .color(ChatColor.AQUA)
            .append("UHCGames")
            .color(ChatColor.GOLD)
            .append(" - ")
            .color(ChatColor.AQUA)
            .append("mc.uhcgames.com")
            .color(ChatColor.RED)
            .create();
    private static final BaseComponent[] footer = new ComponentBuilder("Boutique: ")
            .color(ChatColor.GREEN)
            .append("store.uhcgames.com")
            .color(ChatColor.GOLD)
            .create();
    private static final String MOTD_MAINTENANCE = ChatColor.BLACK + "        " +
            ChatColor.WHITE + ChatColor.BOLD + "» " +
            ChatColor.RED + ChatColor.UNDERLINE + ChatColor.BOLD + "Serveur en maintenance" +
            ChatColor.WHITE + ChatColor.BOLD + " «";
    private static final String EMPTY_MESSAGE = " ";
    private static final String WELCOME_MSG_1 = ChatColor.GOLD + "Bienvenue sur UHCGames, %s";
    private static final String WELCOME_MSG_2 = ChatColor.YELLOW + "IP: " + ChatColor.GREEN + "mc.uhcgames.com" +
            ChatColor.GRAY + " | " +
            ChatColor.YELLOW + "TeamSpeak: " + ChatColor.GREEN + "ts.uhcgames.com";
    private static final String WELCOME_MSG_3 = ChatColor.YELLOW + "Site: " + ChatColor.GREEN + "www.uhcgames.com" +
            ChatColor.GRAY + " | " +
            ChatColor.YELLOW + "Boutique: " + ChatColor.GREEN + "store.uhcgames.com";
    private static final int PROTOCOL_MC_18_VERSION = 47;

    static {
        List<String> lines = new ArrayList<>();
        lines.add(ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         " + ChatColor.RESET + "" + ChatColor.BOLD + "«" + ChatColor.GOLD + "" + ChatColor.BOLD + " UHC " + ChatColor.AQUA + "" + ChatColor.BOLD + "Network " + ChatColor.RESET + "" + ChatColor.BOLD + "»" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "         ");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Un serveur de jeux UltraHardCore !");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "  Stress, Difficulté, Travail d'équipe");
        lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "      Vous allez aimer UHCGames !");
        lines.add(ChatColor.GRAY + " ");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.RED + "Kill The Patrick");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.YELLOW + "Ultra Hunger Games");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.BLUE + "Rush");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.AQUA + "Fatality");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.LIGHT_PURPLE + "Tower");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.GREEN + "Monster Defense");
        lines.add(ChatColor.GRAY + "➟ " + ChatColor.GOLD + "UltraLucky");


        ServerPing.PlayerInfo[] players = new ServerPing.PlayerInfo[lines.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = new ServerPing.PlayerInfo(lines.get(i), "");
        }
        playersPing = players;
    }

    final ServerManager SM;
    private final Main plugin;
    private final Method handshakeMethod;
    private final Set<UUID> firstJoin = new HashSet<>();

    public BungeeGuardListener(final Main plugin) {
        this.plugin = plugin;
        Method handshake;
        try {
            Class<?> initialHandler = Class.forName("net.md_5.bungee.connection.InitialHandler");
            handshake = initialHandler.getDeclaredMethod("getHandshake");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            handshake = null;
        }
        handshakeMethod = handshake;
        SM = Main.getServerManager();
    }


    @EventHandler
    public void onLogin(final LoginEvent event) {
        UUID user = event.getConnection().getUniqueId();
        if (Main.getMB().isPlayerOnline(user) && Main.getMB().getServerFor(user) == null) {
            Main.getMB().kickPlayer(user, "");
        }

        if (Main.getMB().getPlayerCount() > plugin.getConfig().getMaxPlayers()) {
            if (!Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.join_full")) {
                event.setCancelled(true);
                event.setCancelReason(fullNotVIP);
                return;
            }
        }
        if (plugin.isMaintenance() && !Permissions.hasPerm(event.getConnection().getUniqueId(), "bungee.can.join_maintenance")) {
            event.setCancelled(true);
            event.setCancelReason(maintenance);
            return;
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
                event.setCancelled(true);
                event.setCancelReason(ban.getBanMessage());
                return;
            }
        }
        if (event.getConnection().getVersion() < PROTOCOL_MC_18_VERSION) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "UHCGames nécessite la version " + ChatColor.RED + ChatColor.BOLD + "1.8" + ChatColor.AQUA + " de Minecraft pour jouer !");
            return;
        }
        firstJoin.add(event.getConnection().getUniqueId());
    }

    private void showWelcomeTitle(ProxiedPlayer p) {
        Title title = ProxyServer.getInstance().createTitle();
        title.fadeOut(25);
        title.title(TextComponent.fromLegacyText(ChatColor.GOLD + "UHCGames"));
        String welcomeTitle = ArrayUtils.rand(plugin.getConfig().getWelcomeSubtitles());
        if (welcomeTitle != null && !welcomeTitle.isEmpty()) {
            title.subTitle(TextComponent.fromLegacyText(welcomeTitle));
            title.send(p);
        }
        p.sendMessage(TextComponent.fromLegacyText(EMPTY_MESSAGE));
        Group g = plugin.getPermissionManager().getMainGroup(p.getUniqueId());
        p.sendMessage(TextComponent.fromLegacyText(String.format(WELCOME_MSG_1, g.getChatPrefix() + p.getName())));
        p.sendMessage(TextComponent.fromLegacyText(WELCOME_MSG_2));
        p.sendMessage(TextComponent.fromLegacyText(WELCOME_MSG_3));
        p.sendMessage(TextComponent.fromLegacyText(EMPTY_MESSAGE));
    }

    @EventHandler
    public void onServerConnect(final ServerConnectEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (firstJoin.contains(p.getUniqueId())) {
            showWelcomeTitle(p);
            ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
                @Override
                public void run() {
                    notifyFriends(p.getUniqueId(), TextComponent.fromLegacyText(String.format(FRIEND_LOGIN, p.getName())));
                    plugin.getFriendManager().sendJoinMessage(p);
                }
            });
            firstJoin.remove(p.getUniqueId());
        }
        p.setTabHeader(header, footer);
        if (e.getTarget().getName().equalsIgnoreCase("hub")) {
            String l = Main.getServerManager().getBestLobbyFor(p);
            if (l != null) {
                e.setTarget(plugin.getProxy().getServerInfo(l));
            } else {
                e.getPlayer().disconnect(new ComponentBuilder(ChatColor.RED + "Nos services sont momentanément indisponibles" + '\n' + ChatColor.RED + "Veuillez réessayer dans quelques instants").create());
            }
        } else if (!e.getTarget().getName().startsWith("lobby")) {
            final PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && party.isOwner(p)) {
                Main.getMB().summonParty(party.getOwner(), e.getTarget().getName());
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
        if (!e.isCommand()) {
            BungeeMute mute = plugin.getSanctionManager().findMute(p.getUniqueId());
            if (mute != null) {
                p.sendMessage(TextComponent.fromLegacyText(mute.getMuteMessage()));
                e.setCancelled(true);
                return;
            }
        }
        if (!p.hasPermission("bungee.can.repeat_message") && !e.isCommand()) {
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
        if (!e.isCommand()) {

            PartyManager.Party party = plugin.getPartyManager().getPartyByPlayer(p);
            if (party != null && e.getMessage().startsWith("*")) {
                Main.getMB().partyChat(party.getOwner(), p.getUniqueId(), e.getMessage().substring(1));
                e.setCancelled(true);
                return;
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
        notifyFriends(p.getUniqueId(), TextComponent.fromLegacyText(String.format(FRIEND_LOGOUT, p.getName())));
        SM.resetLastLobby(p.getUniqueId());
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e) {
        ServerPing sp = e.getResponse();
        InetSocketAddress virtualHost = e.getConnection().getVirtualHost();
        if (virtualHost != null) {
            String hostname = virtualHost.getHostName().replace(".popcorp.eu", "");
            if (hostname != null) {
                if (hostname.endsWith(".info.uhcwork.net")) {
                    String serverType = hostname.replace(".info.uhcwork.net", "");
                    sp.getPlayers().setOnline(Main.getServerManager().getPlayersOn(serverType + "*").size());
                    e.setResponse(sp);
                    return;
                }
            }
        }
        if (plugin.isMaintenance()) {
            sp.getPlayers().setMax(0);
            sp.getPlayers().setOnline(0);
            sp.getPlayers().setSample(new ServerPing.PlayerInfo[]{});
            sp.setDescription(BASE_MOTD + MOTD_MAINTENANCE);
            sp.setVersion(new ServerPing.Protocol(ChatColor.BOLD + "En maintenance", -42));
        } else {
            sp.getPlayers().setMax(plugin.getConfig().getMaxPlayers());
            sp.getPlayers().setOnline(Main.getMB().getPlayerCount());
            if (sp.getPlayers().getOnline() >= sp.getPlayers().getMax())
                sp.setDescription(BASE_MOTD + MOTD_FULL);
            else
                sp.setDescription(BASE_MOTD + plugin.getConfig().getMotd());
            e.getResponse().getPlayers().setSample(playersPing);
        }
        e.setResponse(sp); // sp might be a new ServerPing instance
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
            if (server == null) {
                return;
            }
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
        if (e.getCursor().equals("/")) {
            e.setCancelled(true);
            return;
        }

        final String checked = (args.length > 0 ? args[args.length - 1] : e.getCursor()).toLowerCase();

        if (checked.length() <= 1)
            return;

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

    @EventHandler
    public void onConnected(final ServerConnectedEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        plugin.getPluginMessageManager().sendPartyInfo(p, e.getServer());
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
            if (SM.isLobby(e.getTarget()) || e.getTarget().getName().startsWith("dev")) {
                data.put("first-join", SM.setLastLobby(p.getUniqueId(), e.getTarget()));
                data.put("disabled_mp", plugin.getIgnoreManager().playerIgnores(p.getUniqueId(), null));
            }
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

    @EventHandler
    public void onConnect(PlayerJoinedNetworkEvent e) {
        UUID u = e.getUuid();
        String username = Main.getMB().getNameFromUuid(u);
        notifyFriends(u, TextComponent.fromLegacyText(String.format(FRIEND_LOGIN, username)));
    }

    @EventHandler
    public void onDisconnect(PlayerLeftNetworkEvent e) {
        UUID u = e.getUuid();
        String username = Main.getMB().getNameFromUuid(u);
        notifyFriends(u, TextComponent.fromLegacyText(String.format(FRIEND_LOGOUT, username)));
    }

    private void notifyFriends(UUID u, BaseComponent[] message) {
        ProxyServer server = plugin.getProxy();
        for (UUID friend : plugin.getFriendManager().getFriends(u, FriendManager.STATE.MUTUAL)) {
            ProxiedPlayer p = server.getPlayer(friend);
            if (p == null)
                continue;
            p.sendMessage(ChatMessageType.ACTION_BAR, message);
            p.sendMessage(message);
        }
    }
}
