package com.github.simy4.xpath.navigator.view;

public abstract class AbstractViewVisitor<N> implements ViewVisitor<N> {

    @Override
    public void visit(NodeSetView<N> nodeSet) { /* NO OP */ }

    @Override
    public void visit(LiteralView<N> literal) { /* NO OP */ }

    @Override
    public void visit(NumberView<N> number) { /* NO OP */ }

    @Override
    public void visit(NodeView<N> node) { /* NO OP */ }

}
