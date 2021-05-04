package com.github.simy4.xpath.dom.navigator;

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomElementsIterable implements Iterable<DomNode> {

  private final Node parent;

  DomElementsIterable(Node parent) {
    this.parent = parent;
  }

  @Override
  public Iterator<DomNode> iterator() {
    return new DomElementsIterator(parent);
  }

  private static final class DomElementsIterator implements Iterator<DomNode> {

    private Node child;

    private DomElementsIterator(Node parent) {
      this.child = nextElement(parent.getFirstChild());
    }

    @Override
    public boolean hasNext() {
      return null != child;
    }

    @Override
    public DomNode next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements");
      }
      final Node next = child;
      child = nextElement(next.getNextSibling());
      return new DomNode(next);
    }

    private Node nextElement(Node next) {
      while (next != null && Node.ELEMENT_NODE != next.getNodeType()) {
        next = next.getNextSibling();
      }
      return next;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
