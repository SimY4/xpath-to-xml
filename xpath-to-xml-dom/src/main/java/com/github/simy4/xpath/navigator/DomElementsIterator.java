package com.github.simy4.xpath.navigator;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomElementsIterator implements Iterator<Node<org.w3c.dom.Node>> {

    private org.w3c.dom.Node child;

    DomElementsIterator(org.w3c.dom.Node parent) {
        this.child = parent.getFirstChild();
    }

    @Override
    public boolean hasNext() {
        return null != child;
    }

    @Override
    public Node<org.w3c.dom.Node> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        final org.w3c.dom.Node next = child;
        child = next.getNextSibling();
        return new DomNode(next);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
