package com.github.simy4.xpath.util;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class TransformingIteratorTest {

    private final Iterator<String> iterator = new TransformingIterator<Number, String>(asList(1, 2, 3).iterator(),
            Object::toString);

    @Test
    void shouldApplyTransformationForAllElements() {
        assertThat(iterator).toIterable().containsExactly("1", "2", "3");
    }

}