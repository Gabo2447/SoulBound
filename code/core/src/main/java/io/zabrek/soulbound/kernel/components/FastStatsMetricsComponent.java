package io.zabrek.soulbound.kernel.components;

import io.zabrek.soulbound.api.dependency.DependencyProvider;
import io.zabrek.soulbound.faststats.FastStatsMetrics;
import io.zabrek.soulbound.faststats.FastStatsMetricsProvider;
import io.zabrek.soulbound.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link FastStatsMetrics}.
 */
public class FastStatsMetricsComponent extends AbstractCoreComponent {

    /**
     * The token to use for metrics publication to FastStats.
     * According to FastStats' documentation, this token is safe for shipping with the plugin's code.
     */
    private static final String TOKEN = "83837c01802c008bf5acd7e3cc4d87d4";

    /**
     * Create a new FastStatsMetricsComponent instance.
     */
    public FastStatsMetricsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(JavaPlugin.class);
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of(FastStatsMetrics.class);
    }

    @Override
    protected boolean requires(final Class<?> type) {
        return FastStatsMetricsProvider.class.isAssignableFrom(type) || super.requires(type);
    }

    @Override
    public void load(final DependencyProvider provider) {
        final JavaPlugin plugin = getDependency(JavaPlugin.class);

        final Set<FastStatsMetricsProvider> fastStatsMetricsProviders =  injectedDependencies.stream()
                .filter(injectedDependency -> FastStatsMetricsProvider.class.isAssignableFrom(injectedDependency.type()))
                .map(injectedDependency -> (FastStatsMetricsProvider) injectedDependency.dependency())
                .collect(Collectors.toSet());
        final FastStatsMetrics fastStatsMetrics = new FastStatsMetrics(plugin, TOKEN, fastStatsMetricsProviders, true);
        fastStatsMetrics.enable();

        provider.take(FastStatsMetrics.class, fastStatsMetrics);
    }
}
