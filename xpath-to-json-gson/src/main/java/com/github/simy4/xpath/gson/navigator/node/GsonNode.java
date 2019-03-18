package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.google.gson.JsonElement;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Gson node contract.
 *
 * @author Alex Simkin
 * @since 1.1
 */
public interface GsonNode extends Node, Iterable<GsonNode> {

    GsonNode getParent();

    void setParent(GsonNode parent);

    JsonElement get();

    void set(JsonElement jsonElement) throws XmlBuilderException;

    void remove() throws XmlBuilderException;

    Stream<GsonNode> stream();

    @Override
    default Iterator<GsonNode> iterator() {
        return stream().iterator();
    }

}
