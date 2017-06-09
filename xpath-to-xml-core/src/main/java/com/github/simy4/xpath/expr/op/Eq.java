package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Comparator;
import java.util.Iterator;

public class Eq implements Op, Comparator<NodeWrapper<?>> {

    @Override
    public <N> boolean test(Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right) {
        final Iterator<NodeWrapper<N>> leftIterator = left.iterator();
        final Iterator<NodeWrapper<N>> rightIterator = right.iterator();
        while (leftIterator.hasNext()) {
            final NodeWrapper<N> leftNode = leftIterator.next();
            while (rightIterator.hasNext()) {
                final NodeWrapper<N> rightNode = rightIterator.next();
                if (0 == compare(leftNode, rightNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <N> void apply(Navigator<N> navigator, Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right)
            throws XmlBuilderException {
        NodeWrapper<N> rightNode = right.iterator().next();
        for (NodeWrapper<N> leftNode : left) {
            navigator.setText(leftNode, rightNode.getText());
        }
    }

    @Override
    public int compare(NodeWrapper<?> left, NodeWrapper<?> right) {
        final String leftText = left.getText();
        final String rightText = right.getText();
        if (null == leftText) {
            return null == rightText ? 0 : -1;
        } else if (null == rightText) {
            return 1;
        } else {
            return leftText.compareTo(rightText);
        }
    }

    @Override
    public String toString() {
        return "=";
    }

}
