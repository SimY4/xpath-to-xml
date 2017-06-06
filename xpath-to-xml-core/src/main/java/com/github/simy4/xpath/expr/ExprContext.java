package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ExprContext<N> {

    private final Navigator<N> navigator;
    private int size;
    private int position;

    public ExprContext(Navigator<N> navigator) {
        this.navigator = navigator;
    }

    /**
     * Constructor.
     *
     * @param navigator XML model navigator
     * @param size      XPath expression context size
     * @param position  XPath expression context position
     */
    public ExprContext(Navigator<N> navigator, int size, int position) {
        this.navigator = navigator;
        this.size = size;
        this.position = position;
    }

    public Navigator<N> getNavigator() {
        return navigator;
    }

    public int getSize() {
        assert size > 0 : "ExprContext.getSize was called before context was initialized";
        return size;
    }

    public void setSize(@Nonnegative int size) {
        this.size = size;
    }

    public int getPosition() {
        assert position > 0 : "ExprContext.getPosition was called before context was initialized";
        return position;
    }

    public boolean isLast() {
        return this.position == this.size;
    }

    public void advance() {
        this.position += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExprContext<?> that = (ExprContext<?>) o;

        if (size != that.size) {
            return false;
        }
        if (position != that.position) {
            return false;
        }
        return navigator.equals(that.navigator);
    }

    @Override
    public int hashCode() {
        int result = navigator.hashCode();
        result = 31 * result + size;
        result = 31 * result + position;
        return result;
    }

}
