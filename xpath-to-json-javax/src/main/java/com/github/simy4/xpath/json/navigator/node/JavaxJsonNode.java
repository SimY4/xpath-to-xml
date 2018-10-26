package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.json.JsonValue;

/**
 * Javax JSON node contract.
 *
 * @author Alex Simkin
 * @since 2.0
 */
public interface JavaxJsonNode extends Node, Iterable<JavaxJsonNode> {

    JavaxJsonNode getParent();

    void setParent(JavaxJsonNode parent);

    JsonValue get();

    void set(JsonValue jsonValue) throws XmlBuilderException;

    void remove() throws XmlBuilderException;

}
