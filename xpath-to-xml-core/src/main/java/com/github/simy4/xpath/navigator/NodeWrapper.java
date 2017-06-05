package com.github.simy4.xpath.navigator;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;

public interface NodeWrapper<N> {

    N getWrappedNode();

    QName getNodeName();

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
