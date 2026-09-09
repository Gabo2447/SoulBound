package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.config.ConfigAccessorFactory;
import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.lib.config.DefaultConfigAccessorFactory;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ConfigAccessorFactory}.
 */
public class ConfigAccessorFactoryComponent extends AbstractCoreComponent {


    /**
     * Create a new ConfigAccessorFactoryComponent.
     */
    public ConfigAccessorFactoryComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ConfigAccessorFactory.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));
        provider.take(ConfigAccessorFactory.class, configAccessorFactory);
    }
}
