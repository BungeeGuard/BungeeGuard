package fr.greenns.BungeeGuard.Ban;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;

public enum BanType {
    PERMANENT(ChatColor.RED + "Vous avez été banni définitivement.", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " définitivement."),
    NON_PERMANENT(ChatColor.RED + "Vous avez été banni pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + ".", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + "."),
    PERMANENT_W_REASON(ChatColor.RED + "Vous avez été banni définitivement pour:" + '\n' + ChatColor.AQUA + "+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " définitivement pour:" + ChatColor.AQUA + "+reasonStr" + ChatColor.RED + "."),
    NON_PERMANENT_W_REASON(ChatColor.RED + "Vous avez été banni pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + " pour: " + '\n' + "+reasonStr", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a banni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + "pendant " + ChatColor.AQUA + "+timeStr" + ChatColor.RED + " pour: " + ChatColor.AQUA + "+reasonStr"),
    UNBAN("", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a débanni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + "."),
    UNBAN_W_REASON("", ChatColor.AQUA + "+adminName" + ChatColor.RED + " a débanni " + ChatColor.GREEN + "+bannedName" + ChatColor.RED + " avec la raison:" + ChatColor.AQUA + "+reasonStr" + ChatColor.RED + ".");

    String kickFormat;
    String adminFormat;
    String adminPrefix = ChatColor.RED + "[BungeeGuard] ";

    private BanType(String kickFormat, String adminFormat) {
        this.kickFormat = kickFormat;
        this.adminFormat = adminFormat;
    }

    public String kickFormat(String timeStr, String reasonStr) {
        String message = kickFormat;
        if (message.contains("+timeStr")) {
            timeStr = Matcher.quoteReplacement(timeStr);
            message = message.replaceAll("\\+timeStr", timeStr);
        }
        if (message.contains("+reasonStr")) {
            reasonStr = Matcher.quoteReplacement(reasonStr);
            message = message.replaceAll("\\+reasonStr", reasonStr);
        }
        return message;
    }

    public String adminFormat(String timeStr, String reasonStr, String adminName, String bannedName) {
        String message = adminFormat;
        if (message.contains("+timeStr")) {
            timeStr = Matcher.quoteReplacement(timeStr);
            message = message.replaceAll("\\+timeStr", timeStr);
        }
        if (message.contains("+reasonStr")) {
            reasonStr = Matcher.quoteReplacement(reasonStr);
            message = message.replaceAll("\\+reasonStr", reasonStr);
        }
        if (message.contains("+adminName")) {
            adminName = Matcher.quoteReplacement(adminName);
            message = message.replaceAll("\\+adminName", adminName);
        }
        if (message.contains("+bannedName")) {
            bannedName = Matcher.quoteReplacement(bannedName);
            message = message.replaceAll("\\+bannedName", bannedName);
        }
        return adminPrefix + message;
    }
}