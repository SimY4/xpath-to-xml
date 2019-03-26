package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.google.gson.JsonElement;

/**
 * Gson node contract.
 *
 * @author Alex Simkin
 * @since 1.1
 */
public interface GsonNode extends Node {

    GsonNode getParent();

    void setParent(GsonNode parent);

    JsonElement get();

    void set(JsonElement jsonElement) throws XmlBuilderException;

    void remove() throws XmlBuilderException;

    Iterable<? extends GsonNode> elements();

    Iterable<? extends GsonNode> attributes();

}
