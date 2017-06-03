package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MetaStepExpr implements StepExpr {

    private final StepExpr stepExpr;
    private final List<Expr> predicates;

    public MetaStepExpr(StepExpr stepExpr, List<Expr> predicates) {
        this.stepExpr = stepExpr;
        this.predicates = predicates;
    }

    @Override
    public <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes) {
        final List<NodeWrapper<N>> nodes = stepExpr.traverse(navigator, parentNodes);
        final List<NodeWrapper<N>> result = new ArrayList<NodeWrapper<N>>(nodes.size());
        for (NodeWrapper<N> node : nodes) {
            final Iterator<Expr> predicatesIterator = predicates.iterator();
            if (test(navigator, node, predicatesIterator)) {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public <N> NodeWrapper<N> createNode(Navigator<N> navigator) {
        final NodeWrapper<N> newNode = stepExpr.createNode(navigator);
        for (Expr predicate : predicates) {
            predicate.apply(navigator, newNode, true);
        }
        return newNode;
    }

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
        final StringBuilder stringBuilder = new StringBuilder(stepExpr.toString()).append("[");
        final Iterator<Expr> predicatesIterator = predicates.iterator();
        if (predicatesIterator.hasNext()) {
            stringBuilder.append(predicatesIterator.next());
            while (predicatesIterator.hasNext()) {
                stringBuilder.append("][").append(predicatesIterator.next());
            }
        }
        return stringBuilder.append("]").toString();
    }

}
