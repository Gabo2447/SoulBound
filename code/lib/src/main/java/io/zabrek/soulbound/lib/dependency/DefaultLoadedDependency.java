package io.zabrek.soulbound.lib.dependency;

import io.zabrek.soulbound.api.dependency.LoadedDependency;

/**
 * The record representing an injected dependency.
 *
 * @param type       the type of the dependency
 * @param dependency the dependency instance itself
 * @param <T>        the generic type of the dependency
 */
public record DefaultLoadedDependency<T>(Class<T> type, T dependency) implements LoadedDependency<T> {

    @Override
    public boolean match(final Class<?> type) {
        return type.isAssignableFrom(this.type);
    }
}
