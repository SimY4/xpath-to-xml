package com.github.simy4.xpath.expr;

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
     *
     * @param navigator
     * @param parentNodes
     * @param <N>
     * @return
     */
    <N> List<NodeWrapper<N>> traverse(Navigator<N> navigator, List<NodeWrapper<N>> parentNodes);

    /**
     *
     * @param navigator
     * @param <N>
     * @return
     */
    <N> NodeWrapper<N> createNode(Navigator<N> navigator);

}
