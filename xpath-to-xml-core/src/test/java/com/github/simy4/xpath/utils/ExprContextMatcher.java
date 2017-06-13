package com.github.simy4.xpath.utils;

import com.github.simy4.xpath.expr.ExprContext;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.util.reflection.FieldReader;

import java.lang.reflect.Field;

public class ExprContextMatcher<N> implements ArgumentMatcher<ExprContext<N>> {

    private static final Field exprContextGreedyField;

    static {
        try {
            exprContextGreedyField = ExprContext.class.getDeclaredField("greedy");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Cannot access greedy field in ExprContext class", e);
        }
    }

    public static <T> ArgumentMatcher<ExprContext<T>> greedyContext() {
        return new ExprContextMatcher<T>(true);
    }

    private final boolean greedy;

    private ExprContextMatcher(boolean greedy) {
        this.greedy = greedy;
    }

    @Override
    public boolean matches(ExprContext<N> context) {
        FieldReader fieldReader = new FieldReader(context, exprContextGreedyField);
        return fieldReader.read().equals(greedy);
    }

}
