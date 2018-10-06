package com.github.simy4.xpath.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML model modification effect.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Effect {

    /**
     * Performs effect on a particular xml model.
     *
     * @param xml       XML model to modify
     * @param navigator XML model navigator
     * @param <N>       XML model type
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException;

}
