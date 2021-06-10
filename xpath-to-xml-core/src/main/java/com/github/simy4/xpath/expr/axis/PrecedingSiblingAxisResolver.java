package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PrecedingSiblingAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  private final boolean sibling;

  public PrecedingSiblingAxisResolver(QName name, boolean sibling) {
    super(name);
    this.sibling = sibling;
  }

  @Override
  protected <N extends Node> Iterable<? extends N> traverseAxis(
      Navigator<N> navigator, NodeView<N> view) {
    final N node = view.getNode();
    final N parent = navigator.parentOf(node);
    return null == parent
        ? Collections.emptyList()
        : new PrecedingSiblingIterable<>(navigator, parent, node, sibling);
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> node, int position) throws XmlBuilderException {
    if (isWildcard()) {
      throw new XmlBuilderException("Wildcard elements cannot be created");
    }
    final N nodeNode = node.getNode();
    final N newElement = navigator.createElement(navigator.parentOf(nodeNode), name);
    navigator.appendPrev(nodeNode, newElement);
    return new NodeView<>(newElement, position);
  }

  @Override
  public String toString() {
    return (sibling ? "preceding-sibling::" : "preceding::") + super.toString();
  }

  private static final class PrecedingSiblingIterable<T extends Node> implements Iterable<T> {

    private final Navigator<T> navigator;
    private final T parent;
    private final T current;
    private final boolean sibling;

    private PrecedingSiblingIterable(Navigator<T> navigator, T parent, T current, boolean sibling) {
      this.navigator = navigator;
      this.parent = parent;
      this.current = current;
      this.sibling = sibling;
    }

    @Override
    public Iterator<T> iterator() {
      final Iterator<T> preceding =
          new Preceding<>(navigator.elementsOf(parent).iterator(), current);
      return sibling
          ? preceding
          : new DescendantOrSelfAxisResolver.DescendantOrSelf<>(navigator, preceding);
    }
  }

  private static final class Preceding<T extends Node> implements Iterator<T> {

    private final Iterator<? extends T> children;
    private final T current;
    private boolean noMoreElements;
    private boolean hasPeeked;
    private T next;

    private Preceding(Iterator<? extends T> children, T current) {
      this.children = children;
      this.current = current;
    }

    @Override
    public boolean hasNext() {
      if (!hasPeeked) {
        if (children.hasNext()) {
          final T next = children.next();
          hasPeeked = true;
          if (next == current) {
            noMoreElements = true;
          } else {
            this.next = next;
          }
        } else {
          noMoreElements = true;
        }
      }
      return !noMoreElements;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements");
      }
      hasPeeked = false;
      return next;
    }
  }
}
