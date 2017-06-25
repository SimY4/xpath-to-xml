package com.github.simy4.xpath.navigator;

import javax.xml.namespace.QName;

/**
 * XML node contract.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Node {

    /**
     * XML node name.
     *
     * @return node name.
     */
    QName getName();

    /**
     * XML node text content.
     *
     * @return text content.
     */
    String getText();

}
