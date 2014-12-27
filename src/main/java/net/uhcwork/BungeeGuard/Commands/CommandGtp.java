package net.uhcwork.BungeeGuard.Commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.BungeeGuardUtils;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.ServerManager;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

public class CommandGtp extends Command {
    private final ServerManager SM;

    public CommandGtp(Main plugin) {
        super("gtp", "bungee.gtp");
        SM = Main.getServerManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (args.length != 1) {
            BungeeGuardUtils.msgPluginCommand(sender);
            return;
        }
        String playerName = args[0];
        MultiBungee MB = Main.getMB();
        if (MB.isPlayerOnline(playerName)) {
            ServerInfo server = MB.getServerFor(playerName);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Téléportation vers " + ChatColor.BLUE + playerName + ChatColor.GREEN + " dans le monde " + ChatColor.GOLD + SM.getPrettyName(server.getName()) + ChatColor.RESET + ChatColor.GOLD + "(" + server.getName() + ")" + ChatColor.GREEN + "..."));
            if (server.getName().equalsIgnoreCase(p.getServer().getInfo().getName())) {
                p.chat("/tp " + playerName);
            } else {
                MB.gtp(p.getName(), playerName);
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Erreur: Ce joueur n'est pas en ligne"));
        }
    }
}