package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import java.util.stream.Stream;

/**
 * Javax JSON node contract.
 *
 * @author Alex Simkin
 * @since 2.0
 */
public interface JakartaJsonNode extends Node {

  JakartaJsonNode getParent();

  void setParent(JakartaJsonNode parent);

  JsonValue get();

  void set(JsonProvider jsonProvider, JsonValue jsonValue) throws XmlBuilderException;

  Stream<JakartaJsonNode> elements();

  Stream<JakartaJsonNode> attributes();
}
