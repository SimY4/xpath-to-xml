package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;

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

    default IterableNodeView<N> filter(Navigator<N> navigator, boolean greedy, Predicate<ViewContext<?>> predicate)
            throws XmlBuilderException {
        return filter(navigator, greedy, 1, predicate);
    }

    IterableNodeView<N> filter(Navigator<N> navigator, boolean greedy, int position,
                               Predicate<ViewContext<?>> predicate) throws XmlBuilderException;

    IterableNodeView<N> flatMap(Navigator<N> navigator, boolean greedy,
                                Function<ViewContext<N>, IterableNodeView<N>> fmap) throws XmlBuilderException;

}
