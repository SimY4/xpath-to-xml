package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import javax.xml.namespace.QName;
import java.util.Comparator;

final class Comparators {

    static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>() {
        @Override
        public int compare(QName left, QName right) {
            int result = compare(left.getNamespaceURI(), right.getNamespaceURI());
            if (0 == result) {
                result = compare(left.getLocalPart(), right.getLocalPart());
            }
            return result;
        }

        private int compare(String left, String right) {
            if ("*".equals(left) || "*".equals(right)) {
                return 0;
            } else {
                return left.compareTo(right);
            }
        }
    };

    static final Comparator<NodeWrapper<?>> NODE_COMPARATOR = new Comparator<NodeWrapper<?>>() {
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
    };

    private Comparators() { }

}
