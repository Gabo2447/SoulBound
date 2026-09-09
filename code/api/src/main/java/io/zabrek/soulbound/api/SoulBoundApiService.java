package io.zabrek.soulbound.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * The SoulBound API service represents the single source of truth for all methods related to SoulBound.
 * Accessing and modifying the current state of SoulBound will primarily be done through this service
 * and the {@link SoulBoundApi} it is providing.
 * <br> <br>
 * The only valid instance for this interface is available through the {@link ServicesManager}
 * and may be obtained calling {@link ServicesManager#load(Class)} with this interface as parameter.
 * Alternatively does {@link #get()} invoke {@link ServicesManager#load(Class)} method for you
 * and returns its nullable result wrapped in an {@link Optional}.
 * <br> <br>
 * The API service is available and ready to use after SoulBound itself has finished enabling and may therefore be called
 * the earliest while enabling a plugin explicitly depending on SoulBound (enabling after SoulBound).
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface SoulBoundApiService {

    /**
     * Attempts to load {@link SoulBoundApiService} from the Bukkit's {@link ServicesManager}.
     * Will return an empty optional if the service is not registered yet, got disabled,
     * or an error caused SoulBound to fail to load entirely.
     *
     * @return an optional containing the API service or an empty optional if the service is not available
     * @since 2.0.0
     */
    @Contract(value = "-> new", pure = true)
    static Optional<SoulBoundApiService> get() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(SoulBoundApiService.class));
    }

    /**
     * Attempts to get the API instance for the specified plugin.
     * <br> <br>
     * To ensure that api is functioning correctly, it is advised that only the specified plugin
     * is going to use the resulting instance hereafter.
     *
     * @param plugin the plugin to get the API instance for
     * @return the API instance for the specified plugin
     * @since 2.0.0
     */
    @Contract(pure = true)
    SoulBoundApi api(Plugin plugin);
}
