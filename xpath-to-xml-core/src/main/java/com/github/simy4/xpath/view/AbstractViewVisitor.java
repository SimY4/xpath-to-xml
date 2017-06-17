package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;

public abstract class AbstractViewVisitor<N, T> implements ViewVisitor<N, T> {

    @Override
    public T visit(BooleanView<N> bool) throws XmlBuilderException {
        return returnDefault(bool);
    }

    @Override
    public T visit(LiteralView<N> literal) throws XmlBuilderException {
        return returnDefault(literal);
    }

    @Override
    public T visit(NodeSetView<N> nodeSet) throws XmlBuilderException {
        return returnDefault(nodeSet);
    }

    @Override
    public T visit(NodeView<N> node) throws XmlBuilderException {
        return returnDefault(node);
    }

    @Override
    public T visit(NumberView<N> number) throws XmlBuilderException {
        return returnDefault(number);
    }

    protected abstract T returnDefault(View<N> view) throws XmlBuilderException;

}
