package com.github.simy4.xpath.view;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ViewContext<N extends Node> {

    private final Navigator<N> navigator;
    private final NodeView<N> current;
    private final boolean greedy;
    private final boolean hasNext;
    private final int position;

    public ViewContext(Navigator<N> navigator, NodeView<N> current, boolean greedy) {
        this(navigator, current, greedy, false, 1);
    }

    ViewContext(Navigator<N> navigator, NodeView<N> current, boolean greedy, boolean hasNext, int position) {
        this.navigator = navigator;
        this.current = current;
        this.greedy = greedy;
        this.hasNext = hasNext;
        this.position = position;
    }

    public Navigator<N> getNavigator() {
        return navigator;
    }

    public NodeView<N> getCurrent() {
        return current;
    }

    public boolean isGreedy() {
        return greedy;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int getPosition() {
        return position;
    }

}
