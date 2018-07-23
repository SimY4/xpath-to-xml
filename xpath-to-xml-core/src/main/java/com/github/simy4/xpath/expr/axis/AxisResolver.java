package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

import javax.xml.namespace.QName;

/**
 * XPath step axis resolver.
 *
 * @author Alex Simkin
 * @since 1.2
 */
public interface AxisResolver {


    /**
     * Traverses XML nodes for the nodes that matches this axis.
     *
     * @param context XPath expression context
     * @param <N>     XML node type
     * @return ordered set of matching nodes
     * @throws XmlBuilderException if error occur during XML node creation
     */
    <N extends Node> IterableNodeView<N> resolveAxis(ViewContext<N> context) throws XmlBuilderException;

    /**
     * Creates new node of this axis type.
     *
     * @param context XPath expression context
     * @param <N>     XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    <N extends Node> NodeView<N> createAxisNode(ViewContext<N> context) throws XmlBuilderException;

}
