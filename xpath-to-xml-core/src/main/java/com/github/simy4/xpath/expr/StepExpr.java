package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.View;

import javax.xml.namespace.QName;
import java.util.Comparator;

/**
 * XPath step expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface StepExpr extends Expr {

    /**
     * QName comparator aware of wildcards.
     */
    Comparator<QName> qnameComparator = new Comparator<QName>() {
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

    @Override
    <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException;

}
