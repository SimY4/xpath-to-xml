package com.github.simy4.xpath.expr;

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

    private Comparators() { }

}
