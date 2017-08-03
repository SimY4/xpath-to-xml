package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

/**
 * XPath expression context.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@NotThreadSafe
@SuppressWarnings("WeakerAccess")
public final class ExprContext<N extends Node> implements Iterator<NodeView<N>> {

    private final Navigator<N> navigator;
    private final boolean greedy;
    private final Iterator<NodeView<N>> nodeSetIterator;
    private int position;
    private NodeView<N> current;

    public ExprContext(Navigator<N> navigator, boolean greedy, IterableNodeView<N> nodeSet) {
        this(navigator, greedy, nodeSet, 0);
    }

    /**
     * Constructor.
     *
     * @param navigator XML model navigator
     * @param greedy    {@code true} if you want to evaluate expression greedily and {@code false} otherwise
     * @param nodeSet   context bound XML node set
     * @param position  context position
     */
    public ExprContext(Navigator<N> navigator, boolean greedy, IterableNodeView<N> nodeSet, @Nonnegative int position) {
        this.navigator = navigator;
        this.greedy = greedy;
        this.nodeSetIterator = nodeSet.iterator();
        this.position = position;
    }

    @Override
    public boolean hasNext() {
        return nodeSetIterator.hasNext();
    }

    @Override
    public NodeView<N> next() {
        position += 1;
        current = nodeSetIterator.next();
        return current;
    }

    @Override
    public void remove() {
        nodeSetIterator.remove();
    }

    public Navigator<N> getNavigator() {
        return navigator;
    }

    public NodeView<N> getCurrent() {
        assert current != null : "ExprContext.getCurrent was called before context was initialized";
        return current;
    }

    public int getPosition() {
        return position;
    }

    public boolean shouldCreate() {
        return !nodeSetIterator.hasNext() && greedy;
    }

    public ExprContext<N> clone(IterableNodeView<N> nodeSet) {
        return new ExprContext<N>(navigator, greedy, nodeSet);
    }

}
