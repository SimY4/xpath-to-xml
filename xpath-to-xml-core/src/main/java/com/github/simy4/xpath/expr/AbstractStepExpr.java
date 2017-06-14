package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final List<Predicate> predicateList;

    AbstractStepExpr(List<Predicate> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml)
            throws XmlBuilderException {
        context.advance();
        Set<NodeView<N>> result = traverseStep(context, xml);
        ExprContext<N> lookupContext = context.clone(false, result.size());
        Iterator<Predicate> predicateIterator = predicateList.iterator();
        result = resolvePredicates(lookupContext, result, predicateIterator);

        if (result.isEmpty() && context.shouldCreate()) {
            final NodeView<N> newNode = createStepNode(context);
            context.getNavigator().append(xml, newNode);
            result = Collections.singleton(newNode);
            predicateIterator = predicateList.iterator();
            lookupContext = lookupContext.clone(true, lookupContext.getSize() + 1, lookupContext.getSize());
            resolvePredicates(lookupContext, result, predicateIterator);
        }
        return result;
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentNode XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N> Set<NodeView<N>> traverseStep(ExprContext<N> context, NodeView<N> parentNode);

    /**
     * Creates new node of this step type.
     *
     * @param context XML model navigator
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N> NodeView<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> Set<NodeView<N>> resolvePredicates(ExprContext<N> lookupContext, Set<NodeView<N>> xmlNodes,
                                                   Iterator<Predicate> predicateIterator)
            throws XmlBuilderException {
        if (predicateIterator.hasNext() && !xmlNodes.isEmpty()) {
            final Set<NodeView<N>> result = new LinkedHashSet<NodeView<N>>(xmlNodes.size());
            final Predicate nextPredicate = predicateIterator.next();
            for (NodeView<N> xmlNode : xmlNodes) {
                lookupContext.advance();
                if (nextPredicate.apply(lookupContext, xmlNode)) {
                    result.add(xmlNode);
                }
            }
            lookupContext = lookupContext.clone(result.size());
            return resolvePredicates(lookupContext, result, predicateIterator);
        } else {
            return xmlNodes;
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Predicate predicate : predicateList) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

}
