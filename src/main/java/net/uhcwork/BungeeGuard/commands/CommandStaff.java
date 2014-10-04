package net.uhcwork.BungeeGuard.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.plugin.Command;
import net.uhcwork.BungeeGuard.Main;
import net.uhcwork.BungeeGuard.MultiBungee.MultiBungee;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandStaff extends Command {
    public Main plugin;
    Map<String, ChatColor> groupes = new LinkedHashMap<>();
    // LinkedHashMap: garde l'ordre d'insertion, ce qui fait que les groupes sont affichés dans le bon ordre
    MultiBungee MB;

    public CommandStaff(Main plugin) {
        super("staff");
        this.plugin = plugin;
        MB = plugin.getMB();
        groupes.put("admin", ChatColor.RED);
        groupes.put("modo", ChatColor.BLUE);
        groupes.put("yt", ChatColor.GOLD);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BaseComponent[] message = new ComponentBuilder("Staff")
                .color(ChatColor.RED)
                .append(": ")
                .create();
        boolean isStaffOnline = false;
        ConfigurationAdapter config = ProxyServer.getInstance().getConfigurationAdapter();

        Multimap<String, String> staff = ArrayListMultimap.create();
        // Permet d'avoir les joueurs par groupe, au lieu de les avoir dans un ordre random


        playersBoucle:
        for (String playerName : MB.getHumanPlayersOnline()) {
            for (String groupeName : groupes.keySet()) {
                if (config.getGroups(playerName).contains(groupeName)) {
                    isStaffOnline = true;
                    staff.put(groupeName, playerName);
                    continue playersBoucle;
                }
            }
        }

        if (isStaffOnline) {
            for (String groupeName : groupes.keySet()) {
                for (String playerName : staff.get(groupeName)) {
                    ComponentBuilder TC = new ComponentBuilder(playerName)
                            .color(groupes.get(groupeName));
                    if (sender.hasPermission("bungeeguard.admin")) {
                        TC.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("Serveur: ")
                                        .append(MB.getServerFor(playerName).getName())
                                        .color(ChatColor.BLUE)
                                        .append("\nProxy: ")
                                        .append(MB.getProxy(playerName))
                                        .color(ChatColor.DARK_AQUA)
                                        .append("\nGroupes: ")
                                        .append(Joiner.on(", ").join(config.getGroups(playerName)))
                                        .color(ChatColor.RED)
                                        .create()));
                    }
                    TC.append(" ").underlined(false).color(null).bold(false)
                            .italic(false).strikethrough(false).obfuscated(false);
                    message = ObjectArrays.concat(message, TC.create(), BaseComponent.class);
                }
            }
            sender.sendMessage(message);
        } else
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Aucun membre du staff n'est en ligne pour le moment."));
    }
}