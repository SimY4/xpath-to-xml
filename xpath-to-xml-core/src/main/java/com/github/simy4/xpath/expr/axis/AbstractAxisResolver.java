package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

abstract class AbstractAxisResolver implements AxisResolver, Predicate<Node> {

    protected final QName name;

    protected AbstractAxisResolver(QName name) {
        this.name = name;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolveAxis(Navigator<N> navigator, NodeView<N> parent,
                                                                  boolean greedy) throws XmlBuilderException {
        IterableNodeView<N> result = NodeSetView.of(traverseAxis(navigator, parent), this);
        if (greedy && !result.toBoolean()) {
            result = createAxisNode(navigator, parent, 1);
        }
        return result;
    }

    protected abstract <N extends Node> Iterable<? extends N> traverseAxis(Navigator<N> navigator, NodeView<N> view);

    protected final boolean isWildcard() {
        return "*".equals(name.getNamespaceURI()) || "*".equals(name.getLocalPart());
    }

    @Override
    public boolean test(Node t) {
        final QName actual = t.getName();
        return test(name.getNamespaceURI(), actual.getNamespaceURI())
                && test(name.getLocalPart(), actual.getLocalPart());
    }

    private boolean test(String expected, String actual) {
        return "*".equals(expected) || "*".equals(actual) || expected.equals(actual);
    }

    @Override
    public String toString() {
        return name.toString();
    }

}
