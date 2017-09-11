package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.util.Triple;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class QNamePredicateTest {

    @DataPoints("QNames")
    public static final Triple[] Q_NAMES = new Triple[] {
            Triple.of(new QName("elem"), new QName("elem"), true),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "elem"), true),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("*", "elem"), true),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "*"), true),
            Triple.of(new QName("*", "elem"), new QName("*", "elem"), true),
            Triple.of(new QName("http://example.com/my", "*"),
                    new QName("http://example.com/my", "*"), true),

            Triple.of(new QName("elem"), new QName("attr"), false),
            Triple.of(new QName("http://example.com/my2", "elem"),
                    new QName("http://example.com/my1", "elem"), false),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "attr"), false),
            Triple.of(new QName("http://example.com/my", "elem"), new QName("elem"), false),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("*", "attr"), false),
            Triple.of(new QName("http://example.com/my2", "elem"),
                    new QName("http://example.com/my1", "*"), false),
            Triple.of(new QName("*", "elem"), new QName("*", "attr"), false),
            Triple.of(new QName("http://example.com/my2", "*"),
                    new QName("http://example.com/my1", "*"), false),
    };

    @Theory
    public void shouldCompareLeftAndRightQNames(@FromDataPoints("QNames") Triple<QName, QName, Boolean> qnames) {
        boolean leftToRight = new AbstractStepExpr.QNamePredicate(qnames.getFirst()).test(node(qnames.getSecond()));
        boolean rightToLeft = new AbstractStepExpr.QNamePredicate(qnames.getSecond()).test(node(qnames.getFirst()));
        assertThat(leftToRight).isEqualTo(rightToLeft).isEqualTo(qnames.getThird());
    }

}