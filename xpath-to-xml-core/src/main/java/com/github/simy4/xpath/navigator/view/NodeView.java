package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

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
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
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

    @Override
    public String toString() {
        return node.toString();
    }

}
