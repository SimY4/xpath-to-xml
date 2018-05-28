package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.util.TransformingIterator;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;
import java.util.Iterator;

abstract class AbstractStepExpr implements StepExpr {

    private final Iterable<Expr> predicates;

    AbstractStepExpr(Iterable<Expr> predicates) {
        this.predicates = predicates;
    }

    @Override
    public final <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        IterableNodeView<N> result = resolveStep(context);
        for (Expr predicate : predicates) {
            result = resolvePredicate(context, result, predicate);
        }
        return result;
    }

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context XPath expression context
     * @param <N>        XML node type
     * @return ordered set of matching nodes
     */
    abstract <N extends Node> IterableNodeView<N> resolveStep(ViewContext<N> context) throws XmlBuilderException;

    /**
     * Creates new node of this step type.
     *
     * @param context XPath expression context
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    abstract <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException;

    final boolean isWildcard(QName name) {
        return "*".equals(name.getNamespaceURI()) || "*".equals(name.getLocalPart());
    }

    private <N extends Node> IterableNodeView<N> resolvePredicate(final ViewContext<N> context,
                                                                  final IterableNodeView<N> filtered,
                                                                  final Expr predicate) throws XmlBuilderException {
        final PredicateContext<N> predicateContext = new PredicateContext<N>();
        IterableNodeView<N> result = new NodeSetView<N>(new Iterable<NodeView<N>>() {
            @Override
            public Iterator<NodeView<N>> iterator() {
                final Iterator<NodeView<N>> iterator = filtered.iterator();
                return new FilteringIterator<NodeView<N>>(
                        new TransformingIterator<NodeView<N>, NodeView<N>>(iterator, predicateContext),
                        new PredicateResolver<N>(context, iterator, predicate));
            }
        });
        if (context.isGreedy() && !context.hasNext() && !result.toBoolean()) {
            final NodeView<N> last = predicateContext.last;
            final int position = predicateContext.position;
            final NodeView<N> newNode;
            final ViewContext<N> newContext;
            if (null != last && last.isNew()) {
                newNode = last;
                newContext = new ViewContext<N>(context.getNavigator(), last, true, false, position);
            } else {
                newNode = createStepNode(context);
                newContext = new ViewContext<N>(context.getNavigator(), newNode, true, false, position + 1);
            }
            final View<N> resolve = predicate.resolve(newContext);
            if (!resolve.toBoolean()) {
                throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
            }
            result = newNode;
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Expr predicate : predicates) {
            stringBuilder.append(predicate);
        }
        return stringBuilder.toString();
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

    private final class PredicateResolver<T extends Node> implements Predicate<NodeView<T>> {

        private final ViewContext<T> parentContext;
        private final Iterator<NodeView<T>> iterator;
        private final Expr predicate;
        private int position = 1;

        private PredicateResolver(ViewContext<T> parentContext, Iterator<NodeView<T>> iterator, Expr predicate) {
            this.parentContext = parentContext;
            this.iterator = iterator;
            this.predicate = predicate;
        }

        @Override
        public boolean test(NodeView<T> view) {
            final ViewContext<T> context = new ViewContext<T>(parentContext.getNavigator(), view, false,
                    iterator.hasNext(), position++);
            return predicate.resolve(context).toBoolean();
        }

    }

    private final class PredicateContext<T extends Node> implements Function<NodeView<T>, NodeView<T>> {

        private NodeView<T> last;
        private int position;

        @Override
        public NodeView<T> apply(NodeView<T> view) {
            if (null == last || !last.isNew()) {
                last = view;
                position++;
            }
            return view;
        }

    }

}
