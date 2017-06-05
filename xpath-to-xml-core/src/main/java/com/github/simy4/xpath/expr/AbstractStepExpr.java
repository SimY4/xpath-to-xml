package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class AbstractStepExpr implements StepExpr {

    private final List<Expr> predicateList;

    AbstractStepExpr(List<Expr> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        final List<NodeWrapper<N>> nodes = traverseStep(navigator, parentNodes);
        final List<NodeWrapper<N>> result = new ArrayList<NodeWrapper<N>>(nodes.size());
        for (NodeWrapper<N> node : nodes) {
            final Iterator<Expr> predicatesIterator = predicateList.iterator();
            if (test(navigator, node, predicatesIterator)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public final <N> NodeWrapper<N> createNode(Navigator<N> navigator) throws XmlBuilderException {
        final NodeWrapper<N> newNode = createStepNode(navigator);
        for (Expr predicate : predicateList) {
            predicate.apply(navigator, newNode, true);
        }
        return newNode;
    }

    abstract <N> List<NodeWrapper<N>> traverseStep(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes);

    abstract <N> NodeWrapper<N> createStepNode(Navigator<N> navigator) throws XmlBuilderException;

    private <N> boolean test(Navigator<N> navigator, NodeWrapper<N> node, Iterator<Expr> predicateIterator) {
        if (predicateIterator.hasNext()) {
            final Expr predicate = predicateIterator.next();
            final List<NodeWrapper<N>> children = predicate.apply(navigator, node, false);
            return !children.isEmpty() && test(navigator, node, predicateIterator);
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("[");
        final Iterator<Expr> predicatesIterator = predicateList.iterator();
        if (predicatesIterator.hasNext()) {
            stringBuilder.append(predicatesIterator.next());
            while (predicatesIterator.hasNext()) {
                stringBuilder.append("][").append(predicatesIterator.next());
            }
        }
        return stringBuilder.append(']').toString();
    }

}
