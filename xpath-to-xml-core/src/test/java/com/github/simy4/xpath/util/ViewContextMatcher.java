package com.github.simy4.xpath.util;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.ViewContext;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.util.reflection.FieldReader;

import java.lang.reflect.Field;

public class ViewContextMatcher<N extends Node> implements ArgumentMatcher<ViewContext<N>> {

    private static final Field viewContextGreedyField;

    static {
        try {
            viewContextGreedyField = ViewContext.class.getDeclaredField("greedy");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Cannot access greedy field in ViewContext class", e);
        }
    }

    public static <T extends Node> ArgumentMatcher<ViewContext<T>> greedyContext() {
        return new ViewContextMatcher<T>(true);
    }

    private final boolean greedy;

    private ViewContextMatcher(boolean greedy) {
        this.greedy = greedy;
    }

    @Override
    public boolean matches(ViewContext<N> context) {
        FieldReader fieldReader = new FieldReader(context, viewContextGreedyField);
        return fieldReader.read().equals(greedy);
    }

}
