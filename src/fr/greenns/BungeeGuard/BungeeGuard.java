package fr.greenns.BungeeGuard;

import fr.greenns.BungeeGuard.Ban.Ban;
import fr.greenns.BungeeGuard.Ban.CommandBan;
import fr.greenns.BungeeGuard.Ban.CommandUnban;
import fr.greenns.BungeeGuard.Config.MysqlConfigAdapter;
import fr.greenns.BungeeGuard.Kick.CommandKick;
import fr.greenns.BungeeGuard.Lobbies.Lobby;
import fr.greenns.BungeeGuard.Lobbies.LobbyUtils;
import fr.greenns.BungeeGuard.Mute.CommandMute;
import fr.greenns.BungeeGuard.Mute.CommandUnmute;
import fr.greenns.BungeeGuard.Mute.Mute;
import fr.greenns.BungeeGuard.PubSub.ReloadConfHandler;
import fr.greenns.BungeeGuard.SQL.MySQL;
import fr.greenns.BungeeGuard.commands.*;
import fr.greenns.BungeeGuard.utils.AuthPlayer;
import fr.greenns.BungeeGuard.utils.MultiBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BungeeGuard extends Plugin {

    public static BungeeGuard plugin;
    public static List<AuthPlayer> authplayers = new ArrayList<>();
    public static List<Ban> bans = new ArrayList<>();
    public static List<Mute> mutes = new ArrayList<>();
    public static List<Lobby> lobbys = new ArrayList<>();
    public MySQL sql;
    public Configuration config;
    public BungeeGuardUtils utils;
    public LobbyUtils lobbyUtils;
    public Map<UUID, String> reply = new HashMap<>();
    public List<UUID> spy = new ArrayList<>();
    public List<String> serv = new ArrayList<>();
    public BungeeGuardListener BGListener;
    public String motd;
    public Long time;
    public HashMap<UUID, List<UUID>> ignore = new HashMap<>();
    public HashMap<UUID, String> gtp = new HashMap<>();
    MultiBungee MB;

    public MultiBungee getMB() {
        return MB;
    }

    @Override
    public void onLoad() {
        utils = new BungeeGuardUtils(this);
        System.out.println("OnLoad, I am caulled.");
        sql = new MySQL(getLogger(), "", "vm-db-01.uhcwork.net", "3306", "plugin", "minecraft", "tn8E6VhU9P3m");

        sql.open();

        plugin = this;

        if (!sql.checkTable("BungeeGuard_Config")) {
            System.out.println("BungeeGuard - Table BungeeGuard_Config inexistante, creation en cours ...");

            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Config` (" +
                    " `bungeeConf` TEXT NOT NULL, " +
                    " `server_id` VARCHAR(60) DEFAULT NULL," +
                    " PRIMARY KEY(`server_id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

            System.out.println("BungeeGuard - Table BungeeGuard_Config créée !");
        }

        if (this.sql.checkConnection()) {
            System.out.println("BungeeGuard - Connexion BDD réussie !");
        } else {
            System.out.println("BungeeGuard - Connexion BDD §cIMPOSSIBLE  §r!!!!!");
        }
        BungeeCord.getInstance().setConfigurationAdapter(new MysqlConfigAdapter(this));
    }

    @Override
    public void onEnable() {
        MB = new MultiBungee();
        System.out.println("Bonjour, je suis MultiBungee. A votre service!");
        MB.registerPubSubChannels("summon", "reloadConf", "staffChat", "notifyStaff", "ban", "kick", "mute", "message", "broadcast", "refreshMOTD", "privateMessage", "unmute", "unban");

        if (sql.checkTable("BungeeGuard_Ban")) {
            System.out.println("BungeeGuard - Table BungeeGuard_Ban trouvée !");

            System.out.println("BungeeGuard - Chargement des bannis ...");
            try {
                if (BungeeGuard.plugin.sql.getConnection().isClosed()) {
                    BungeeGuard.plugin.sql.open();
                }

                if (BungeeGuard.plugin.sql.getConnection() == null) {
                    System.out.println("[MYSQL] Connection error ...");
                }
                ResultSet res = sql.query("SELECT * FROM BungeeGuard_Ban WHERE status = 1");
                UUID u;
                while (res.next()) {
                    u = UUID.fromString(res.getString("uuidBanned"));
                    if (u == null)
                        continue;
                    new Ban(u, res.getString("nameBanned"), res.getLong("unban"), res.getString("reason"), res.getString("nameAdmin"), res.getString("uuidAdmin"));
                }
            } catch (final SQLException ex) {
                System.out.println("SQL problem (exception) when gettings banned players from BDD : " + ex);
            }
        } else {
            System.out.println("BungeeGuard - Table BungeeGuard_Ban inÃ©xistante, creation en cours ...");

            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Ban` (" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                    "  `nameBanned` varchar(255) NOT NULL," +
                    "  `nameAdmin` varchar(255) NOT NULL," +
                    "  `uuidBanned` varchar(255) NOT NULL," +
                    "  `uuidAdmin` varchar(255) NOT NULL," +
                    "  `ban` bigint(20) NOT NULL," +
                    "  `unban` bigint(20) NOT NULL," +
                    "  `reason` text NOT NULL," +
                    "  `unbanReason` text," +
                    "  `unbanName` varchar(255) NOT NULL," +
                    "  `status` int(11) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

            System.out.println("BungeeGuard - Table BungeeGuard_Ban créé !");
        }

        if (sql.checkTable("BungeeGuard_Mute")) {
            System.out.println("BungeeGuard - Table BungeeGuard_Mute trouvée !");

            System.out.println("BungeeGuard - Chargement des mutes ...");
            try {
                if (BungeeGuard.plugin.sql.getConnection().isClosed()) {
                    BungeeGuard.plugin.sql.open();
                }

                if (BungeeGuard.plugin.sql.getConnection() == null) {
                    System.out.println("[MYSQL] Connection error ...");
                }
                ResultSet res = sql.query("SELECT * FROM BungeeGuard_Mute WHERE status = 1");

                while (res.next()) {
                    new Mute(UUID.fromString(res.getString("uuidMute")), res.getString("nameMute"), res.getLong("unmute"), res.getString("reason"), res.getString("nameAdmin"), res.getString("uuidAdmin"));
                }
            } catch (final SQLException ex) {
                System.out.println("SQL problem (exception) when getting mute players from BDD : " + ex);
            }
        } else {
            System.out.println("BungeeGuard - Table BungeeGuard_Mute inéxistante, création en cours ...");

            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Mute` (" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                    "  `nameMute` varchar(255) NOT NULL," +
                    "  `nameAdmin` varchar(255) NOT NULL," +
                    "  `uuidMute` varchar(255) NOT NULL," +
                    "  `uuidAdmin` varchar(255) NOT NULL," +
                    "  `mute` bigint(20) NOT NULL," +
                    "  `unmute` bigint(20) NOT NULL," +
                    "  `reason` text NOT NULL," +
                    "  `unmuteReason` text," +
                    "  `unmuteName` varchar(255) NOT NULL," +
                    "  `status` int(11) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");

            System.out.println("BungeeGuard - Table BungeeGuard_Mute créé !");
        }
        if (sql.checkTable("BungeeGuard_AdminAuth")) {
            System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth trouvée !");

            System.out.println("BungeeGuard - Chargement des adminAuth ...");
            try {
                if (BungeeGuard.plugin.sql.getConnection().isClosed()) {
                    BungeeGuard.plugin.sql.open();
                }

                if (BungeeGuard.plugin.sql.getConnection() == null) {
                    System.out.println("[MYSQL] Connection error ...");
                }
                ResultSet res = sql.query("SELECT * FROM BungeeGuard_AdminAuth");

                while (res.next()) {
                    new AuthPlayer(UUID.fromString(res.getString("UUID")), res.getString("AuthKey"));
                }
            } catch (final SQLException ex) {
                System.out.println("SQL problem (exception) when getting adminAuth players from BDD : " + ex);
            }
        } else {
            System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth inéxistante, création en cours ...");

            sql.createTable("CREATE TABLE `BungeeGuard_AdminAuth` (" +
                    "`UUID` varchar(255) NOT NULL," +
                    "`Pseudo` varchar(45) NOT NULL," +
                    "`AuthKey` varchar(255) NOT NULL," +
                    "PRIMARY KEY (`UUID`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");

            System.out.println("BungeeGuard - Table BungeeGuard_AdminAuth créé !");
        }
        if (sql.checkTable("BungeeGuard_Motd")) {
            System.out.println("BungeeGuard - Table BungeeGuard_Motd trouvÃ©e !");
        } else {
            System.out.println("BungeeGuard - Table BungeeGuard_Motd inÃ©xistante, creation en cours ...");
            sql.createTable("CREATE TABLE IF NOT EXISTS `BungeeGuard_Motd` (" +
                    "  `id` int(11) NOT NULL," +
                    "  `motd` varchar(255) NOT NULL," +
                    "  PRIMARY KEY (`id`))");
            System.out.println("BungeeGuard - Table BungeeGuard_Motd créé !");
        }


        BGListener = new BungeeGuardListener(this);
        lobbyUtils = new LobbyUtils(this);
        sql.close();

        ProxyServer.getInstance().getPluginManager().registerListener(this, BGListener);

        Set<Class<? extends Command>> commandes = new HashSet<>();
        commandes.add(CommandKick.class);
        commandes.add(CommandLobby.class);
        commandes.add(CommandReloadConf.class);
        commandes.add(CommandSpychat.class);
        commandes.add(CommandSend.class);
        commandes.add(CommandBan.class);
        commandes.add(CommandUnban.class);
        commandes.add(CommandList.class);
        commandes.add(CommandCheck.class);
        commandes.add(CommandMute.class);
        commandes.add(CommandUnmute.class);
        commandes.add(CommandSilence.class);
        commandes.add(CommandMotd.class);
        commandes.add(CommandSay.class);
        commandes.add(CommandMsg.class);
        commandes.add(CommandReply.class);
        commandes.add(CommandHelp.class);
        commandes.add(CommandBCast.class);
        commandes.add(CommandGtp.class);
        commandes.add(CommandIgnore.class);

        for (Class<? extends Command> commande : commandes) {
            try {
                Command cmd = commande.getDeclaredConstructor(getClass()).newInstance(this);
                ProxyServer.getInstance().getPluginManager().registerCommand(this, cmd);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }


        utils.refreshMotd();

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                updateLobbysStatus();
            }
        }, 1, 3, TimeUnit.SECONDS);

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                new ReloadConfHandler(plugin).handle();
            }
        }, 0, 5 * 60, TimeUnit.SECONDS);
    }

    public void updateLobbysStatus() {
        final List<Lobby> new_lobbys = new ArrayList<>();

        for (final ServerInfo serverInfo : BungeeCord.getInstance().getServers().values()) {
            if (serverInfo.getName().contains("lobby")) {
                serverInfo.ping(new Callback<ServerPing>() {
                    @Override
                    public void done(ServerPing result, Throwable error) {
                        int players = serverInfo.getPlayers().size();
                        double tps = 0;
                        try {
                            if (error == null) tps = Double.parseDouble(result.getDescription());
                        } catch (NumberFormatException e) {
                            tps = 20;
                        }
                        Lobby Lobby = new Lobby(serverInfo.getName(), players, tps, (error == null));
                        new_lobbys.add(Lobby);
                    }
                });
            }
        }

        lobbys = new_lobbys;
    }

    @Override
    public void onDisable() {
        sql.close();
        BungeeCord.getInstance().getScheduler().cancel(this);
    }
}
