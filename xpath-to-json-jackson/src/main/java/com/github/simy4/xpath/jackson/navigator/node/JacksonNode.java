package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Jackson node contract.
 *
 * @author Alex Simkin
 * @since 1.2
 */
public interface JacksonNode extends Node, Iterable<JacksonNode> {

    JacksonNode getParent();

    void setParent(JacksonNode parent);

    JsonNode get();

    void set(JsonNode jsonNode) throws XmlBuilderException;

    void remove() throws XmlBuilderException;

    Stream<JacksonNode> stream();

    @Override
    default Iterator<JacksonNode> iterator() {
        return stream().iterator();
    }

}
