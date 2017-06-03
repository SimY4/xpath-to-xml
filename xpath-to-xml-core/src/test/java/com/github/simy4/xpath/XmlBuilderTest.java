package com.github.simy4.xpath;

import org.junit.Test;

public class XmlBuilderTest {

    private final XmlBuilder xmlBuilder = new XmlBuilder();

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnBuildWithoutConcreteSpiImplementation() {
        xmlBuilder.build(new Object());
    }

}