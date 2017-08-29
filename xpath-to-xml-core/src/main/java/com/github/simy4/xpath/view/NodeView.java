package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Iterator;

@Immutable
public final class NodeView<N extends Node> implements IterableNodeView<N> {

    private final N node;

    public NodeView(N node) {
        this.node = node;
    }

    @Override
    public int compareTo(@Nonnull View<N> other) {
        final String text = node.getText();
        if (null == text) {
            return other.toBoolean() ? -1 : 0;
        } else {
            return text.compareTo(other.toString());
        }
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public double toNumber() {
        try {
            return Double.parseDouble(node.getText());
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

    @Override
    public String toString() {
        return node.getText();
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return Collections.singleton(this).iterator();
    }

    @Override
    public IterableNodeView<N> filter(Navigator<N> navigator, boolean greedy, Predicate<ViewContext<?>> predicate)
            throws XmlBuilderException {
        return filter(navigator, greedy, 1, predicate);
    }

    @Override
    public IterableNodeView<N> filter(Navigator<N> navigator, boolean greedy, int position,
                                      Predicate<ViewContext<?>> predicate) throws XmlBuilderException {
        final ViewContext<N> context = new ViewContext<N>(navigator, this, greedy, false, position);
        return predicate.test(context) ? this : NodeSetView.<N>empty();
    }

    @Override
    public IterableNodeView<N> flatMap(Navigator<N> navigator, boolean greedy,
                                       Function<ViewContext<N>, IterableNodeView<N>> fmap) throws XmlBuilderException {
        final ViewContext<N> context = new ViewContext<N>(navigator, this, greedy, false, 1);
        return fmap.apply(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || !o.getClass().isAssignableFrom(View.class)) {
            return false;
        }

        final String text = node.getText();
        return null != text ? text.equals(o.toString()) : !((View<?>) o).toBoolean();
    }

    @Override
    public int hashCode() {
        final String text = node.getText();
        return null != text ? text.hashCode() : 0;
    }

    public N getNode() {
        return node;
    }

}
