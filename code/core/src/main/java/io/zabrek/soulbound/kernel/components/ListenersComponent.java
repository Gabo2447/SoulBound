package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.kernel.CoreComponent;
import io.zabrek.soulbound.api.listener.service.DefaultListenerServiceProvider;
import io.zabrek.soulbound.api.listeners.Listener;
import io.zabrek.soulbound.api.listeners.service.ListenerServiceProvider;
import io.zabrek.soulbound.api.logger.SoulBoundLoggerFactory;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.data.PlayerDataStorage;
import io.zabrek.soulbound.id.listener.ListenerIdentifierFactory;
import io.zabrek.soulbound.kernel.DependencyProvider;
import io.zabrek.soulbound.listeners.ListenerTypeRegistry;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link CoreComponent} for {@link Listener}.
 */
public class ListenersComponent implements CoreComponent {

    /**
     * Create a new ListenersComponent.
     */
    public ListenersComponent() {
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
    public void load(final DependencyProvider provider) {
        final SoulBoundLoggerFactory loggerFactory = provider.get(SoulBoundLoggerFactory.class);
        final ProfileProvider profileProvider = provider.get(ProfileProvider.class);
        final Plugin plugin = provider.get(Plugin.class);
        final PlayerDataStorage playerDataStorage = provider.get(PlayerDataStorage.class);

        final DefaultListenerServiceProvider listenerServiceProvider = new DefaultListenerServiceProvider(
                loggerFactory, profileProvider, plugin, playerDataStorage
        );
        final ListenerIdentifierFactory listenerIdentifierFactory = new ListenerIdentifierFactory();
        ListenerTypeRegistry.load(listenerServiceProvider, listenerIdentifierFactory, loggerFactory, plugin);

        provider.take(ListenerIdentifierFactory.class, listenerIdentifierFactory);
        provider.take(ListenerServiceProvider.class, listenerServiceProvider);
    }
}
