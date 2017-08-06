package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

abstract class AbstractStepExpr extends AbstractExpr implements StepExpr {

    private final Iterable<Predicate<ViewContext<?>>> predicates;

    AbstractStepExpr(Iterable<Predicate<ViewContext<?>>> predicates) {
        this.predicates = predicates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = traverseStep(context.getNavigator(), context.getCurrent());
        Iterator<? extends Predicate> predicateIterator = predicates.iterator();
        final Counter counter;
        if (predicateIterator.hasNext()) {
            Predicate<ViewContext<N>> predicate = (Predicate<ViewContext<N>>) predicateIterator.next();
            final CountingPredicate<N> countingPredicate = new CountingPredicate<N>(predicate);
            counter = countingPredicate;
            result = result.filter(context.getNavigator(), false, countingPredicate);
            while (predicateIterator.hasNext()) {
                predicate = (Predicate<ViewContext<N>>) predicateIterator.next();
                result = result.filter(context.getNavigator(), false, predicate);
            }
        } else {
            counter = Counter.NO_OP;
        }

        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = new NodeView<N>(createStepNode(context.getNavigator(), context.getCurrent()));
            predicateIterator = predicates.iterator();
            if (predicateIterator.hasNext()) {
                final int count = counter.count();
                Predicate<ViewContext<N>> predicate = (Predicate<ViewContext<N>>) predicateIterator.next();
                result = result.filter(context.getNavigator(), true, count + 1, predicate);
                while (predicateIterator.hasNext()) {
                    predicate = (Predicate<ViewContext<N>>) predicateIterator.next();
                    result = result.filter(context.getNavigator(), true, predicate);
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

    private interface Counter {

        Counter NO_OP = new Counter() {
            @Override
            public int count() {
                return 0;
            }
        };

        @Nonnegative
        int count();

    }

    @NotThreadSafe
    private static final class CountingPredicate<T extends Node> implements Predicate<ViewContext<T>>, Counter {

        private final Predicate<ViewContext<T>> predicate;
        private int count;

        private CountingPredicate(Predicate<ViewContext<T>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(ViewContext<T> context) {
            count += 1;
            return predicate.test(context);
        }

        @Override
        public int count() {
            return count;
        }

    }

}
