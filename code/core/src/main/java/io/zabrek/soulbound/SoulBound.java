package io.zabrek.soulbound;

import com.zaxxer.hikari.pool.HikariPool;
import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.config.FileConfigAccessor;
import io.zabrek.soulbound.api.kernel.CoreComponentLoader;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.data.PlayerDataStorage;
import io.zabrek.soulbound.database.Connector;
import io.zabrek.soulbound.database.Saver;
import io.zabrek.soulbound.faststats.FastStatsMetrics;
import io.zabrek.soulbound.kernel.SoulBoundComponents;
import io.zabrek.soulbound.kernel.TopologicalCoreComponentLoader;
import io.zabrek.soulbound.lib.logger.CachingSoulBoundLoggerFactory;
import io.zabrek.soulbound.logger.DefaultSoulBoundLoggerFactory;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.NoSuchElementException;

/**
 * Represents SoulBound plugin.
 */
public class SoulBound extends JavaPlugin {

    /**
     * The SoulBound Plugin instance.
     */
    private static SoulBound instance;

    /**
     * The custom logger for the plugin.
     */
    protected SoulBoundLogger log;

    /**
     * The loader responsible for discovering and registering core components.
     */
    private CoreComponentLoader loader;

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public SoulBound() {
        super();
    }

    /**
     * Get the plugin's instance.
     *
     * @return The plugin's instance.
     */
    public static SoulBound getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        try {
            loader.load();
        } catch (final NoSuchElementException | IllegalStateException
                       | HikariPool.PoolInitializationException e) {
            log.error("Failed to load SoulBound components: %s".formatted(e.getMessage()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log.info("SoulBound has been enabled.");
    }

    @Override
    public void onLoad() {
        final SoulBoundLoggerFactory loggerFactory = new CachingSoulBoundLoggerFactory(new DefaultSoulBoundLoggerFactory());
        this.log = loggerFactory.create(this);

        this.loader = new TopologicalCoreComponentLoader(loggerFactory.create(CoreComponentLoader.class));
        this.loader.init(SoulBoundLoggerFactory.class, loggerFactory);
        initPluginDependencies(loader);
        SoulBoundComponents.createDefaults(this).forEach(loader::register);
    }

    @Override
    public void onDisable() {

        loader.getOptional(ProfileProvider.class).map(ProfileProvider::getOnlineProfiles)
                .ifPresent(onlineProfiles -> onlineProfiles.forEach(onlineProfile -> onlineProfile.getPlayer().closeInventory()));

        loader.getOptional(Saver.class).ifPresent(Saver::end);
        loader.getOptional(Connector.class).ifPresent(connector -> connector.getDatabase().closeConnection());

        loader.getOptional(FastStatsMetrics.class).ifPresent(FastStatsMetrics::disable);

        log.info("SoulBound has been disabled.");
    }

    private void initPluginDependencies(final CoreComponentLoader loader) {
        loader.init(JavaPlugin.class, this);
        loader.init(Server.class, getServer());
        loader.init(PluginManager.class, getServer().getPluginManager());
        loader.init(BukkitScheduler.class, getServer().getScheduler());
        loader.init(PluginDescriptionFile.class, getDescription());
        loader.init(ServicesManager.class, getServer().getServicesManager());
    }

    /**
     * Returns the {@link CoreComponentLoader} instance.
     *
     * @return the {@link CoreComponentLoader} instance.
     */
    public CoreComponentLoader getLoader() {
        return loader;
    }

    /**
     * Get the plugin configuration file.
     *
     * @return config file
     */
    public ConfigAccessor getPluginConfig() {
        return loader.get(FileConfigAccessor.class);
    }

    /**
     * Returns the {@link Saver} instance used by BetonQuest.
     *
     * @return the database saver
     */
    public Saver getSaver() {
        return loader.get(Saver.class);
    }

    /**
     * Gets the stored player data.
     *
     * @return storage for currently loaded player data
     */
    public PlayerDataStorage getPlayerDataStorage() {
        return loader.get(PlayerDataStorage.class);
    }
}
