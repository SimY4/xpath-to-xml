package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

public interface Op {

    <N> boolean test(Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right);

    <N> void apply(Navigator<N> navigator, Iterable<NodeWrapper<N>> left, Iterable<NodeWrapper<N>> right)
            throws XmlBuilderException;

}
