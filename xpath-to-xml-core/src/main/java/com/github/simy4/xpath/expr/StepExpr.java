package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.List;

/**
 * XPath step expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface StepExpr {

    /**
     * Traverses XML nodes for the nodes that matches this step expression
     *
     * @param navigator   XML model navigator
     * @param parentNodes XML nodes to traverse
     * @param <N>         XML node type
     * @return list of matching nodes
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes);

    /**
     * Creates new node of this step type
     *
     * @param navigator XML model navigator
     * @param <N>       XML node type
     * @return newly created node
     * @throws XmlBuilderException if error occur during XML node creation
     */
    <N> NodeWrapper<N> createNode(Navigator<N> navigator) throws XmlBuilderException;

}
