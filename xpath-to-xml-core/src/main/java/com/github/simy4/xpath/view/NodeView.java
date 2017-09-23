package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.google.errorprone.annotations.Immutable;

import java.util.Collections;
import java.util.Iterator;

@Immutable(containerOf = "N")
public final class NodeView<N extends Node> implements IterableNodeView<N> {

    private final N node;

    public NodeView(N node) {
        this.node = node;
    }

    @Override
    public int compareTo(View<N> other) {
        return toString().compareTo(other.toString());
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
        return ((this == o || null != o) && o instanceof View) && toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public N getNode() {
        return node;
    }

}
