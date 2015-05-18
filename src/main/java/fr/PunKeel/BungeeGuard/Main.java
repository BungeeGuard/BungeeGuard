package fr.PunKeel.BungeeGuard;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.io.Files;
import com.google.gson.Gson;
import fr.PunKeel.BungeeGuard.Announces.AnnouncementManager;
import fr.PunKeel.BungeeGuard.Announces.AnnouncementTask;
import fr.PunKeel.BungeeGuard.BanHammer.AntiSpamListener;
import fr.PunKeel.BungeeGuard.Commands.*;
import fr.PunKeel.BungeeGuard.Config.MysqlConfigAdapter;
import fr.PunKeel.BungeeGuard.Managers.*;
import fr.PunKeel.BungeeGuard.Models.BungeeBlockedCommands;
import fr.PunKeel.BungeeGuard.MultiBungee.MultiBungee;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSub.ReloadConfHandler;
import fr.PunKeel.BungeeGuard.MultiBungee.RedisBungeeListener;
import fr.PunKeel.BungeeGuard.PluginMessage.PluginMessageListener;
import fr.PunKeel.BungeeGuard.Utils.MyReconnectHandler;
import fr.PunKeel.BungeeGuard.Utils.SentryHandler;
import fr.PunKeel.BungeeGuard.Utils.ShopTask;
import lombok.Getter;
import net.kencochrane.raven.DefaultRavenFactory;
import net.kencochrane.raven.Raven;
import net.kencochrane.raven.RavenFactory;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.javalite.activejdbc.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main extends Plugin {
    public static final BaseComponent[] SEPARATOR = TextComponent.fromLegacyText(ChatColor.YELLOW + "-----------------------------------------------------");
    public static final String ADMIN_TAG = ChatColor.RED + "[BungeeGuard] " + ChatColor.RESET;
    public static final String API_TOKEN;
    public static final String API_ENDPOINT;
    private static final List<String> forbiddenCommands = new ArrayList<>();
    @Getter
    private static final MultiBungee MB = new MultiBungee();
    private static final Map<UUID, UUID> reply = new HashMap<>();
    private static final List<UUID> spy = new ArrayList<>();
    @Getter
    private static final Random random = new Random();
    public static Main plugin;
    @Getter
    public static Gson gson = new Gson();
    @Getter
    static ServerManager serverManager;

    static {
        API_TOKEN = getEnv("API_TOKEN", null, null);
        API_ENDPOINT = getEnv("API_ENDPOINT", "https://forum.uhcgames.com/api_register.php", null);
    }

    @Getter
    final FriendManager friendManager = new FriendManager(this);
    private final List<String> silencedServers = new ArrayList<>();
    @Getter
    private final SanctionManager sanctionManager = new SanctionManager(this);
    @Getter
    PermissionManager permissionManager = new PermissionManager(this);
    @Getter
    CheatManager cheatManager = new CheatManager(this);
    @Getter
    PluginMessageManager pluginMessageManager = new PluginMessageManager(this);
    @Getter
    MysqlConfigAdapter config;
    private long startTime;
    @Getter
    private PartyManager partyManager = new PartyManager();
    @Getter
    private IgnoreManager ignoreManager = new IgnoreManager(this);
    @Getter
    private AntiSpamListener antiSpamListener = new AntiSpamListener();
    @Getter
    private AnnouncementManager announcementManager = new AnnouncementManager();
    @Getter
    private WalletManager walletManager = new WalletManager(this);
    private String MYSQL_USER, MYSQL_HOST, MYSQL_DATABASE, MYSQL_PASS;

    public static void missPermission(CommandSender sender, String permissionNode) {
        if (!sender.hasPermission("bungee.debug.permissions")) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Permission refusée"));
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Permission refusée (" + ChatColor.WHITE + permissionNode + ChatColor.RED + ")"));
    }

    public static Logger logger() {
        return Main.plugin.getLogger();
    }

    private static String getEnv(String name, String def, Properties prop) {
        String property = (prop == null) ? def : prop.getProperty(name, def);
        try {
            return MoreObjects.firstNonNull(System.getenv(name), property);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public List<String> getForbiddenCommands() {
        return forbiddenCommands;
    }

    public void setForbiddenCommands(List<BungeeBlockedCommands> all) {
        forbiddenCommands.clear();
        for (BungeeBlockedCommands cmd : all) {
            forbiddenCommands.add(cmd.getCommand());
        }
    }

    public <T> Future<T> executePersistenceRunnable(final Callable<T> callable) {
        FutureTask<T> F = new FutureTask<>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                T value = null;
                try {
                    plugin.getLogger().finest("[ORM] Creation de la connexion SQL pour " + Thread.currentThread().toString() + " ... :)");
                    setup();
                    value = callable.call();
                } finally {
                    plugin.getLogger().finest("[ORM] Fermeture pour " + Thread.currentThread().toString() + " ... :D");
                    cleanup();
                }
                return value;
            }

            private void setup() {
                if (Base.hasConnection())
                    return;
                Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://" + MYSQL_HOST + "/" + MYSQL_DATABASE, MYSQL_USER, MYSQL_PASS);
            }

            private void cleanup() {
                Base.close();
            }
        });
        getProxy().getScheduler().runAsync(this, F);
        return F;
    }

    @Override
    public void onLoad() {
        plugin = this;
        startTime = System.currentTimeMillis();
        serverManager = new ServerManager(this);
        Properties prop = null;
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                prop = new Properties();
                BufferedReader reader = Files.newReader(configFile, Charsets.UTF_8);
                prop.load(reader);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // RAVEN
        String SENTRY_ENDPOINT = getEnv("SENTRY_ENDPOINT", null, prop);
        if (SENTRY_ENDPOINT != null) {
            RavenFactory.registerFactory(new DefaultRavenFactory());
            Raven raven = RavenFactory.ravenInstance(SENTRY_ENDPOINT);
            ProxyServer.getInstance().getLogger().addHandler(new SentryHandler(raven, getDescription().getVersion()));
        }
        // END RAVEN

        MYSQL_HOST = getEnv("MYSQL_HOST", "localhost", prop);
        MYSQL_DATABASE = getEnv("MYSQL_DATABASE", "plugin", prop);
        MYSQL_USER = getEnv("MYSQL_USER", "root", prop);
        MYSQL_PASS = getEnv("MYSQL_PASS", "", prop);


        if (MYSQL_HOST.isEmpty() || MYSQL_DATABASE.isEmpty() || MYSQL_USER.isEmpty() || MYSQL_PASS.isEmpty()) {
            ProxyServer.getInstance().stop();
            throw new RuntimeException("La configuration est mauvaise, chef.");
        }

        new BungeeGuardUtils(this);
        System.out.println("Welcome to BungeeGuard ~ Crafted with " + ChatColor.RED + "♥");
        getProxy().setReconnectHandler(new MyReconnectHandler());
        config = new MysqlConfigAdapter(this);
        config.load(true);
        getProxy().setConfigurationAdapter(config);
    }

    private void fetchParties() {
        List<String> server = MB.getAllServers();
        partyManager = new PartyManager();
        for (String s : server) {
            if (!s.equals(MB.getServerId())) {
                System.out.println("RequestParties: " + s);
                MB.requestParties(s);

                System.out.println("RequestIgnores: " + s);
                MB.requestIgnores(s);
                return;
            }
        }

    }

    @Override
    public void onEnable() {
        MB.init();

        sanctionManager.loadBans();
        sanctionManager.loadMutes();
        friendManager.loadFriends();
        serverManager.setupPingTask();

        getProxy().getPluginManager().registerListener(this, new BungeeGuardListener(this));
        getProxy().getPluginManager().registerListener(this, new RedisBungeeListener(this));

        getProxy().registerChannel("UHCGames");
        getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));

        fetchParties();
        Set<Class<? extends Command>> commandes = new HashSet<>();
        Collections.addAll(commandes,
                CommandKick.class, CommandLobby.class, CommandReloadConf.class, CommandBStats.class,
                CommandSpychat.class, CommandSend.class, CommandBan.class, CommandUnban.class, CommandList.class,
                CommandCheck.class, CommandMute.class, CommandUnmute.class, CommandSilence.class, CommandSay.class,
                CommandMsg.class, CommandReply.class, CommandHelp.class, CommandBCast.class, CommandGtp.class,
                CommandFriendVIP.class, CommandToken.class, CommandId.class, CommandPwd.class, CommandFriend.class,
                CommandIgnore.class, CommandBPl.class, CommandBLoad.class, CommandParty.class, CommandServer.class,
                CommandFind.class, CommandStaff.class, CommandSeen.class, CommandMaintenance.class,
                CommandUser.class, CommandGroups.class, CommandGtpHere.class, CommandRegister.class, CommandPings.class);

        for (Class<? extends Command> commande : commandes) {
            try {
                Command cmd = commande.getDeclaredConstructor(getClass()).newInstance(this);
                getProxy().getPluginManager().registerCommand(this, cmd);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        plugin.getPermissionManager().loadUsers();

        final ShopTask shopTask = new ShopTask(this);
        final ReloadConfHandler reloadConfHandler = new ReloadConfHandler();
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (isMaintenance())
                    return;
                reloadConfHandler.handle(plugin);
                shopTask.run();
            }
        }, 2, 10, TimeUnit.SECONDS);

        setupStopSchedule();

        getProxy().getScheduler().schedule(this, new AnnouncementTask(), 1, 1, TimeUnit.SECONDS);
    }

    private void setupStopSchedule() {
        // Auto reboot après 6 heures, dès qu'il y a peu de joueurs
        getProxy().getScheduler().schedule(this, new Runnable() {
            final Title title = ProxyServer.getInstance().createTitle()
                    .fadeIn(5)
                    .fadeOut(5)
                    .stay(15);

            @Override
            public void run() {
                Date date = new Date();
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                calendar.setTime(date);
                if (calendar.get(Calendar.HOUR_OF_DAY) == 5) {
                    broadcast(TextComponent.fromLegacyText(ChatColor.WHITE + "[UHCGames] " + ChatColor.DARK_RED + "Redémarrage du serveur dans 5 minutes ..."));

                    getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            broadcast(TextComponent.fromLegacyText(ChatColor.WHITE + "[UHCGames] " + ChatColor.DARK_RED + "Redémarrage du serveur dans 1 minute ..."));
                        }
                    }, 4 * 60, TimeUnit.SECONDS);

                    getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            broadcast(TextComponent.fromLegacyText(ChatColor.WHITE + "[UHCGames] " + ChatColor.DARK_RED + "Redémarrage du serveur dans 10 secondes ..."));
                        }
                    }, 5 * 60 - 10, TimeUnit.SECONDS);

                    getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            getProxy().stop();
                        }
                    }, 5 * 60, TimeUnit.SECONDS);
                }
            }

            private void broadcast(BaseComponent[] message) {
                getProxy().broadcast(message);
                title.title(message);
                broadcastTitle(title);
            }
        }, 10, 10, TimeUnit.MINUTES);

    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
    }

    public boolean isSilenced(String servName) {
        return silencedServers.contains(servName);
    }

    public boolean isSpying(UUID uniqueId) {
        return spy.contains(uniqueId);
    }

    public void toggleSpy(UUID uniqueId) {
        if (spy.contains(uniqueId))
            spy.remove(uniqueId);
        else
            spy.add(uniqueId);
    }

    public void setReply(UUID receiver, UUID sender) {
        reply.put(receiver, sender);
    }

    public List<UUID> getSpies() {
        return spy;
    }

    public void silence(String serverName) {
        silencedServers.add(serverName);
    }

    public void unsilence(String serverName) {
        silencedServers.remove(serverName);
    }

    public UUID getReply(UUID uniqueId) {
        return reply.containsKey(uniqueId) ? reply.get(uniqueId) : null;
    }

    public long getUptime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    boolean isMaintenance() {
        return serverManager.isRestricted("hub");
    }

    public void broadcastTitle(Title title) {
        for (ProxiedPlayer p : getProxy().getPlayers()) {
            title.send(p);
        }
    }
}
