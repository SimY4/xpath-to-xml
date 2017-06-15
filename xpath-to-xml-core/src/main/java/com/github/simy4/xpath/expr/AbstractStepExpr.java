package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.AbstractViewVisitor;
import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;
import com.github.simy4.xpath.navigator.view.ViewVisitor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;
import java.util.List;

abstract class AbstractStepExpr implements StepExpr {

    private final List<Expr> predicateList;

    AbstractStepExpr(List<Expr> predicateList) {
        this.predicateList = predicateList;
    }

    @Override
    public final <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        final StepNodeVisitor<N> stepNodeVisitor = new StepNodeVisitor<N>(context);
        xml.visit(stepNodeVisitor);
        return stepNodeVisitor.getNodeSetView();
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentNode XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> parentNode);

    /**
     * Creates new node of this step type.
     *
     * @param context XML model navigator
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N> NodeView<N> createStepNode(ExprContext<N> context) throws XmlBuilderException;

    private <N> NodeSetView<N> resolvePredicates(ExprContext<N> lookupContext, NodeSetView<N> xmlNodes,
                                                 Iterator<Expr> predicateIterator) throws XmlBuilderException {
        if (predicateIterator.hasNext() && !xmlNodes.isEmpty()) {
            final NodeSetView.Builder<N> builder = NodeSetView.builder(xmlNodes.size());
            final Expr nextPredicate = predicateIterator.next();
            for (View<N> xmlNode : xmlNodes) {
                lookupContext.advance();
                final View<N> lookup = nextPredicate.resolve(lookupContext, xmlNode);
                final PredicateVisitor<N> predicateVisitor = new PredicateVisitor<N>();
                lookup.visit(predicateVisitor);
                if (predicateVisitor.isMatch()) {
                    builder.add(xmlNode);
                }
            }
            final NodeSetView<N> result = builder.build();
            lookupContext = lookupContext.clone(result.size());
            return resolvePredicates(lookupContext, result, predicateIterator);
        } else {
            return xmlNodes;
        }
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Expr predicate : predicateList) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

    @NotThreadSafe
    private final class StepNodeVisitor<N> extends AbstractViewVisitor<N> {

        private final ExprContext<N> context;
        private final NodeSetView.Builder<N> builder = NodeSetView.builder();

        private StepNodeVisitor(ExprContext<N> context) {
            this.context = context;
        }

        @Override
        public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            for (View<N> node : nodeSet) {
                node.visit(this);
            }
        }

        @Override
        public void visit(NodeView<N> node) {
            context.advance();
            NodeSetView<N> result = traverseStep(context, node);
            ExprContext<N> lookupContext = context.clone(false, result.size());
            Iterator<Expr> predicateIterator = predicateList.iterator();
            result = resolvePredicates(lookupContext, result, predicateIterator);

            if (result.isEmpty() && context.shouldCreate()) {
                final NodeView<N> newNode = createStepNode(context);
                context.getNavigator().append(node, newNode);
                result = NodeSetView.singleton(newNode);
                predicateIterator = predicateList.iterator();
                lookupContext = lookupContext.clone(true, lookupContext.getSize() + 1, lookupContext.getSize());
                resolvePredicates(lookupContext, result, predicateIterator);
            }
            builder.add(result);
        }

        private NodeSetView<N> getNodeSetView() {
            return builder.build();
        }

    }

    @NotThreadSafe
    private static final class PredicateVisitor<N> implements ViewVisitor<N> {

        private boolean match;

        @Override
        public void visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
            match = !nodeSet.isEmpty();
        }

        @Override
        public void visit(LiteralView<N> literal) {
            match = !literal.getLiteral().isEmpty();
        }

        @Override
        public void visit(NumberView<N> number) {
            match = 0 != Double.compare(0.0, number.getNumber().doubleValue());
        }

        @Override
        public void visit(NodeView<N> node) {
            match = true;
        }

        private boolean isMatch() {
            return match;
        }

    }

}
