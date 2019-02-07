package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.util.Collections;
import java.util.Iterator;

public final class NodeView<N extends Node> implements IterableNodeView<N> {

    private final N node;
    private final boolean isNew;
    private boolean marked;

    public NodeView(N node) {
        this(node, false);
    }

    public NodeView(N node, boolean isNew) {
        this.node = node;
        this.isNew = isNew;
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
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public Iterator<NodeView<N>> iterator() {
        return Collections.singleton(this).iterator();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof NodeView<?> && node.equals(((NodeView<?>) o).node));
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    public N getNode() {
        return node;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isMarked() {
        return marked;
    }

    public void mark() {
        marked = true;
    }

}
