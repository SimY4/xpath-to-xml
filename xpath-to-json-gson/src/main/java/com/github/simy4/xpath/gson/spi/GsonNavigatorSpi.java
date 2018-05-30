package com.github.simy4.xpath.gson.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.effects.Effect;
import com.github.simy4.xpath.gson.navigator.GsonNavigator;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.gson.navigator.node.GsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.google.gson.JsonElement;

/**
 * Gson model navigator extension SPI.
 */
public class GsonNavigatorSpi implements NavigatorSpi {

    @Override
    public boolean canHandle(Object o) {
        return o instanceof JsonElement;
    }

    @Override
    public <T> T process(T json, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(json)) {
            throw new IllegalArgumentException("JSON model is not supported");
        }
        final JsonElement jsonNode = (JsonElement) json;
        final GsonNode root = new GsonRootNode(jsonNode);
        final Navigator<GsonNode> navigator = new GsonNavigator(root);
        for (Effect effect : effects) {
            effect.perform(navigator, root);
        }
        return json;
    }

}
