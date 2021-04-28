package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

import java.util.Collections;
import java.util.Iterator;

public class FollowingSiblingAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  private final boolean sibling;

  public FollowingSiblingAxisResolver(QName name, boolean sibling) {
    super(name);
    this.sibling = sibling;
  }

  @Override
  protected <N extends Node> Iterable<? extends N> traverseAxis(
      Navigator<N> navigator, NodeView<N> view) {
    final N node = view.getNode();
    final N parent = navigator.parentOf(node);
    return null == parent
        ? Collections.<N>emptyList()
        : new FollowingSiblingIterable<N>(navigator, parent, node, sibling);
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> parent, int position) throws XmlBuilderException {
    if (isWildcard()) {
      throw new XmlBuilderException("Wildcard elements cannot be created");
    }
    final N parentNode = parent.getNode();
    final N parentParent = navigator.parentOf(parentNode);
    if (null == parentParent) {
      throw new XmlBuilderException("Can't append siblings to root");
    }
    final N element = navigator.createElement(parentParent, name);
    return new NodeView<N>(element, position);
  }

  @Override
  public String toString() {
    return (sibling ? "following-sibling::" : "following::") + super.toString();
  }

  private static final class FollowingSiblingIterable<T extends Node> implements Iterable<T> {

    private final Navigator<T> navigator;
    private final T parent;
    private final T current;
    private final boolean sibling;

    private FollowingSiblingIterable(Navigator<T> navigator, T parent, T current, boolean sibling) {
      this.navigator = navigator;
      this.parent = parent;
      this.current = current;
      this.sibling = sibling;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
      final Iterator<? extends T> it = navigator.elementsOf(parent).iterator();
      while (it.hasNext()) {
        if (current == it.next()) {
          break;
        }
      }
      return sibling
          ? (Iterator<T>) it
          : new DescendantOrSelfAxisResolver.DescendantOrSelf<T>(navigator, it);
    }
  }
}
