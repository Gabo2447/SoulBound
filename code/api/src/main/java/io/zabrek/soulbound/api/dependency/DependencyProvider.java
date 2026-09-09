package io.zabrek.soulbound.api.dependency;

/**
 * Provides dependencies to the core components in the
 * {@link CoreComponent#loadComponent(DependencyProvider)} method and distributes them
 * across all components in the {@link CoreComponentLoader}.
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface DependencyProvider {

    /**
     * Provides a new loaded dependency to the core components.
     *
     * @param type       the type of the dependency
     * @param dependency the dependency instance
     * @param <U>        the type of the dependency
     * @since 2.0.0
     */
    <U> void take(Class<U> type, U dependency);
}
