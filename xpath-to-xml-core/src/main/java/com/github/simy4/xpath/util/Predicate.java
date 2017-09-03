package com.github.simy4.xpath.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * java.util.function.Predicate exact copy.
 *
 * @param <T> predicate parameter type
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
public interface Predicate<T> extends java.util.function.Predicate<T> {

    boolean test(T t);

}
