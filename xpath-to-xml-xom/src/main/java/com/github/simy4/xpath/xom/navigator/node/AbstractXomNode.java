package com.github.simy4.xpath.xom.navigator.node;

import nu.xom.Node;

abstract class AbstractXomNode<N extends Node> implements XomNode {

    private final N node;

    AbstractXomNode(N node) {
        this.node = node;
    }

    @Override
    public final N getNode() {
        return node;
    }

    @Override
    public final boolean equals(Object o) {
        return (this == o || (o instanceof AbstractXomNode && node.equals(((AbstractXomNode<?>) o).node)));
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
