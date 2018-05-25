package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

/**
 * XPath step expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface StepExpr extends Expr {

    @Override
    <N extends Node> IterableNodeView<N> resolve(ViewContext<N> context) throws XmlBuilderException;

    /**
     * Creates new node of this step type.
     *
     * @param context XPath expression context
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException;

}
