package com.github.simy4.xpath.jackson.spi;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.jackson.navigator.JacksonNavigator;
import com.github.simy4.xpath.jackson.navigator.node.JacksonNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.NavigatorSpi;

/**
 * Gson model navigator extension SPI.
 */
public class JacksonNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof JsonNode;
    }

    @Override
    public <T> T process(T json, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(json)) {
            throw new IllegalArgumentException("JSON model is not supported");
        }
        final JsonNode jsonNode = (JsonNode) json;
        final JacksonNode root = new JacksonRootNode(jsonNode);
        final Navigator<JacksonNode> navigator = new JacksonNavigator(root);
        for (Effect effect : effects) {
            effect.perform(navigator, root);
        }
        return json;
    }

}
