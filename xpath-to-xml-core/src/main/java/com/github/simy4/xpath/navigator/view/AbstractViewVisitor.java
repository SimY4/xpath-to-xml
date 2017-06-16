package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

public abstract class AbstractViewVisitor<N, T> implements ViewVisitor<N, T> {

    @Override
    public T visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
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

    @Override
    public T visit(NodeView<N> node) throws XmlBuilderException {
        return returnDefault(node);
    }

    protected abstract T returnDefault(View<N> view) throws XmlBuilderException;

}
