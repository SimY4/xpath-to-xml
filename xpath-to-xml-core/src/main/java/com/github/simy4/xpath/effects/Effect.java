package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML model modification effect.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface Effect {

    /**
     * Performs effect on a particular {@link Navigator} instance.
     *
     * @param navigator XML model navigator
     * @param <N> XML model type
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> void perform(Navigator<N> navigator) throws XmlBuilderException;

}
