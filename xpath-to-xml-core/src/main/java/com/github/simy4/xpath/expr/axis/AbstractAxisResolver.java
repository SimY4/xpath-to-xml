package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

abstract class AbstractAxisResolver implements AxisResolver, Predicate<Node> {

    final QName name;

    AbstractAxisResolver(QName name) {
        this.name = name;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolveAxis(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = traverseAxis(context);
        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = createAxisNode(context);
        }
        return result;
    }

    abstract <N extends Node> IterableNodeView<N> traverseAxis(ViewContext<N> context);

    final boolean isWildcard() {
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
