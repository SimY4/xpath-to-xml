package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Node;

abstract class AbstractDom4jNode<N extends Node> implements Dom4jNode<N> {

    private final N node;

    AbstractDom4jNode(N node) {
        this.node = node;
    }

    @Override
    public final N getNode() {
        return node;
    }

    @Override
    public final String getText() {
        return node.getText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractDom4jNode<?> that = (AbstractDom4jNode<?>) o;

        return node.equals(that.node);
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
