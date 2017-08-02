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
     * Size of an iterable.
     *
     * @return iterable size
     */
    int size();

}
