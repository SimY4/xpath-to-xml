package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.xml.namespace.QName;

public final class JacksonByIndexNode extends AbstractJacksonNode {

    private final ArrayNode parentArray;
    private final int index;

    /**
     * Constructor.
     *
     * @param parentArray parent json array element
     * @param index       json array index
     * @param parent      parent node
     */
    public JacksonByIndexNode(ArrayNode parentArray, int index, JacksonNode parent) {
        super(parent);
        this.parentArray = parentArray;
        this.index = index;
    }

    @Override
    public QName getName() {
        return new QName("array[" + index + ']');
    }

    @Override
    public JsonNode get() {
        return parentArray.get(index);
    }

    @Override
    public void set(JsonNode jsonElement) {
        parentArray.set(index, jsonElement);
    }

    @Override
    public void remove() {
        parentArray.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JacksonByIndexNode gsonNodes = (JacksonByIndexNode) o;
        return index == gsonNodes.index;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + index;
        return result;
    }

    public int getIndex() {
        return index;
    }

}
