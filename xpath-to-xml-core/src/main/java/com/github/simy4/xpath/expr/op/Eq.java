package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Comparator;

public class Eq implements Op, Comparator<NodeView<?>> {

    @Override
    public <N> boolean test(Iterable<NodeView<N>> left, Iterable<NodeView<N>> right) {
        for (NodeView<N> leftNode : left) {
            for (NodeView<N> rightNode : right) {
                if (0 == compare(leftNode, rightNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <N> void apply(Navigator<N> navigator, Iterable<NodeView<N>> left, Iterable<NodeView<N>> right)
            throws XmlBuilderException {
        NodeView<N> rightNode = right.iterator().next();
        for (NodeView<N> leftNode : left) {
            navigator.setText(leftNode, rightNode.getText());
        }
    }

    @Override
    public int compare(NodeView<?> left, NodeView<?> right) {
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
