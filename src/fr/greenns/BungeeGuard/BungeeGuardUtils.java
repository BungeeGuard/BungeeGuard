package fr.greenns.BungeeGuard;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BungeeGuardUtils {

    public BungeeGuard plugin;
    public String staffBroadcast = "§7§l[§c§LSTAFF§7§l]§r ";
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

    public BungeeGuardUtils(BungeeGuard plugin)
    {
        this.plugin = plugin;
    }

    public boolean isParsableToInt(String i) {
        try {
            Integer.parseInt(i);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String getDateFormat(long time)
    {
        Date date = new Date(time);
        String dateString = new SimpleDateFormat("HH:mm:ss").format(date);

        return dateString;
    }


    public void refreshMotd()
    {
        ResultSet res;
        plugin.sql.open();

        if(plugin.sql.getConnection() == null)
        {
            System.out.println(ChatColor.RED + "[MYSQL] Connection error ... when claim a ticket !");
            return;
        }

        try
        {
            res = plugin.sql.query("SELECT `motd` FROM `BungeeGuard_Motd` WHERE id=1");

            if(res.next())
            {
                String te = res.getString("motd");
                plugin.motd = translateCodes(te);
            }

            if (!plugin.sql.getConnection().isClosed())
            {
                plugin.sql.close();
            }
        }
        catch (SQLException ex)
        {
            System.out.println(ex);
        }
    }

    public String translateCodes(String message)
    {
        message = message.replaceAll("&([0-9a-fk-or])", "§$1");
        //message = message.replaceAll("%timer", getDateFormat(plugin.time));
        return message = message.replaceAll("%n", "\n");
    }

    public void msgPluginCommand(CommandSender sender)
    {
        sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "                            "+ChatColor.RESET+"§6.: BungeeManager :."+ ChatColor.RED + "" + ChatColor.UNDERLINE +"                           ");
        sender.sendMessage(ChatColor.RED + " ");
        sender.sendMessage(ChatColor.RED + " /ban <Player> [temps en minutes/0 pour définitif] [raison ...]");
        sender.sendMessage(ChatColor.RED + " /kick <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /unban <Player> [raison ...]");
        sender.sendMessage(ChatColor.RED + " /check <Player>");
        sender.sendMessage(ChatColor.RED + " /mute <Player>");
        sender.sendMessage(ChatColor.RED + " /unmute <Player>");
        sender.sendMessage(ChatColor.RED + " /silence (on/off)");
        sender.sendMessage(ChatColor.RED + " /say [message]");
        sender.sendMessage(ChatColor.RED + " /msg <Player> [message]");
        sender.sendMessage(ChatColor.RED + " /spychat (toggle)");
        sender.sendMessage(ChatColor.RED + " /motd (refresh)");
        sender.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "                                                                               ");
    }

    public static long parseDuration(final String durationStr) throws IllegalArgumentException {
        final Matcher m = timePattern.matcher(durationStr);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(ChatColor.RED + "Invalid duration !");
        }
        final Calendar c = new GregorianCalendar();
        if (years > 0) {
            c.add(Calendar.YEAR, years);
        }
        if (months > 0) {
            c.add(Calendar.MONTH, months);
        }
        if (weeks > 0) {
            c.add(Calendar.WEEK_OF_YEAR, weeks);
        }
        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours);
        }
        if (minutes > 0) {
            c.add(Calendar.MINUTE, minutes);
        }
        if (seconds > 0) {
            c.add(Calendar.SECOND, seconds);
        }
        return c.getTimeInMillis();
    }


    public static String getDuration(final long futureTimestamp) {
        int seconds = (int) ((futureTimestamp - System.currentTimeMillis()) / 1000);
        Preconditions.checkArgument(seconds > 0,"Le timestamp doit etre supérieur au current timestamp !");

        final List<String> item = new ArrayList<String>();

        int months = 0;
        while (seconds >= 2678400) {
            months++;
            seconds -= 2678400;
        }
        if (months > 0) {
            item.add(months + " mois");
        }

        int days = 0;
        while (seconds >= 86400) {
            days++;
            seconds -= 86400;
        }
        if (days > 0) {
            item.add(days + " jours");
        }

        int hours = 0;
        while (seconds >= 3600) {
            hours++;
            seconds -= 3600;
        }
        if (hours > 0) {
            item.add(hours + " heures");
        }

        int mins = 0;
        while (seconds >= 60) {
            mins++;
            seconds -= 60;
        }
        if (mins > 0) {
            item.add(mins + " minutes");
        }

        if (seconds > 0) {
            item.add(seconds + " secondes");
        }

        return Joiner.on(", ").join(item);
    }

    public static String getFinalArg(String[] args, int start)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++)
        {
            if (i != start) {
                sb.append(" ");
            }
            sb.append(args[i]);
        }
        String msg = sb.toString();
        sb.setLength(0);
        return msg;
    }


}