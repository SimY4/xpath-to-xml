package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class NodeView<N> implements View<N> {

    private final Node<N> node;

    public NodeView(Node<N> node) {
        this.node = node;
    }

    @Override
    public int compareTo(View<N> other) {
        return -other.compareTo(this);
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public Number toNumber() {
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

    public Node<N> getNode() {
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NodeView<?> nodeView = (NodeView<?>) o;

        return node.equals(nodeView.node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

}
