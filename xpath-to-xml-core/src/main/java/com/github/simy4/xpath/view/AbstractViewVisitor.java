package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

public abstract class AbstractViewVisitor<N extends Node, T> implements ViewVisitor<N, T> {

    @Override
    public T visit(BooleanView<N> bool) throws XmlBuilderException {
        return returnDefault(bool);
    }

    @Override
    public T visit(IterableNodeView<N> nodeSet) throws XmlBuilderException {
        return returnDefault(nodeSet);
    }

    @Override
    public T visit(LiteralView<N> literal) throws XmlBuilderException {
        return returnDefault(literal);
    }

    @Override
    public T visit(NumberView<N> number) throws XmlBuilderException {
        return returnDefault(number);
    }

    protected abstract T returnDefault(View<N> view) throws XmlBuilderException;

}
