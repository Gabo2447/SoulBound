package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.api.profile.ProfileProvider;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import io.zabrek.soulbound.profile.DefaultProfileProvider;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link ProfileProvider}.
 */
public class ProfileProviderComponent extends AbstractCoreComponent {

    /**
     * Create a new ProfileProviderComponent.
     */
    public ProfileProviderComponent() {
        super();
    }
    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, Server.class, ServicesManager.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(ProfileProvider.class);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final Plugin plugin = getDependency(Plugin.class);
        final Server server = getDependency(Server.class);
        final ServicesManager servicesManager = getDependency(ServicesManager.class);

        final DefaultProfileProvider profileProvider = new DefaultProfileProvider(server);

        servicesManager.register(ProfileProvider.class, profileProvider, plugin, ServicePriority.Lowest);
        provider.take(ProfileProvider.class, servicesManager.load(ProfileProvider.class));
    }
}
