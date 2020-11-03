package com.github.simy4.xpath.json.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.JakartaJsonNavigator;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonRootNode;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

/**
 * Javax JSON model navigator extension SPI.
 */
public class JakartaJsonNavigatorSpi implements NavigatorSpi {

    private final JsonProvider jsonProvider = JsonProvider.provider();

    @Override
    public boolean canHandle(Object o) {
        return o instanceof JsonValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T process(T json, Iterable<Effect> effects) throws XmlBuilderException {
        if (!canHandle(json)) {
            throw new IllegalArgumentException("JSON model is not supported");
        }
        final var root = new JakartaJsonRootNode((JsonValue) json);
        final var navigator = new JakartaJsonNavigator(jsonProvider, root);
        for (var effect : effects) {
            effect.perform(navigator, root);
        }
        return (T) root.get();
    }

}
