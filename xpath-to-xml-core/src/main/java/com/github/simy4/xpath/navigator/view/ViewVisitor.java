package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

public interface ViewVisitor<N> {

    void visit(NodeSetView<N> nodeSet) throws XmlBuilderException;

    void visit(LiteralView<N> literal) throws XmlBuilderException;

    void visit(NumberView<N> number) throws XmlBuilderException;

    void visit(NodeView<N> node) throws XmlBuilderException;

}
