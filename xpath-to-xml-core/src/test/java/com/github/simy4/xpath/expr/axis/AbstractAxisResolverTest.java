package com.github.simy4.xpath.expr.axis;

import org.junit.Test;

import javax.xml.namespace.QName;

public abstract class AbstractAxisResolverTest {

    protected static final QName name = new QName("namespaceURI", "localPart");

    protected AxisResolver axisResolver;

    @Test
    public void shouldReturnTarversedNodesIfAxisIsTraversable() {

    }

    protected abstract void setUpResolvableAxis();

    protected abstract void setUpUnresolvableAxis();

}
