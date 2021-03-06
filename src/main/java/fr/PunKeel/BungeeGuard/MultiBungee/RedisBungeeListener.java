package fr.PunKeel.BungeeGuard.MultiBungee;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fr.PunKeel.BungeeGuard.Main;
import fr.PunKeel.BungeeGuard.MultiBungee.PubSub.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Method;

public class RedisBungeeListener implements Listener {
    private final Main plugin;
    private final Multimap<String, Method> handlers = HashMultimap.create();

    public RedisBungeeListener(Main plugin) {
        this.plugin = plugin;
        addHandler(BroadcastHandler.class);
        addHandler(FriendHandler.class);
        addHandler(IgnoreHandler.class);
        addHandler(KickHandler.class);
        addHandler(MessageHandler.class);
        addHandler(PartyHandler.class);
        addHandler(PermissionHandler.class);
        addHandler(ReloadConfHandler.class);
        addHandler(SanctionHandler.class);
        addHandler(ServerSilenceHandler.class);
        addHandler(StaffChatHandler.class);
        addHandler(SummonHandler.class);
        Main.getMB().registerPubSubChannels(handlers.keySet());

    }

    @EventHandler
    public void onPubSubmessageEvent(com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent e) {
        String channel = e.getChannel();
        dispatchEvent(new PubSubMessageEvent(channel, e.getMessage()));
    }

    void addHandler(Class<?> handler) {
        this.handlers.putAll(findMatchingEventHandlerMethods(handler));
    }

    void dispatchEvent(PubSubMessageEvent event) {
        for (Method method : handlers.get(event.getChannel())) {
            try {
                if (method.getParameterTypes().length == 0)
                    method.invoke(null);
                if (method.getParameterTypes().length == 1)
                    method.invoke(null, event);
                if (method.getParameterTypes().length == 2)
                    method.invoke(null, plugin, event);
            } catch (Exception e) {
                System.err.println("Could not invoke event handler for " + event);
                e.printStackTrace(System.err);
            }
        }
    }

    Multimap<String, Method> findMatchingEventHandlerMethods(Class<?> handler) {
        Multimap<String, Method> _handlers = HashMultimap.create();
        Method[] methods = handler.getDeclaredMethods();
        String eventName;
        for (Method method : methods) {
            eventName = getEventName(method);
            if (!eventName.isEmpty()) {
                method.setAccessible(true);
                if (eventName.startsWith("@")) {
                    eventName = "@" + Main.getMB().getServerId() + "/" + eventName.substring(1);
                }
                _handlers.put(eventName, method);
            }
        }
        return _handlers;
    }

    String getEventName(Method method) {
        PubSubHandler handleEventAnnotation = method.getAnnotation(PubSubHandler.class);
        if (handleEventAnnotation != null) {
            return handleEventAnnotation.value();
        }
        return "";
    }

    @EventHandler
    public void onNetworkPlayerServerChangeEvent(com.imaginarycode.minecraft.redisbungee.events.PlayerChangedServerNetworkEvent e) {
        String previousServer = e.getPreviousServer();
        String targetServer = e.getServer();
        Main.getServerManager().addPlayer(previousServer, -1);
        Main.getServerManager().addPlayer(targetServer, 1);
    }
}
