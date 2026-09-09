package io.zabrek.soulbound.kernel.components;

import dev.faststats.data.Metric;
import io.zabrek.soulbound.api.config.ConfigAccessorFactory;
import io.zabrek.soulbound.api.config.FileConfigAccessor;
import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.reload.ReloadPhase;
import io.zabrek.soulbound.api.reload.Reloader;
import io.zabrek.soulbound.faststats.FastStatsMetricsProvider;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link FileConfigAccessor}.
 */
public class ConfigComponent extends AbstractCoreComponent {

    /**
     * The configuration file name.
     */
    public static final String CONFIG_FILE = "config.yml";

    /**
     * Create a new ConfigComponent.
     */
    public ConfigComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, SoulBoundLoggerFactory.class, ConfigAccessorFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(FileConfigAccessor.class, ConfigMetrics.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final Plugin plugin = getDependency(Plugin.class);
        final ConfigAccessorFactory configAccessorFactory = getDependency(ConfigAccessorFactory.class);
        final Reloader reloader = getDependency(Reloader.class);

        final File dataFolder = plugin.getDataFolder();
        final File configurationFile = new File(dataFolder, CONFIG_FILE);

        try {
            final FileConfigAccessor config = configAccessorFactory.createPatching(configurationFile, plugin, CONFIG_FILE);
            provider.take(FileConfigAccessor.class, config);
            provider.take(ConfigMetrics.class, new ConfigMetrics(config));
            reloader.register(ReloadPhase.CONFIG, () -> reload(config));
        } catch (final InvalidConfigurationException | FileNotFoundException e) {
            throw new IllegalStateException("Could not load the %s file!".formatted(CONFIG_FILE), e);
        }
    }

    private void reload(final FileConfigAccessor config) {
        try {
            config.reload();
        } catch (final IOException e) {
            throw new IllegalStateException("Could not reload the %s file!".formatted(CONFIG_FILE), e);
        }
    }

    /**
     * Metrics provider for config settings.
     *
     * @param fileConfigAccessor the config accessor to read the settings from
     */
    private record ConfigMetrics(FileConfigAccessor fileConfigAccessor) implements FastStatsMetricsProvider {

        @Override
        public Set<Metric<?>> getMetrics() {
            return Set.of(

            );
        }
    }
}
