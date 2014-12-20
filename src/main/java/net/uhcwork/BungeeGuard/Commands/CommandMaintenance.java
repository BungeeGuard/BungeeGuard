package net.uhcwork.BungeeGuard.Commands;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.Managers.ServerManager;
import net.uhcwork.BungeeGuard.Models.BungeeServer;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;
import net.uhcwork.BungeeGuard.Persistence.SaveRunner;

import java.util.Collection;

public class CommandMaintenance extends Command {
    private final Main plugin;
    Joiner joiner = Joiner.on(", ").skipNulls();
    ServerManager SM;

    MultiBungee MB;

    public CommandMaintenance(Main plugin) {
        super("maintenance", "bungee.maintenance");
        this.plugin = plugin;
        SM = Main.getServerManager();
        MB = Main.getMB();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /maintenance <serveur_name>"));
            sender.sendMessage(TextComponent.fromLegacyText("Liste des serveurs en maintenance: " + joiner.join(SM.getServersInMaintenance())));
            return;
        }
        String pattern = args[0];
        Collection<String> serverNames = SM.matchServer(pattern);

        if (serverNames.isEmpty()) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serveur ... inconnu u.u"));
            return;
        }

        for (String name : serverNames) {
            boolean isRestricted = !SM.isRestricted(name);
            MB.setMaintenance(name, isRestricted);
            if (isRestricted) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Maintenance activée pour " + SM.getPrettyName(name)));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Maintenance désactivée pour " + SM.getPrettyName(name)));
            }
            BungeeServer server = SM.getServerModel(name);
            server.setRestricted(isRestricted);
            plugin.executePersistenceRunnable(new SaveRunner(server));
        }
    }
}