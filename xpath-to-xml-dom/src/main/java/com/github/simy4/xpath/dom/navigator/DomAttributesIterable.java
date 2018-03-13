package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.util.ReadOnlyIterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomAttributesIterable implements Iterable<DomNode> {

    private final Node parent;

    DomAttributesIterable(Node parent) {
        this.parent = parent;
    }

    @Override
    public Iterator<DomNode> iterator() {
        return new DomAttributesIterator(parent);
    }

    private static final class DomAttributesIterator extends ReadOnlyIterator<DomNode> {

        private final NamedNodeMap attributes;
        private int cursor;

        private DomAttributesIterator(Node parent) {
            attributes = parent.getAttributes();
        }

        @Override
        public boolean hasNext() {
            return cursor < attributes.getLength();
        }

        @Override
        public DomNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more attributes");
            }
            return new DomNode(attributes.item(cursor++));
        }

    }

}
