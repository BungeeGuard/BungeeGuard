package fr.PunKeel.BungeeGuard.Commands;

import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Managers.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public class CommandFind extends Command {
    private final ServerManager SM;

    public CommandFind(Main plugin) {
        super("find", "bungeecord.command.find");
        SM = Main.getServerManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please follow this command by a user name"));
            return;
        }
        ServerInfo SI = Main.getMB().getServerFor(args[0]);
        String proxy = Main.getMB().getProxy(args[0]);
        if (SI == null) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That user is not online"));
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "[" + ChatColor.AQUA + proxy + ChatColor.GRAY + "] " + ChatColor.GREEN + args[0] + ChatColor.GRAY + " est sur " + SM.getPrettyName(SI.getName()) + ChatColor.RESET + ChatColor.GRAY + " (" + ChatColor.YELLOW + SI.getName() + ChatColor.RESET + ChatColor.GRAY + ")"));
    }
}