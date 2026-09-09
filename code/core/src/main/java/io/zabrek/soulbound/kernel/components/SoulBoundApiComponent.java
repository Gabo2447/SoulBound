package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.SoulBoundApi;
import io.zabrek.soulbound.api.SoulBoundApiService;
import io.zabrek.soulbound.api.bukkit.BukkitManager;
import io.zabrek.soulbound.api.kernel.CoreComponent;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.api.reload.Reloader;
import io.zabrek.soulbound.api.service.DefaultSoulBoundApiService;
import io.zabrek.soulbound.kernel.DependencyProvider;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;
import java.util.function.Function;

/**
 * The implementation of {@link CoreComponent} for {@link DefaultSoulBoundApi}.
 */
public class SoulBoundApiComponent implements CoreComponent {

    /**
     * Create a new SoulBoundApiComponent.
     */
    public SoulBoundApiComponent() {}

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, ServicesManager.class, SoulBoundLoggerFactory.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(DefaultSoulBoundApiService.class, DefaultSoulBoundApi.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final ServicesManager servicesManager = provider.get(ServicesManager.class);
        final SoulBoundLoggerFactory loggerFactory = provider.get(SoulBoundLoggerFactory.class);
        final ProfileProvider profileProvider = provider.get(ProfileProvider.class);
        final Reloader reloader = provider.get(Reloader.class);
        final Plugin plugin = provider.get(Plugin.class);

        final SoulBoundLogger serviceLogger = loggerFactory.create(SoulBoundApiService.class);
        final Function<Plugin, SoulBoundApi> defaultSoulBoundApiGenerator = callerPlugin -> {
            serviceLogger.debug("Loading API for plugin %s version %s".formatted(callerPlugin.getName(),
                    callerPlugin.getDescription().getVersion()));
            final BukkitManager bukkitManager = new DefaultBukkitManager(callerPlugin);
            return new DefaultSoulBoundApi(callerPlugin, profileProvider, loggerFactory, reloader, bukkitManager);
        };
        final DefaultSoulBoundApiService defaultSoulBoundApiService = new DefaultSoulBoundApiService(defaultSoulBoundApiGenerator);
        servicesManager.register(SoulBoundApiService.class, defaultSoulBoundApiService, plugin, ServicePriority.Highest);

        provider.take(DefaultSoulBoundApiService.class, defaultSoulBoundApiService);
        provider.take(DefaultSoulBoundApi.class, (DefaultSoulBoundApi) defaultSoulBoundApiGenerator.apply(plugin));
    }

    /**
     * The default implementation of the {@link BukkitManager}.
     *
     * @param plugin the plugin this manager and the api is created for
     */
    /* default */
    record DefaultBukkitManager(Plugin plugin) implements BukkitManager {

        @Override
        public void registerEvents(final Listener listener) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    /**
     * The default implementation of the {@link SoulBoundApi}.
     *
     * @param attachedPlugin the plugin this api instance is created for
     * @param profiles the profile provider handling profiles for players
     * @param loggerFactory the logger factory to create logger for individual services
     * @param reloader the reloader
     * @param bukkit the Bukkit manager
     */
    /* default */ record DefaultSoulBoundApi(Plugin attachedPlugin, ProfileProvider profiles,
                                             SoulBoundLoggerFactory loggerFactory, Reloader reloader,
                                             BukkitManager bukkit) implements SoulBoundApi {

    }
}
