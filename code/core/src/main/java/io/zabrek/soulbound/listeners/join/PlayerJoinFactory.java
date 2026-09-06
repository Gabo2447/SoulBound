package io.zabrek.soulbound.listeners.join;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.listeners.Listener;
import io.zabrek.soulbound.api.listeners.ListenerFactory;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * The default implementation of {@link ListenerFactory} for PlayerJoinFactory.
 */
public class PlayerJoinFactory implements ListenerFactory {

    /**
     * Creates a new PlayerJonFactory.
     */
    public PlayerJoinFactory() {
    }

    @Override
    public Listener create(final ListenerService service) throws SoulBoundException {
        final PlayerJoin listener = new PlayerJoin(service);
        service.request(PlayerJoinEvent.class)
                .handler(listener::onJoin)
                .player(PlayerEvent::getPlayer)
                .subscribe(true);
        return listener;
    }
}
