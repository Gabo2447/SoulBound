package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.kernel.CoreComponent;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.config.migrator.Migrator;
import io.zabrek.soulbound.kernel.DependencyProvider;

import java.io.IOException;
import java.util.Set;

/**
 * The implementation of {@link CoreComponent} for {@link Migrator}.
 */
public class MigratorComponent implements CoreComponent {

    /**
     * Create a new MigratorComponent.
     */
    public MigratorComponent() {
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
        final SoulBoundLoggerFactory loggerFactory = provider.get(SoulBoundLoggerFactory.class);
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
