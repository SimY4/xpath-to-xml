package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;
import org.w3c.dom.NamedNodeMap;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomAttributesIterator implements Iterator<Node<org.w3c.dom.Node>> {

    private final NamedNodeMap attributes;
    private int cursor;

    DomAttributesIterator(org.w3c.dom.Node parent) {
        attributes = parent.getAttributes();
    }

    @Override
    public boolean hasNext() {
        return cursor < attributes.getLength();
    }

    @Override
    public Node<org.w3c.dom.Node> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more attributes");
        }
        return new DomNode(attributes.item(cursor++));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
