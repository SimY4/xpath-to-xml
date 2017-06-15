package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.navigator.view.View;
import com.github.simy4.xpath.utils.Triple;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class EqTest {

    @DataPoints("Nodes")
    public static final Triple[] NODES = new Triple[] {
            Triple.of(singleton(node("text")), asList(node("text"), node("another-text")), true),
            Triple.of(node(""), asList(node("text"), node("")), true),
            Triple.of(node(null), node(null), true),

            Triple.of(node("text2"), node("text1"), false),
            Triple.of(node("text"), node(""), false),
            Triple.of(node("text"), node(null), false),
    };

    @Theory
    public void shouldTestLeftAndRightNodes(@FromDataPoints("Nodes") Triple<View<String>, View<String>, Boolean> data) {
        boolean result = new Eq().test(data.getFirst(), data.getSecond());
        assertThat(result).isEqualTo(data.getThird());

        result = new Eq().test(data.getSecond(), data.getFirst());
        assertThat(result).isEqualTo(data.getThird());
    }

    @Test
    public void testToString() {
        assertThat(new Eq()).hasToString("=");
    }

}