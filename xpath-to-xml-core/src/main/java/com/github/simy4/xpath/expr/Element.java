package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

public class Element extends AbstractStepExpr {

    private final QName element;
    private final Predicate<Node> filter;

    /**
     * Constructor.
     *
     * @param element    element name
     * @param predicates element predicates
     */
    public Element(QName element, Iterable<Expr> predicates) {
        super(predicates);
        this.element = element;
        this.filter = new QNamePredicate(element);
    }

    @Override
    <N extends Node> IterableNodeView<N> resolveStep(ViewContext<N> context) throws XmlBuilderException {
        final Navigator<N> navigator = context.getNavigator();
        final N parentNode = context.getCurrent().getNode();
        IterableNodeView<N> result = NodeSetView.filtered(navigator.elementsOf(parentNode), filter);
        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = createStepNode(context);
        }
        return result;
    }

    @Override
    <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        if (isWildcard(element)) {
            throw new XmlBuilderException("Wildcard attribute cannot be created");
        }
        return new NodeView<N>(context.getNavigator().createElement(context.getCurrent().getNode(), element), true);
    }

    @Override
    public String toString() {
        return element.toString() + super.toString();
    }

}
