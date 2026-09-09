package io.zabrek.soulbound.api.service;

import io.zabrek.soulbound.api.SoulBoundApi;
import io.zabrek.soulbound.api.SoulBoundApiService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link SoulBoundApiService}.
 */
public class DefaultSoulBoundApiService implements SoulBoundApiService {

    /**
     * The {@link Supplier} for the {@link SoulBoundApi}.
     */
    private final Function<Plugin, SoulBoundApi> apiSupplier;

    /**
     * The cached {@link SoulBoundApi} instances for each {@link Plugin}.
     */
    private final Map<Plugin, SoulBoundApi> cacheApiInstances;

    /**
     * Creates a new instance of the {@link DefaultSoulBoundApiService}.
     *
     * @param apiSupplier the {@link Supplier} for the {@link SoulBoundApi}
     */
    public DefaultSoulBoundApiService(final Function<Plugin, SoulBoundApi> apiSupplier) {
        this.apiSupplier = apiSupplier;
        this.cacheApiInstances = new HashMap<>();
    }

    @Override
    public SoulBoundApi api(final Plugin plugin) {
        return cacheApiInstances.computeIfAbsent(plugin, apiSupplier);
    }
}
