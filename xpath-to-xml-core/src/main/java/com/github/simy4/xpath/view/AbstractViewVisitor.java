package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

public abstract class AbstractViewVisitor<N extends Node> implements ViewVisitor<N> {

    @Override
    public void visit(BooleanView<N> bool) throws XmlBuilderException {
        returnDefault(bool);
    }

    @Override
    public void visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
        returnDefault(nodeSet);
    }

    @Override
    public void visit(LiteralView<N> literal) throws XmlBuilderException {
        returnDefault(literal);
    }

    @Override
    public void visit(NumberView<N> number) throws XmlBuilderException {
        returnDefault(number);
    }

    protected abstract void returnDefault(View<N> view) throws XmlBuilderException;

}
