package io.zabrek.soulbound.database.factory;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.database.provider.ConnectionProvider;
import io.zabrek.soulbound.database.provider.HikariProvider;
import io.zabrek.soulbound.database.provider.SqliteJdbcProvider;
import io.zabrek.soulbound.database.type.Database;
import io.zabrek.soulbound.database.type.DatabaseBuilder;
import io.zabrek.soulbound.database.type.SQLite;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Creates a new Sqlite Database.
 */
public class SqliteFactory implements DatabaseFactory {

    /**
     * Empty constructor for PMD.
     */
    public SqliteFactory() {
    }

    @Override
    public Database create(final ConfigAccessor config, final Plugin plugin, final SoulBoundLoggerFactory loggerFactory) {
        final boolean hikariEnabled = config.getBoolean("database.hikari_pooling", true);
        final SoulBoundLogger log = loggerFactory.create(this.getClass());

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            log.error("unable to create plugin data folder!");
        }

        final File file = new File(plugin.getDataFolder(), "database.db");

        final ConnectionProvider sqliteProvider = hikariEnabled
                ? new HikariProvider(loggerFactory.create(HikariProvider.class, "HikariCP"), HikariProvider.HikariDriver.SQLITE, file.getAbsolutePath())
                : new SqliteJdbcProvider(loggerFactory.create(SqliteJdbcProvider.class, "SQLite"), file.getAbsolutePath());

        return DatabaseBuilder.request(SQLite.class)
                .config(config)
                .plugin(plugin)
                .logger(loggerFactory.create(SQLite.class))
                .connectionProvider(sqliteProvider)
                .build();
    }
}
