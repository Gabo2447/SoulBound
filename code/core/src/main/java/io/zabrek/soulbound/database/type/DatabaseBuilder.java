package io.zabrek.soulbound.database.type;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.database.provider.ConnectionProvider;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Builder for dynamically instantiating database implementations.
 *
 * @param <T> the database type being built
 */
public final class DatabaseBuilder<T extends Database> {

    /**
     * Target database class.
     */
    private final Class<T> dbClass;

    /**
     * Custom logger instance.
     */
    private SoulBoundLogger log;

    /**
     * Connection provider implementation.
     */
    private ConnectionProvider connProvider;

    /**
     * Plugin instance.
     */
    private Plugin pluginInstance;

    /**
     * The config accessor instance.
     */
    private ConfigAccessor configAccessor;

    private DatabaseBuilder(final Class<T> dbClass) {
        this.dbClass = dbClass;
    }

    /**
     * Initializes a new builder instance.
     *
     * @param databaseClass the database class to instantiate
     * @param <T>           the database type
     * @return a new builder instance
     */
    public static <T extends Database> DatabaseBuilder<T> request(final Class<T> databaseClass) {
        return new DatabaseBuilder<>(databaseClass);
    }

    /**
     * Sets the logger.
     *
     * @param logger the logger instance
     * @return this builder
     */
    public DatabaseBuilder<T> logger(final SoulBoundLogger logger) {
        this.log = logger;
        return this;
    }

    /**
     * Sets the plugin.
     *
     * @param plugin the plugin instance
     * @return this builder
     */
    public DatabaseBuilder<T> plugin(final Plugin plugin) {
        this.pluginInstance = plugin;
        return this;
    }

    /**
     * Sets the connection provider.
     *
     * @param connectionProvider the connection provider
     * @return this builder
     */
    public DatabaseBuilder<T> connectionProvider(final ConnectionProvider connectionProvider) {
        this.connProvider = connectionProvider;
        return this;
    }

    /**
     *  Sets the config accessor.
     *
     * @param configAccessor the config accessor
     * @return this builder
     */
    public DatabaseBuilder<T> config(final ConfigAccessor configAccessor) {
        this.configAccessor = configAccessor;
        return this;
    }

    /**
     * Instantiates and returns the database.
     *
     * @return the built database instance
     */
    public T build() {
        final DatabaseConfig config = new DatabaseConfig(log, connProvider, pluginInstance, configAccessor);

        try {
            return dbClass.getDeclaredConstructor(DatabaseConfig.class).newInstance(config);
        } catch (final NoSuchMethodException
                       | InstantiationException
                       | IllegalAccessException
                       | InvocationTargetException e) {
            throw new IllegalStateException("Failed to instantiate '%s'".formatted(dbClass.getName()), e);
        }
    }
}
