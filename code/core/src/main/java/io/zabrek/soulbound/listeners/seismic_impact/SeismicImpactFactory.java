package io.zabrek.soulbound.listeners.seismic_impact;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.listeners.Listener;
import io.zabrek.soulbound.api.listeners.ListenerFactory;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Factory for creating {@link SeismicImpact} instances.
 */
public class SeismicImpactFactory implements ListenerFactory {

    /**
     * Creates a new SeismicImpactFactory.
     */
    public SeismicImpactFactory() {
    }

    @Override
    public Listener create(final ListenerService service) throws SoulBoundException {
        final SeismicImpact listener = new SeismicImpact(service);
        service.request(EntityDamageEvent.class)
                .player(event -> event.getDamageSource() instanceof final Player player ? player : null)
                .priority(EventPriority.HIGHEST)
                .onlineHandler(listener::onDamage)
                .subscribe(false);
        return listener;
    }
}
