package io.zabrek.soulbound.kernel.components.types;

import io.zabrek.soulbound.api.SoulBoundException;
import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.identifier.ListenerIdentifier;
import io.zabrek.soulbound.api.listeners.ListenerFactory;
import io.zabrek.soulbound.api.listeners.service.ListenerService;
import io.zabrek.soulbound.api.listeners.service.ListenerServiceProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.id.listener.ListenerIdentifierFactory;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import io.zabrek.soulbound.listeners.death.EntityDeathFactory;
import io.zabrek.soulbound.listeners.join.PlayerJoinFactory;
import io.zabrek.soulbound.listeners.seismic_impact.SeismicImpactFactory;
import io.zabrek.soulbound.listeners.ui.VisualEventFactory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all listeners types.
 */
public class ListenerTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ListenerTypesComponent.
     */
    public ListenerTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class, Plugin.class, ListenerServiceProvider.class,
                ListenerIdentifierFactory.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final Plugin plugin = getDependency(Plugin.class);
        final ListenerServiceProvider listenerServiceProvider = getDependency(ListenerServiceProvider.class);
        final ListenerIdentifierFactory listenerIdentifierFactory = getDependency(ListenerIdentifierFactory.class);

        final SoulBoundLogger log = loggerFactory.create(ListenerTypesComponent.class);

        final Map<String, ListenerFactory> factories = new HashMap<>();
        factories.put("death", new EntityDeathFactory());
        factories.put("join", new PlayerJoinFactory());
        factories.put("seismic", new SeismicImpactFactory());
        factories.put("ui", new VisualEventFactory(plugin));

        load(listenerIdentifierFactory, listenerServiceProvider, factories, log);
    }

    private void load(final ListenerIdentifierFactory identifierFactory, final ListenerServiceProvider serviceProvider,
                      final Map<String, ListenerFactory> factories, final SoulBoundLogger log) {
        try {
            log.info("Loading %d listener components...".formatted(factories.size()));
            loadFactory(identifierFactory, serviceProvider, factories);
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
