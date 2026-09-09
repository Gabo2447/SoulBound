package io.zabrek.soulbound.kernel;

import io.zabrek.soulbound.api.kernel.CoreComponent;
import io.zabrek.soulbound.kernel.components.AsyncSaverComponent;
import io.zabrek.soulbound.kernel.components.ConfigAccessorFactoryComponent;
import io.zabrek.soulbound.kernel.components.ConfigComponent;
import io.zabrek.soulbound.kernel.components.DatabaseComponent;
import io.zabrek.soulbound.kernel.components.FastStatsMetricsComponent;
import io.zabrek.soulbound.kernel.components.IdentifiersComponent;
import io.zabrek.soulbound.kernel.components.ListenersComponent;
import io.zabrek.soulbound.kernel.components.LogHandlerComponent;
import io.zabrek.soulbound.kernel.components.MigratorComponent;
import io.zabrek.soulbound.kernel.components.PlayerDataStorageComponent;
import io.zabrek.soulbound.kernel.components.ProfileProviderComponent;
import io.zabrek.soulbound.kernel.components.ReloaderComponent;
import io.zabrek.soulbound.kernel.components.SoulBoundApiComponent;
import io.zabrek.soulbound.kernel.components.types.ListenerTypesComponent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Factory utility class responsible for instantiating and grouping
 * the default core components of the SoulBound plugin.
 *
 * @since 2.0.0
 */
public final class SoulBoundComponents {

    private SoulBoundComponents() {
    }

    /**
     * Creates and returns a set containing all default core components.
     *
     * @param plugin the main JavaPlugin instance
     * @return a set of default core components
     * @since 1.0.0
     */
    public static Set<CoreComponent> createDefaults(final JavaPlugin plugin) {
        return Stream.of(createEssentials(), createDefaultFeatures(), createDefaultTypes(),
                createAdditionalFeatures(), createIntegrationsAndAPI()
        ).flatMap(Set::stream).collect(Collectors.toSet());
    }

    private static Set<CoreComponent> createEssentials() {
        return Set.of(
                new ConfigAccessorFactoryComponent(),
                new ProfileProviderComponent(),
                new ConfigComponent(),
                new AsyncSaverComponent(),
                new DatabaseComponent(),
                new PlayerDataStorageComponent(),
                new ListenersComponent()
        );
    }

    private static Set<CoreComponent> createDefaultFeatures() {
        return Set.of(
                new IdentifiersComponent()
        );
    }

    private static Set<CoreComponent> createIntegrationsAndAPI() {
        return Set.of(
                new SoulBoundApiComponent()
        );
    }

    private static Set<CoreComponent> createAdditionalFeatures() {
        return Set.of(
                new FastStatsMetricsComponent(),
                new ReloaderComponent(),
                new LogHandlerComponent(),
                new MigratorComponent()
        );
    }

    private static Set<CoreComponent> createDefaultTypes() {
        return Set.of(
                new ListenerTypesComponent()
        );
    }
}
