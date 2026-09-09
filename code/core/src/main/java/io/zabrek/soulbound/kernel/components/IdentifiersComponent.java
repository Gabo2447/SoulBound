package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.kernel.registry.soul.IdentifierTypeRegistry;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link IdentifierTypeRegistry}.
 */
public class IdentifiersComponent extends AbstractCoreComponent {

    /**
     * Create a new IdentifiersComponent.
     */
    public IdentifiersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(IdentifierTypeRegistry.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final IdentifierTypeRegistry identifierTypeRegistry = new IdentifierTypeRegistry(loggerFactory.create(IdentifierTypeRegistry.class));

        provider.take(IdentifierTypeRegistry.class, identifierTypeRegistry);
    }
}
