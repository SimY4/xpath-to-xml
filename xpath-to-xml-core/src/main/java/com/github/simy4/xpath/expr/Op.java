package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Iterator;

public enum Op {

    EQ("=") {
        @Override
        <N> boolean test(Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right) {
            final Iterator<NodeWrapper<N>> leftIterator = left.iterator();
            final Iterator<NodeWrapper<N>> rightIterator = right.iterator();
            if (!leftIterator.hasNext()) {
                return !leftIterator.hasNext();
            } else {
                while (leftIterator.hasNext()) {
                    final NodeWrapper<N> leftNode = leftIterator.next();
                    while (rightIterator.hasNext()) {
                        final NodeWrapper<N> rightNode = rightIterator.next();
                        if (0 == Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    },
    NE("!=") {
        @Override
        <N> boolean test(Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right) {
            return !EQ.test(left, right);
        }
    };

    private final String op;

    Op(String op) {
        this.op = op;
    }

    abstract <N> boolean test(Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right);

    @Override
    public String toString() {
        return op;
    }

}
