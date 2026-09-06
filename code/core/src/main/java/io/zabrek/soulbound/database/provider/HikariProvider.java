package io.zabrek.soulbound.database.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.zabrek.soulbound.api.logger.SoulBoundLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * A connection provider that uses HikariCP.
 */
public class HikariProvider implements ConnectionProvider {

    /**
     * The custom logger instances.
     */
    private final SoulBoundLogger log;

    /**
     * The driver to use.
     */
    private final HikariDriver driver;

    /**
     * The hikari data source.
     */
    private final HikariDataSource dataSource;

    /**
     * Create a new HikariCP connection provider.
     *
     * @param log    the logger to use
     * @param driver the driver to use
     * @param args   the arguments to pass to the driver
     */
    public HikariProvider(final SoulBoundLogger log, final HikariDriver driver, final String... args) {
        this.log = log;
        this.driver = driver;

        if (args.length != driver.getRequiredArgs()) {
            throw new IllegalArgumentException("Invalid number of arguments '%s' != '%s' for driver '%s'"
                    .formatted(args.length, driver.getRequiredArgs(), driver.name()));
        }

        log.debug("Initializing HikariCP pool for driver '%s'".formatted(driver.name()));
        final HikariConfig config = driver.getConfig(args);

        this.dataSource = new HikariDataSource(config);
        log.debug("HikariCP pool initialized successfully");
    }

    @Override
    public Connection create() {
        try {
            log.debug("Obtaining database connection from HikariCP pool...");
            return dataSource.getConnection();
        } catch (final SQLException e) {
            throw new IllegalStateException("Failed to create '%s' connection via hikari pool".formatted(driver.name()), e);
        }
    }

    @Override
    public void close() {
        log.debug("Closing HikariCP data source pool...");
        dataSource.close();
        log.debug("HikariCP data source pool closed.");
    }

    /**
     * Enum for the different database drivers.
     */
    public enum HikariDriver {
        /**
         * SQLite driver configuration for HikariCP.
         * <p>
         * Takes one argument:
         * <ul>
         * <li>0: the absolute path to the database file</li>
         * </ul>
         */
        SQLITE(1, args -> {
            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("soulbound-sqlite-pool");
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:%s".formatted(args[0]));
            hikariConfig.setMaximumPoolSize(1);
            hikariConfig.setInitializationFailTimeout(-1);
            hikariConfig.addDataSourceProperty("journal_mode", "WAL");
            hikariConfig.addDataSourceProperty("synchronous", "NORMAL");
            hikariConfig.addDataSourceProperty("busy_timeout", "5000");
            return hikariConfig;
        }),
        /**
         * MySQL driver configuration for HikariCP.
         * <p>
         * Takes five arguments:
         * <ul>
         * <li>0: the host of the database </li>
         * <li>1: the port of the database </li>
         * <li>2: the name of the database </li>
         * <li>3: the username to connect with </li>
         * <li>4: the password to connect with </li>
         * </ul>
         */
        MYSQL(5, args -> {
            final HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("soulbound-mysql-pool");
            hikariConfig.setJdbcUrl("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false".formatted(args[0], args[1], args[2]));
            hikariConfig.setUsername(args[3]);
            hikariConfig.setPassword(args[4]);
            hikariConfig.addDataSourceProperty("allowPublicKeyRetrieval", "true");
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            hikariConfig.addDataSourceProperty("createDatabaseIfNotExist", "true");
            return hikariConfig;
        });

        /**
         * The number of required arguments for this driver.
         */
        private final int requiredArgs;

        /**
         * The configuration provider function.
         */
        private final Function<String[], HikariConfig> config;

        HikariDriver(final int requiredArgs, final Function<String[], HikariConfig> config) {
            this.requiredArgs = requiredArgs;
            this.config = config;
        }

        /**
         * Get the driver-specific configuration for the given arguments.
         *
         * @param args the arguments
         * @return the configuration
         */
        public HikariConfig getConfig(final String... args) {
            return config.apply(args);
        }

        /**
         * Get the number of required arguments for this driver.
         *
         * @return the number of required arguments
         */
        public int getRequiredArgs() {
            return requiredArgs;
        }
    }
}
