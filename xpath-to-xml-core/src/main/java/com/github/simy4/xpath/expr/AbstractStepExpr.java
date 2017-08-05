package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final Iterable<? extends Predicate> predicates;

    AbstractStepExpr(Iterable<? extends Predicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = traverseStep(context.getNavigator(), context.getCurrent());
        Iterator<? extends Predicate> predicateIterator = predicates.iterator();
        final CountingPredicateResolver<N> counter = new CountingPredicateResolver<N>(predicateIterator.next());
        if (predicateIterator.hasNext()) {
            result = result.flatMap(context.getNavigator(), false, counter);
            while (predicateIterator.hasNext()) {
                final Predicate predicate = predicateIterator.next();
                result = result.flatMap(context.getNavigator(), false, new PredicateResolver<N>(predicate));
            }
        }

        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = new NodeView<N>(createStepNode(context.getNavigator(), context.getCurrent()));
            predicateIterator = predicates.iterator();
            if (predicateIterator.hasNext()) {
                final int count = counter.count();
                Predicate predicate = predicateIterator.next();
                result = result.flatMap(context.getNavigator(), true, count, new PredicateResolver<N>(predicate));
                while (predicateIterator.hasNext()) {
                    predicate = predicateIterator.next();
                    result = result.flatMap(context.getNavigator(), true, new PredicateResolver<N>(predicate));
                }
            }
        }
        return result;
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param navigator  XML model navigator
     * @param parentView XML node to traverse
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N extends Node> IterableNodeView<N> traverseStep(Navigator<N> navigator, NodeView<N> parentView);

    /**
     * Creates new node of this step type.
     *
     * @param navigator  XML model navigator
     * @param parentView XML node modify
     * @param <N>        XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N extends Node> N createStepNode(Navigator<N> navigator, NodeView<N> parentView)
            throws XmlBuilderException;

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Predicate predicate : predicates) {
            stringBuilder.append('[').append(predicate).append(']');
        }
        return stringBuilder.toString();
    }

    private static class PredicateResolver<T extends Node> implements Function<ViewContext<T>, IterableNodeView<T>> {

        private final Predicate predicate;

        private PredicateResolver(Predicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public IterableNodeView<T> apply(ViewContext<T> context) {
            return predicate.match(context) ? context.getCurrent() : NodeSetView.<T>empty();
        }

    }

    @NotThreadSafe
    private static final class CountingPredicateResolver<T extends Node> extends PredicateResolver<T> {

        private int count;

        private CountingPredicateResolver(Predicate predicate) {
            super(predicate);
        }

        @Override
        public IterableNodeView<T> apply(ViewContext<T> context) {
            count += 1;
            return super.apply(context);
        }

        private int count() {
            return count;
        }

    }

}
