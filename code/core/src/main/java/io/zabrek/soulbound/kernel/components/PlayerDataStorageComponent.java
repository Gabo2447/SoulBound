package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.config.ConfigAccessor;
import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.api.reload.ReloadPhase;
import io.zabrek.soulbound.api.reload.Reloader;
import io.zabrek.soulbound.data.PlayerDataStorage;
import io.zabrek.soulbound.database.Connector;
import io.zabrek.soulbound.database.Saver;
import io.zabrek.soulbound.database.data.PlayerDataFactory;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link PlayerDataStorage}.
 */
public class PlayerDataStorageComponent extends AbstractCoreComponent {

    /**
     * Create a new PlayerDataStorageComponent.
     */
    public PlayerDataStorageComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class, ConfigAccessor.class, Saver.class, Connector.class,
                ProfileProvider.class, Reloader.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(PlayerDataFactory.class, PlayerDataStorage.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final Saver saver = getDependency(Saver.class);
        final Connector connector = getDependency(Connector.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Reloader reloader = getDependency(Reloader.class);

        final PlayerDataFactory playerDataFactory = new PlayerDataFactory(loggerFactory, saver, connector, config);
        final PlayerDataStorage playerDataStorage = new PlayerDataStorage(loggerFactory.create(PlayerDataStorage.class),
                playerDataFactory, profileProvider);

        provider.take(PlayerDataStorage.class, playerDataStorage);
        provider.take(PlayerDataFactory.class, playerDataFactory);
        reloader.register(ReloadPhase.PROFILES, () -> {
            // playerDataStorage.reloadProfiles(profileProvider.getOnlineProfiles());
        });
    }
}
