package com.github.simy4.xpath.navigator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomAttributesIterator implements Iterator<NodeWrapper<Node>> {

    private final NamedNodeMap attributes;
    private int cursor;

    DomAttributesIterator(Node parent) {
        attributes = parent.getAttributes();
    }

    @Override
    public boolean hasNext() {
        return cursor < attributes.getLength();
    }

    @Override
    public NodeWrapper<Node> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more attributes");
        }
        return new DomNodeWrapper(attributes.item(cursor++));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
