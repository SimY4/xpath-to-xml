package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.List;

public enum Op {

    EQ("=") {
        @Override
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            if (left.isEmpty()) {
                return right.isEmpty();
            } else {
                for (NodeWrapper<N> leftNode : left) {
                    for (NodeWrapper<N> rightNode : right) {
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
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            if (left.isEmpty()) {
                return !right.isEmpty();
            } else {
                for (NodeWrapper<N> leftNode : left) {
                    for (NodeWrapper<N> rightNode : right) {
                        if (0 != Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    },
    GT(">") {
        @Override
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            for (NodeWrapper<N> leftNode : left) {
                for (NodeWrapper<N> rightNode : right) {
                    if (0 > Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    GE(">=") {
        @Override
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            for (NodeWrapper<N> leftNode : left) {
                for (NodeWrapper<N> rightNode : right) {
                    if (0 >= Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    LT("<") {
        @Override
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            for (NodeWrapper<N> leftNode : left) {
                for (NodeWrapper<N> rightNode : right) {
                    if (0 < Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    LE("<=") {
        @Override
        <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right) {
            for (NodeWrapper<N> leftNode : left) {
                for (NodeWrapper<N> rightNode : right) {
                    if (0 <= Comparators.NODE_COMPARATOR.compare(leftNode, rightNode)) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private final String op;

    Op(String op) {
        this.op = op;
    }

    abstract <N> boolean test(List<NodeWrapper<N>> left, List<NodeWrapper<N>> right);

    @Override
    public String toString() {
        return op;
    }

}
