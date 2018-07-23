package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;

import java.util.Collections;
import java.util.Iterator;

final class DescendantOrSelf<N extends Node> implements Function<N, Iterator<N>> {

    private final Navigator<N> navigator;

    DescendantOrSelf(Navigator<N> navigator) {
        this.navigator = navigator;
    }

    @Override
    public Iterator<N> apply(N self) {
        return new FlatteningIterator<N>(Collections.singleton(self).iterator(),
                new TransformingIterator<N, Iterator<N>>(navigator.elementsOf(self).iterator(), this));
    }

}
