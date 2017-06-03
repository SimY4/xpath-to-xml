package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;
import com.github.simy4.xpath.utils.Triple;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class ComparatorsTest {

    @DataPoints("QNames")
    public static final Triple[] Q_NAMES = new Triple[] {
            Triple.of(new QName("elem"), new QName("elem"), 0),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "elem"), 0),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("*", "elem"), 0),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "*"), 0),
            Triple.of(new QName("*", "elem"), new QName("*", "elem"), 0),
            Triple.of(new QName("http://example.com/my", "*"),
                    new QName("http://example.com/my", "*"), 0),

            Triple.of(new QName("elem"), new QName("attr"), 4),
            Triple.of(new QName("http://example.com/my2", "elem"),
                    new QName("http://example.com/my1", "elem"), 1),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("http://example.com/my", "attr"), 4),
            Triple.of(new QName("http://example.com/my", "elem"), new QName("elem"), 21),
            Triple.of(new QName("http://example.com/my", "elem"),
                    new QName("*", "attr"), 4),
            Triple.of(new QName("http://example.com/my2", "elem"),
                    new QName("http://example.com/my1", "*"), 1),
            Triple.of(new QName("*", "elem"), new QName("*", "attr"), 4),
            Triple.of(new QName("http://example.com/my2", "*"),
                    new QName("http://example.com/my1", "*"), 1),
    };

    @DataPoints("Nodes")
    public static final Triple[] NODES = new Triple[] {
            Triple.of(node("text"), node("text"), 0),
            Triple.of(node(""), node(""), 0),
            Triple.of(node(null), node(null), 0),

            Triple.of(node("text2"), node("text1"), 1),
            Triple.of(node("text"), node(""), 4),
            Triple.of(node("text"), node(null), 1),
    };

    @Theory
    public void shouldCompareLeftAndRightQNames(@FromDataPoints("QNames") Triple<QName, QName, Integer> qnames) {
        int result = Comparators.QNAME_COMPARATOR.compare(qnames.getFirst(), qnames.getSecond());
        assertThat(result).isEqualTo(qnames.getThird());
    }

    @Theory
    public void shouldCompareRightAndLeftQNames(@FromDataPoints("QNames") Triple<QName, QName, Integer> qnames) {
        int result = Comparators.QNAME_COMPARATOR.compare(qnames.getSecond(), qnames.getFirst());
        assertThat(result).isEqualTo(-qnames.getThird());
    }

    @Theory
    public void shouldCompareLeftAndRightNodes(
            @FromDataPoints("Nodes") Triple<NodeWrapper<String>, NodeWrapper<String>, Integer> nodes) {
        int result = Comparators.NODE_COMPARATOR.compare(nodes.getFirst(), nodes.getSecond());
        assertThat(result).isEqualTo(nodes.getThird());
    }

    @Theory
    public void shouldCompareRightAndLeftNodes(
            @FromDataPoints("Nodes") Triple<NodeWrapper<String>, NodeWrapper<String>, Integer> nodes) {
        int result = Comparators.NODE_COMPARATOR.compare(nodes.getSecond(), nodes.getFirst());
        assertThat(result).isEqualTo(-nodes.getThird());
    }

}