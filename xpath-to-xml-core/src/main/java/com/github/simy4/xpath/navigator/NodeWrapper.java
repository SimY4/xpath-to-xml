package com.github.simy4.xpath.navigator;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

/**
 * XML node contract.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface NodeWrapper<N> {

    /**
     * Original node.
     *
     * @return node.
     */
    N getWrappedNode();

    /**
     * XML node name.
     *
     * @return node name.
     */
    QName getNodeName();

    /**
     * XML node text content.
     *
     * @return text content.
     */
    String getText();

    @Immutable
    final class LiteralNodeWrapper<N> implements NodeWrapper<N> {

        private final String literal;

        public LiteralNodeWrapper(String literal) {
            this.literal = literal;
        }

        @Override
        public N getWrappedNode() {
            throw new UnsupportedOperationException("getWrappedNode");
        }

        @Override
        public QName getNodeName() {
            return XPathConstants.STRING;
        }

        @Override
        public String getText() {
            return literal;
        }

    }

    @Immutable
    final class NumberNodeWrapper<N> implements NodeWrapper<N> {

        private final Number number;

        public NumberNodeWrapper(Number number) {
            this.number = number;
        }

        @Override
        public N getWrappedNode() {
            throw new UnsupportedOperationException("getWrappedNode");
        }

        @Override
        public QName getNodeName() {
            return XPathConstants.NUMBER;
        }

        @Override
        public String getText() {
            return String.valueOf(number);
        }

    }

}
