package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractStepExpr implements StepExpr {

    private final Iterable<Predicate<ViewContext<?>>> predicates;

    AbstractStepExpr(Iterable<Predicate<ViewContext<?>>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = traverseStep(context.getNavigator(), context.getCurrent());
        Iterator<Predicate<ViewContext<?>>> predicateIterator = predicates.iterator();
        final Number count;
        if (predicateIterator.hasNext()) {
            Predicate<ViewContext<?>> predicate = predicateIterator.next();
            final CountingPredicate countingPredicate = new CountingPredicate(predicate);
            result = result.filter(context.getNavigator(), false, countingPredicate);
            while (predicateIterator.hasNext()) {
                result = result.filter(context.getNavigator(), false, predicateIterator.next());
            }
            count = countingPredicate.count();
        } else {
            count = 0;
        }

        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            result = new NodeView<>(createStepNode(context.getNavigator(), context.getCurrent()));
            predicateIterator = predicates.iterator();
            if (predicateIterator.hasNext()) {
                result = result.filter(context.getNavigator(), true, count.intValue() + 1, predicateIterator.next());
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

    private static final class CountingPredicate implements Predicate<ViewContext<?>> {

        private final Predicate<ViewContext<?>> predicate;
        private final AtomicInteger counter = new AtomicInteger(0);

        private CountingPredicate(Predicate<ViewContext<?>> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(ViewContext<?> context) {
            counter.incrementAndGet();
            return predicate.test(context);
        }

        private Number count() {
            return counter;
        }

    }

    static final class QNamePredicate implements Predicate<Node> {

        private final QName expected;

        QNamePredicate(QName expected) {
            this.expected = expected;
        }

        @Override
        public boolean test(Node t) {
            final QName actual = t.getName();
            return test(expected.getNamespaceURI(), actual.getNamespaceURI())
                    && test(expected.getLocalPart(), actual.getLocalPart());
        }

        private boolean test(String expected, String actual) {
            return "*".equals(expected) || "*".equals(actual) || expected.equals(actual);
        }

    }

}
