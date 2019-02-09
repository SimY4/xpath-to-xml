package com.github.simy4.xpath.view;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;

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

    IterableNodeView<N> flatMap(Function<? super NodeView<N>, ? extends IterableNodeView<N>> fmap);

}
