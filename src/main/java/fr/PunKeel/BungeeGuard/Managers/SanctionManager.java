package fr.PunKeel.BungeeGuard.Managers;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.Models.BungeeBan;
import fr.PunKeel.BungeeGuard.Models.BungeeMute;
import fr.PunKeel.BungeeGuard.Models.BungeePremadeMessage;
import fr.PunKeel.BungeeGuard.Persistence.SaveRunner;
import fr.PunKeel.BungeeGuard.Persistence.VoidRunner;
import org.javalite.activejdbc.LazyList;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SanctionManager {
    private static final String REDUCTION_PEINE = " &r(&eRéduction de peine&r)";
    private static final Map<String, String> premadeMessages = new HashMap<>();
    private final List<BungeeBan> banList = new ArrayList<>();
    private final List<BungeeMute> muteList = new ArrayList<>();
    private final Main plugin;

    public SanctionManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadBans() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            @Override
            protected void run() {
                LazyList<BungeeBan> bans = BungeeBan.findAll();
                banList.clear();
                banList.addAll(Collections2.filter(bans, new Predicate<BungeeBan>() {
                    @Override
                    public boolean apply(BungeeBan bungeeBan) {
                        return bungeeBan.isBanned();
                    }
                }));
            }
        });
    }

    public BungeeBan findBan(UUID uuid) {
        if (uuid == null)
            return null;
        for (BungeeBan ban : new CopyOnWriteArrayList<>(banList)) {
            if (ban.getBannedUUID().equals(uuid)) {
                if (ban.isBanned())
                    return ban;
                else {
                    removeBan(ban);
                }
            }
        }
        return null;
    }

    public void removeBan(BungeeBan ban) {
        if (ban == null)
            return;
        if (banList.contains(ban))
            banList.remove(ban);
    }

    public BungeeBan ban(UUID bannedUUID, String bannedName, long bannedUntilTime, String reason, String adminName, UUID adminUUID, boolean saveToBdd) {
        BungeeBan ban;
        ban = findBan(bannedUUID);
        unban(ban, adminName, "ReBan", saveToBdd);
        ban = new BungeeBan();
        ban.setBannedUUID(bannedUUID.toString());
        ban.setBannedName(bannedName);
        ban.setAdminName(adminName);
        ban.setAdminUUID(adminUUID.toString());
        ban.setReason(reason);
        ban.setBannedUntil(bannedUntilTime);
        ban.setStatus(1);
        banList.add(ban);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(ban));
        return ban;
    }

    public void unban(BungeeBan ban, String adminName, String reason, boolean saveToBdd) {
        if (ban == null)
            return;
        ban.setUnbanReason(reason);
        ban.setUnbanAdminName(adminName);
        ban.setUnbanTime(System.currentTimeMillis());
        ban.setStatus(0);
        removeBan(ban);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(ban));
    }

    public void loadMutes() {
        plugin.executePersistenceRunnable(new VoidRunner() {
            protected void run() {
                LazyList<BungeeMute> mutes = BungeeMute.where("status = 1");
                muteList.clear();
                muteList.addAll(Collections2.filter(mutes, new Predicate<BungeeMute>() {
                    @Override
                    public boolean apply(BungeeMute bungeeMute) {
                        return bungeeMute.isMute();
                    }
                }));
            }
        });
    }

    public BungeeMute findMute(UUID uuid) {
        if (uuid == null)
            return null;
        for (BungeeMute mute : new CopyOnWriteArrayList<>(muteList)) {
            if (mute.getMutedUUID().equals(uuid)) {
                if (mute.isMute())
                    return mute;
                else {
                    unmute(mute, "Automatique", "Fin du mute", true);
                }
            }
        }
        return null;
    }

    public void removeMute(BungeeMute mute) {
        if (mute == null)
            return;
        if (muteList.contains(mute))
            muteList.remove(mute);
    }

    public BungeeMute mute(UUID mutedUUID, String mutedName, long mutedUntilTime, String reason, String adminName, UUID adminUUID, boolean saveToBdd) {
        BungeeMute mute;
        mute = findMute(mutedUUID);
        unmute(mute, adminName, "ReMute", saveToBdd);
        removeMute(mute);

        mute = new BungeeMute();
        mute.setMutedUUID(mutedUUID.toString());
        mute.setMutedName(mutedName);
        mute.setAdminName(adminName);
        mute.setAdminUUID(adminUUID.toString());
        mute.setReason(reason);
        mute.setMutedUntil(mutedUntilTime);
        mute.setStatus(1);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(mute));
        muteList.add(mute);
        return mute;
    }

    public void unmute(final BungeeMute mute, String adminName, String reason, boolean saveToBdd) {
        if (mute == null)
            return;
        mute.setUnmuteAdminName(adminName);
        mute.setUnmuteReason(reason);
        mute.setUnmuteTime(System.currentTimeMillis());
        mute.setStatus(0);
        removeMute(mute);
        if (saveToBdd)
            plugin.executePersistenceRunnable(new SaveRunner(mute));
    }

    public boolean isPremadeMessage(String slug) {
        String lowerSlug = slug.toLowerCase();
        if (lowerSlug.startsWith("r:"))
            lowerSlug = lowerSlug.substring(2);
        return premadeMessages.containsKey(lowerSlug);
    }

    public String getPremadeMessage(String slug) {
        String lowerSlug = slug.toLowerCase();
        boolean reduction = lowerSlug.startsWith("r:");
        if (reduction)
            lowerSlug = lowerSlug.substring(2);

        return premadeMessages.get(lowerSlug) + (reduction ? REDUCTION_PEINE : "");
    }

    public void setPremadeMessages(List<BungeePremadeMessage> all) {
        premadeMessages.clear();
        for (BungeePremadeMessage message : all) {
            premadeMessages.put(message.getSlug().toLowerCase(), message.getText());
        }
    }
}
