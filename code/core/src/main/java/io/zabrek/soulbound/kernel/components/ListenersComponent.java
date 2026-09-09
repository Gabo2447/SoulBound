package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.listener.service.DefaultListenerServiceProvider;
import io.zabrek.soulbound.api.listeners.Listener;
import io.zabrek.soulbound.api.listeners.service.ListenerServiceProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.data.PlayerDataStorage;
import io.zabrek.soulbound.id.listener.ListenerIdentifierFactory;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Listener}.
 */
public class ListenersComponent extends AbstractCoreComponent {

    /**
     * Create a new ListenersComponent.
     */
    public ListenersComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(SoulBoundLoggerFactory.class, ProfileProvider.class, PlayerDataStorage.class, Plugin.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ListenerIdentifierFactory.class, ListenerServiceProvider.class);
    }

    @Override
    protected boolean requires(final Class<?> type) {
        return PlayerDataStorage.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = getDependency(SoulBoundLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Plugin plugin = getDependency(Plugin.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);

        final DefaultListenerServiceProvider listenerServiceProvider = new DefaultListenerServiceProvider(
                loggerFactory, profileProvider, plugin, playerDataStorage
        );
        final ListenerIdentifierFactory listenerIdentifierFactory = new ListenerIdentifierFactory();

        provider.take(ListenerIdentifierFactory.class, listenerIdentifierFactory);
        provider.take(ListenerServiceProvider.class, listenerServiceProvider);
    }
}
