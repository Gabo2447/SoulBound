package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.config.ConfigAccessorFactory;
import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.database.AsyncSaver;
import io.zabrek.soulbound.database.Backup;
import io.zabrek.soulbound.database.Connector;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link AsyncSaver}.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
public class AsyncSaverComponent extends AbstractCoreComponent {

    /**
     * Create a new AsyncSaverComponent.
     */
    public AsyncSaverComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, SoulBoundLoggerFactory.class, ConfigAccessorFactory.class, Connector.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(AsyncSaver.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final Plugin plugin = getDependency(Plugin.class);
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final Connector connector = getDependency(Connector.class);

        final AsyncSaver saver = new AsyncSaver(loggerFactory.create(AsyncSaver.class, "AsyncSaver"), connector);
        new Backup(loggerFactory, loggerFactory.create(Backup.class), configAccessorFactory, plugin.getDataFolder(), connector)
                .loadDatabaseFromBackup();

        provider.take(AsyncSaver.class, saver);
    }
}
