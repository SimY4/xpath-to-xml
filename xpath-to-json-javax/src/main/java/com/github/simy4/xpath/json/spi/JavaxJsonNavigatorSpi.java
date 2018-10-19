package com.github.simy4.xpath.json.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.JavaxJsonNavigator;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;

import javax.json.JsonValue;

/**
 * Javax JSON model navigator extension SPI.
 */
public class JavaxJsonNavigatorSpi implements NavigatorSpi {

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
        final JavaxJsonNode root = new JavaxJsonRootNode((JsonValue) json);
        final Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);
        for (Effect effect : effects) {
            effect.perform(navigator, root);
        }
        return (T) root.get();
    }

}
