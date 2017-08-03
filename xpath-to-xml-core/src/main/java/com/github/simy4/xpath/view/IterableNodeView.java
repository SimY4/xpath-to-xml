package com.github.simy4.xpath.view;

import com.github.simy4.xpath.navigator.Node;

/**
 * Abstract XML view to generify access to {@link NodeView} and {@link NodeSetView}.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 * @see NodeView
 * @see NodeSetView
 */
public interface IterableNodeView<N extends Node> extends View<N>, Iterable<NodeView<N>> {

    /**
     * Node view predicate.
     *
     * @param <T> filter type
     */
    interface Filter<T> {

        /**
         * Applies predicate to a given value.
         *
         * @param t value to test
         * @return {@code true} if value matches predicate or {@code false} otherwise
         */
        boolean test(T t);

    }

}
