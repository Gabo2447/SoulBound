package io.zabrek.soulbound.lib.dependency;

import io.zabrek.soulbound.api.dependency.DependencyGraphNode;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a directed dependency graph where nodes of type T can have dependencies and dependents.
 * This graph is used to track relationships between nodes, their number of incoming edges, and their dependents.
 *
 * @param <T>        the type of the nodes in the dependency graph
 * @param dependents the set of dependents for each node in the graph
 * @param inDegree   the number of incoming edges to each node in the graph
 */
public record DependencyGraph<T extends DependencyGraphNode>(Map<T, Integer> inDegree, Map<T, Set<T>> dependents) {

    /**
     * Retrieves all nodes from the dependency graph that have zero incoming edges, indicating that
     * it does not depend on any other node in the graph.
     *
     * @return a list of nodes with an in-degree of zero
     */
    public List<T> getNodesWithZeroInDegree() {
        return inDegree.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all nodes from the dependency graph that have at least one incoming edge.
     *
     * @return a list of nodes with a positive in-degree
     */
    public List<T> getNodesWithPositiveInDegree() {
        return inDegree.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Decrements the in-degree value of the specified node in the dependency graph.
     * Reducing the in-degree of a node represents the resolution of a dependency.
     *
     * @param node the node for which the in-degree value should be decremented
     */
    public void decrementInDegree(final T node) {
        inDegree.put(node, Objects.requireNonNull(inDegree.get(node)) - 1);
    }
}
