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
    @SuppressWarnings("unchecked")
    protected <N extends Node> Iterable<? extends N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
        final var node = view.getNode();
        final var parent = navigator.parentOf(node);
        return null == parent ? Collections.emptyList() : () -> {
            final var it = navigator.elementsOf(parent).iterator();
            while (it.hasNext()) {
                if (node == it.next()) {
                    break;
                }
            }
            return sibling ? (Iterator<N>) it : new DescendantOrSelfAxisResolver.DescendantOrSelf<>(navigator, it);
        };
    }

    @Override
    public <N extends Node> NodeView<N> createAxisNode(Navigator<N> navigator, NodeView<N> parent, int position)
            throws XmlBuilderException {
        if (isWildcard()) {
            throw new XmlBuilderException("Wildcard elements cannot be created");
        }
        final var parentNode = parent.getNode();
        final var parentParent = navigator.parentOf(parentNode);
        if (null == parentParent) {
            throw new XmlBuilderException("Can't append siblings to root");
        }
        final var element = navigator.createElement(parentParent, name);
        return new NodeView<N>(element, position);
    }

    @Override
    public String toString() {
        return (sibling ? "following-sibling::" : "following::") + super.toString();
    }
}
