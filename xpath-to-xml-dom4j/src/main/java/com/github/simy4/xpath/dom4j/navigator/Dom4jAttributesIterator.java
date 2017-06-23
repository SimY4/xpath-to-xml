package com.github.simy4.xpath.dom4j.navigator;

import org.dom4j.Attribute;

import java.util.Iterator;

final class Dom4jAttributesIterator implements Iterator<Dom4jNode> {

    private final Iterator<Attribute> elementIterator;

    Dom4jAttributesIterator(Iterator<Attribute> elementIterator) {
        this.elementIterator = elementIterator;
    }

    @Override
    public boolean hasNext() {
        return elementIterator.hasNext();
    }

    @Override
    public Dom4jNode next() {
        return new Dom4jNode(elementIterator.next());
    }

    @Override
    public void remove() {
        elementIterator.remove();
    }

}
