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
        if (other instanceof NodeView) {
            return node.getText().compareTo(((NodeView) other).getNode().getText());
        } else {
            return -other.compareTo(this);
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
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return Collections.singleton(this).iterator();
    }

    @Override
    public IterableNodeView<N> filter(Navigator<N> navigator, boolean greedy, Predicate<ViewContext<N>> predicate)
            throws XmlBuilderException {
        ViewContext<N> context = new ViewContext<N>(navigator, this, greedy, false, 1);
        return predicate.test(context) ? this : NodeSetView.<N>empty();
    }

    @Override
    public IterableNodeView<N> flatMap(Navigator<N> navigator, boolean greedy,
                                       Function<ViewContext<N>, IterableNodeView<N>> fmap) throws XmlBuilderException {
        return flatMap(navigator, greedy, 1, fmap);
    }

    @Override
    public IterableNodeView<N> flatMap(Navigator<N> navigator, boolean greedy, int position,
                                       Function<ViewContext<N>, IterableNodeView<N>> fmap) throws XmlBuilderException {
        ViewContext<N> context = new ViewContext<N>(navigator, this, greedy, false, 1);
        return fmap.apply(context);
    }

    public N getNode() {
        return node;
    }

}
