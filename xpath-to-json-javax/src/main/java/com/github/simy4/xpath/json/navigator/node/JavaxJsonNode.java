package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import java.util.stream.Stream;

/**
 * Javax JSON node contract.
 *
 * @author Alex Simkin
 * @since 2.0
 */
public interface JavaxJsonNode extends Node {

    JavaxJsonNode getParent();

    void setParent(JavaxJsonNode parent);

    JsonValue get();

    void set(JsonProvider jsonProvider, JsonValue jsonValue) throws XmlBuilderException;

    Stream<JavaxJsonNode> elements();

    Stream<JavaxJsonNode> attributes();

}
