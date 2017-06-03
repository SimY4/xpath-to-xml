package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;

/**
 * Recorded XML model modification action.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Action {

    /**
     * Performs action against particular XML model {@link Navigator} instance.
     *
     * @param navigator XML model navigator
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <T> void perform(Navigator<T> navigator) throws XmlBuilderException;

}
