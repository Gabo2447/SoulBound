package io.zabrek.soulbound.listeners;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.identifier.ListenerIdentifier;
import io.zabrek.soulbound.api.listeners.ListenerFactory;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.listeners.service.ListenerServiceProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.id.listener.ListenerIdentifierFactory;
import io.zabrek.soulbound.listeners.death.EntityDeathFactory;
import io.zabrek.soulbound.listeners.join.PlayerJoinFactory;
import io.zabrek.soulbound.listeners.ui.VisualEventFactory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * This class register the listeners to Bukkit.
 */
public final class ListenerTypeRegistry {

    private ListenerTypeRegistry() {
    }

    /**
     * Loads the listeners.
     *
     * @param serviceProvider           the provider for listener services
     * @param listenerIdentifierFactory the factory for parsing listener identifiers
     * @param loggerFactory             the factory used to create loggers
     */
    public static void load(final ListenerServiceProvider serviceProvider, final ListenerIdentifierFactory listenerIdentifierFactory,
                            final SoulBoundLoggerFactory loggerFactory, final Plugin plugin) {
        final SoulBoundLogger log = loggerFactory.create(ListenerTypeRegistry.class);

        final Map<String, ListenerFactory> factories = new HashMap<>();
        factories.put("join", new PlayerJoinFactory());
        factories.put("death", new EntityDeathFactory());
        factories.put("ui", new VisualEventFactory(plugin));

        try {
            log.info("Loading %d listener components...".formatted(factories.size()));
            loadFactory(listenerIdentifierFactory, serviceProvider, factories);
        } catch (final SoulBoundException e) {
            log.error("Failed to load listeners... Error %s".formatted(e.getMessage()), e);
        }
    }

    private static void loadFactory(final ListenerIdentifierFactory identifierFactory, final ListenerServiceProvider serviceProvider,
                                    final Map<String, ListenerFactory> factories) throws SoulBoundException {
        for (final Map.Entry<String, ListenerFactory> entry : factories.entrySet()) {
            final String name = entry.getKey();
            final ListenerIdentifier identifier = identifierFactory.parseIdentifier("soulbound>%s".formatted(name));
            final ListenerFactory listenerFactory = entry.getValue();

            try {
                final ListenerService service = serviceProvider.getFactoryService(identifier);
                listenerFactory.create(service);
            } catch (final SoulBoundException e) {
                throw new SoulBoundException("Error in '%s' to create the service".formatted(identifier), e);
            }
        }
    }
}
