package com.github.simy4.xpath.util;

/**
 * java.util.function.Function exact copy.
 *
 * @param <T> function parameter type
 * @param <R> function return type
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface Function<T, R> extends java.util.function.Function<T, R> {

    R apply(T t);

}
