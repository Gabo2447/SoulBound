package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.config.migrator.Migrator;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;

import java.io.IOException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Migrator}.
 */
public class MigratorComponent extends AbstractCoreComponent {

    /**
     * Create a new MigratorComponent.
     */
    public MigratorComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Migrator.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final SoulBoundLogger logger = loggerFactory.create(MigratorComponent.class);

        try {
            final Migrator migrator = new Migrator(loggerFactory);
            migrator.migrate();
            provider.take(Migrator.class, migrator);
        } catch (final IOException e) {
            logger.error("There was an exception while migrating from a previous version! Reason: %s".formatted(e.getMessage()), e);
        }
    }
}
