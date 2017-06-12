package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * XPath expression context.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@NotThreadSafe
public final class ExprContext<N> {

    private final Navigator<N> navigator;
    private final boolean greedy;
    private final int size;
    private int position;

    /**
     * Constructor.
     *
     * @param navigator XML model navigator
     * @param greedy    {@code true} if you want to evaluate expression greedily and {@code false} otherwise
     * @param size      XPath expression context size
     */
    public ExprContext(Navigator<N> navigator, boolean greedy, @Nonnegative int size) {
        this(navigator, greedy, size, 0);
    }

    private ExprContext(Navigator<N> navigator, boolean greedy, int size, int position) {
        this.navigator = navigator;
        this.greedy = greedy;
        this.size = size;
        this.position = position;
    }

    public Navigator<N> getNavigator() {
        return navigator;
    }

    public int getSize() {
        return size;
    }

    public int getPosition() {
        assert position > 0 : "ExprContext.getPosition was called before context was initialized";
        return position;
    }

    public boolean shouldCreate() {
        return this.position == this.size && greedy;
    }

    public void advance() {
        this.position += 1;
    }

    public ExprContext<N> clone(boolean greedy, @Nonnegative int size, @Nonnegative int position) {
        return new ExprContext<N>(navigator, greedy, size, position);
    }

    public ExprContext<N> clone(boolean greedy, @Nonnegative int size) {
        return new ExprContext<N>(navigator, greedy, size);
    }

    public ExprContext<N> clone(@Nonnegative int size) {
        return new ExprContext<N>(navigator, greedy, size);
    }

}
