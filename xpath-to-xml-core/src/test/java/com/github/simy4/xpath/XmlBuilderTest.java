package com.github.simy4.xpath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XmlBuilderTest {

    private final XmlBuilder xmlBuilder = new XmlBuilder();

    @Test
    @DisplayName("When no concrete SPI implementation should return false on can handle request")
    void shouldReturnFalseWithoutConcreteSpiImplementation() {
        // when
        assertThat(XmlBuilder.canHandle(new Object())).isFalse();
    }

    @Test
    @DisplayName("When no concrete SPI implementation should throw XmlBuilderException")
    void shouldThrowOnBuildWithoutConcreteSpiImplementation() {
        // when
        assertThatThrownBy(() -> xmlBuilder.build(new Object())).isInstanceOf(XmlBuilderException.class);
    }

}