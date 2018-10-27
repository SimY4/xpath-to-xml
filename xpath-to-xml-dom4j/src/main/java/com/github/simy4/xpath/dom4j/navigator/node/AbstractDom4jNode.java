package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Node;

abstract class AbstractDom4jNode<N extends Node> implements Dom4jNode {

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
    public final boolean equals(Object o) {
        return (this == o || (o instanceof AbstractDom4jNode && node.equals(((AbstractDom4jNode<?>) o).node)));
    }

    @Override
    public final int hashCode() {
        return node.hashCode();
    }

    @Override
    public String toString() {
        return node.toString();
    }

}
