package com.github.simy4.xpath.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * java.util.function.Function exact copy.
 *
 * @param <T> function parameter type
 * @param <R> function return type
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface Function<T, R> extends java.util.function.Function<T, R> {

    R apply(T t);

}
