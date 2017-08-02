package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML elements view visitor.
 *
 * @param <N> XML node type
 * @param <T> visitor return type
 * @author Alex Simkin
 * @since 1.0
 */
public interface ViewVisitor<N extends Node, T> {

    T visit(BooleanView<N> bool) throws XmlBuilderException;

    T visit(IterableNodeView<N> nodeSet) throws XmlBuilderException;

    T visit(LiteralView<N> literal) throws XmlBuilderException;

    T visit(NumberView<N> number) throws XmlBuilderException;

}
