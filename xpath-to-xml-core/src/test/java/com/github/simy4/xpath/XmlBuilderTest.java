package com.github.simy4.xpath;

import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
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

    @Test
    @DisplayName("Should serialize it and deserialize it back")
    void shouldSerializeAndDeserializeBuilder() throws IOException, ClassNotFoundException, XPathExpressionException {
        // given
        var builder = new XmlBuilder(new SimpleNamespaceContext())
                .put("test")
                .putAll("test", "test", "test")
                .putAll(asList("test", "test", "test"))
                .put("test", 123)
                .putAll(singletonMap("test", 123))
                .remove("test")
                .removeAll("test", "test", "test")
                .removeAll(asList("test", "test", "test"));

        // when
        var deserializedBuild = SerializationHelper.serializeAndDeserializeBack(builder);

        // then
        assertThat(deserializedBuild).extracting("effects").flatExtracting(o -> ((Collection<?>) o))
                .hasSize(16);
    }

}