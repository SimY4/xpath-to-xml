package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

/**
 * Javax JSON node contract.
 *
 * @author Alex Simkin
 * @since 1.3
 */
public interface JavaxJsonNode extends Node, Iterable<JavaxJsonNode> {

    JavaxJsonNode getParent();

    void setParent(JavaxJsonNode parent);

    JsonValue get();

    void set(JsonProvider jsonProvider, JsonValue jsonValue) throws XmlBuilderException;

    void remove(JsonProvider jsonProvider) throws XmlBuilderException;

}
