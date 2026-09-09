package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.reload.Reloader;
import io.zabrek.soulbound.kernel.DefaultReloader;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultReloader}.
 */
public class ReloaderComponent extends AbstractCoreComponent {

    /**
     * Create a new ReloaderComponent.
     */
    public ReloaderComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(Reloader.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        provider.take(Reloader.class, new DefaultReloader(loggerFactory.create(DefaultReloader.class)));
    }
}
