package com.github.simy4.xpath.dom4j.navigator;

import org.dom4j.Element;

import java.util.Iterator;

final class Dom4jElementsIterator implements Iterator<Dom4jNode> {

    private final Iterator<Element> elementIterator;

    Dom4jElementsIterator(Iterator<Element> elementIterator) {
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
