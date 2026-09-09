package io.zabrek.soulbound.api.dependency;

/**
 * Represents a loaded dependency to be injected.
 *
 * @param <T> the type of the dependency
 * @since 2.0.0
 */
public interface LoadedDependency<T> {

    /**
     * Matches a type against the dependency type.
     *
     * @param type the type to match
     * @return if the dependency type is assignable to the given type
     * @since 2.0.0
     */
    boolean match(Class<?> type);

    /**
     * Gets the dependency type.
     *
     * @return the dependency type.
     * @since 2.0.0
     */
    Class<T> type();

    /**
     * Gets the dependency instance.
     *
     * @return the dependency instance
     * @since 2.0.0
     */
    T dependency();
}
