package com.github.simy4.xpath.gson.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.gson.navigator.GsonNavigator;
import com.github.simy4.xpath.gson.navigator.node.GsonRootNode;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.google.gson.JsonElement;

/**
 * Gson model navigator extension SPI.
 */
public class GsonNavigatorSpi implements NavigatorSpi {

    public GsonNavigatorSpi() {
    }

    @Override
    public boolean canHandle(Object o) {
        return o instanceof JsonElement;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T process(T json, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(json)) {
            throw new IllegalArgumentException("JSON model is not supported");
        }
        final var root = new GsonRootNode((JsonElement) json);
        final var navigator = new GsonNavigator(root);
        for (var effect : effects) {
            effect.perform(navigator, root);
        }
        return (T) root.get();
    }

}
