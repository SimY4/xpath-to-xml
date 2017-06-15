package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;
import com.github.simy4.xpath.utils.Triple;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static com.github.simy4.xpath.navigator.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class EqTest {

    @DataPoints("Nodes")
    public static final Triple[] NODES = new Triple[] {
            Triple.of(new LiteralView("literal"), new LiteralView("literal"), true),
            Triple.of(new LiteralView("2.0"), new NumberView(2.0), true),
            Triple.of(new NumberView(2.0), new NumberView(2.0), true),
            Triple.of(new LiteralView("literal"), singleton(new LiteralView("literal")), true),
            Triple.of(new LiteralView("2.0"), singleton(new NumberView(2.0)), true),
            Triple.of(new LiteralView("2.0"), singleton(new NumberView(2.0)), true),

            Triple.of(new LiteralView("text2"), new LiteralView("text1"), false),
            Triple.of(new LiteralView("2.0"), new NumberView(1.0), false),
            Triple.of(new LiteralView("text"), new NumberView(1.0), false),
            Triple.of(new LiteralView("text2"), singleton(new LiteralView("text1")), false),
            Triple.of(new LiteralView("2.0"), singleton(new NumberView(1.0)), false),
            Triple.of(new LiteralView("text2"), NodeSetView.empty(), false),
            Triple.of(new LiteralView("2.0"), NodeSetView.empty(), false),
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