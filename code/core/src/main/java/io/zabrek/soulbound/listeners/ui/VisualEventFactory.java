package io.zabrek.soulbound.listeners.ui;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.bukkit.event.PlayerLevelChangeEvent;
import io.zabrek.soulbound.api.bukkit.event.PlayerVisualEffectEvent;
import io.zabrek.soulbound.api.listeners.Listener;
import io.zabrek.soulbound.api.listeners.ListenerFactory;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import org.bukkit.plugin.Plugin;

/**
 * The default implementation for {@link ListenerFactory}.
 */
public class VisualEventFactory implements ListenerFactory {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new VisualEventFactory.
     *
     * @param plugin the plugin instance
     */
    public VisualEventFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Listener create(final ListenerService service) throws SoulBoundException {
        final VisualEvent event = new VisualEvent(service, plugin);
        service.request(PlayerLevelChangeEvent.class)
                .handler(event::onLevelChange)
                .profile(PlayerLevelChangeEvent::getProfile)
                .subscribe(true);
        service.request(PlayerVisualEffectEvent.class)
                .handler(event::applyParticleEffect)
                .profile(PlayerVisualEffectEvent::getProfile)
                .subscribe(true);
        return event;
    }
}
