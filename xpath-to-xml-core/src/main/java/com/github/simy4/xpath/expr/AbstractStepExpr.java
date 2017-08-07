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
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = traverseStep(context.getNavigator(), context.getCurrent());
        Iterator<Predicate<ViewContext<?>>> predicateIterator = predicates.iterator();
        final Counter counter;
        if (predicateIterator.hasNext()) {
            Predicate<ViewContext<?>> predicate = predicateIterator.next();
            final CountingPredicate countingPredicate = new CountingPredicate(predicate);
            counter = countingPredicate;
            result = result.filter(context.getNavigator(), false, countingPredicate);
            while (predicateIterator.hasNext()) {
                result = result.filter(context.getNavigator(), false, predicateIterator.next());
            }
        } else {
            counter = Counter.NO_OP;
        }

        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = new NodeView<N>(createStepNode(context.getNavigator(), context.getCurrent()));
            predicateIterator = predicates.iterator();
            if (predicateIterator.hasNext()) {
                final int count = counter.count();
                result = result.filter(context.getNavigator(), true, count + 1, predicateIterator.next());
                while (predicateIterator.hasNext()) {
                    result = result.filter(context.getNavigator(), true, predicateIterator.next());
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
    private static final class CountingPredicate implements Predicate<ViewContext<?>>, Counter {

        private final Predicate<ViewContext<?>> predicate;
        private int count;

        private CountingPredicate(Predicate<ViewContext<?>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(ViewContext<?> context) {
            count += 1;
            return predicate.test(context);
        }

        @Override
        public int count() {
            return count;
        }

    }

}
