package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Set;

/**
 * XPath step expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface StepExpr extends Expr {

    /**
     * Traverses XML nodes for the nodes that matches this step expression.
     *
     * @param context    XPath expression context
     * @param parentNode XML node to traverse
     * @param <N>        XML node type
     * @return list of matching nodes
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> Set<NodeWrapper<N>> traverse(ExprContext<N> context, NodeWrapper<N> parentNode);

    /**
     * Creates new node of this step type.
     *
     * @param context XML model navigator
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    <N> NodeWrapper<N> createNode(ExprContext<N> context) throws XmlBuilderException;

}
