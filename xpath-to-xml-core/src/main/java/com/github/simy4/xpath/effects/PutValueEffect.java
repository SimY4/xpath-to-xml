package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;

public class PutValueEffect implements Effect, Serializable {

  private static final long serialVersionUID = 1L;

  private final Expr expr;
  private final String value;

  public PutValueEffect(Expr expr, Object value) {
    this.expr = expr;
    this.value = String.valueOf(value);
  }

  @Override
  public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
    expr.resolve(navigator, new NodeView<>(xml), true)
        .visit(new PutValueVisitor<>(navigator, value));
  }

  private static final class PutValueVisitor<N extends Node> extends AbstractViewVisitor<N, Void> {

    private final Navigator<N> navigator;
    private final String value;

    private PutValueVisitor(Navigator<N> navigator, String value) {
      this.navigator = navigator;
      this.value = value;
    }

    @Override
    public Void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
      for (NodeView<N> node : nodeSet) {
        navigator.setText(node.getNode(), value);
      }
      return null;
    }

    @Override
    protected Void returnDefault(View<N> view) throws XmlBuilderException {
      throw new XmlBuilderException(
          "Failed to put value into XML. Read-only view was resolved: " + view);
    }
  }
}
