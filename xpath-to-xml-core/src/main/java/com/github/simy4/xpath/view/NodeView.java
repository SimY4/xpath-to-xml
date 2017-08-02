package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

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
    public int size() {
        return 1;
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return Collections.singleton(this).iterator();
    }

    public N getNode() {
        return node;
    }

}
