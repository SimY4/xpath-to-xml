package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.navigator.view.DomNodeView;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomAttributesIterator implements Iterator<NodeView<Node>> {

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
    public NodeView<Node> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more attributes");
        }
        return new DomNodeView(attributes.item(cursor++));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
