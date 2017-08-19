package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML elements view visitor.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface ViewVisitor<N extends Node> {

    void visit(BooleanView<N> bool) throws XmlBuilderException;

    void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException;

    void visit(LiteralView<N> literal) throws XmlBuilderException;

    void visit(NumberView<N> number) throws XmlBuilderException;

}
