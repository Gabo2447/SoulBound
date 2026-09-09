package io.zabrek.soulbound.api.dependency;

import java.util.Set;

/**
 * Represents a node in the dependency graph.
 *
 * @since 2.0.0
 */
public interface DependencyGraphNode {

    /**
     * Specifies an unmodifiable set of types that this node requires to be loaded before itself.
     * Essentially contains all classes that are dependencies of this node.
     *
     * @return all dependencies of this node
     * @since 2.0.0
     */
    Set<Class<?>> requires();

    /**
     * Specifies an unmodifiable set of types that this node creates and handles in the loading process.
     * Essentially contains all classes that are provided by this node.
     *
     * @return all provided types of this node
     * @since 2.0.0
     */
    Set<Class<?>> provides();
}
