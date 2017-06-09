package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.navigator.NodeWrapper;
import com.github.simy4.xpath.utils.Triple;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class EqTest {

    @DataPoints("Nodes")
    public static final Triple[] NODES = new Triple[] {
            Triple.of(singleton(node("text")), asList(node("text"), node("another-text")), true),
            Triple.of(singleton(node("")), asList(node("text"), node("")), true),
            Triple.of(singleton(node(null)), singleton(node(null)), true),

            Triple.of(singleton(node("text2")), singleton(node("text1")), false),
            Triple.of(singleton(node("text")), singleton(node("")), false),
            Triple.of(singleton(node("text")), singleton(node(null)), false),
    };

    @Theory
    public void shouldTestLeftAndRightNodes(@FromDataPoints("Nodes") Triple<Iterable<NodeWrapper<String>>,
            Iterable<NodeWrapper<String>>, Boolean> nodes) {
        boolean result = new Eq().test(nodes.getFirst(), nodes.getSecond());
        assertThat(result).isEqualTo(nodes.getThird());

        result = new Eq().test(nodes.getSecond(), nodes.getFirst());
        assertThat(result).isEqualTo(nodes.getThird());
    }

    @Test
    public void testToString() {
        assertThat(new Eq()).hasToString("=");
    }

}