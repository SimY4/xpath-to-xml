package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.navigator.view.DomNodeView;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomElementsIterator implements Iterator<NodeView<Node>> {

    private Node child;

    DomElementsIterator(Node parent) {
        this.child = parent.getFirstChild();
    }

    @Override
    public boolean hasNext() {
        return null != child;
    }

    @Override
    public NodeView<Node> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        final Node next = child;
        child = next.getNextSibling();
        return new DomNodeView(next);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
