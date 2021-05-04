package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

public class ChildAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  public ChildAxisResolver(QName name) {
    super(name);
  }

  @Override
  protected <N extends Node> Iterable<? extends N> traverseAxis(
      Navigator<N> navigator, NodeView<N> parent) {
    return navigator.elementsOf(parent.getNode());
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> parent, int position) throws XmlBuilderException {
    if (isWildcard()) {
      throw new XmlBuilderException("Wildcard elements cannot be created");
    }
    return new NodeView<>(navigator.createElement(parent.getNode(), name), position);
  }

  @Override
  public String toString() {
    return "child::" + super.toString();
  }
}
