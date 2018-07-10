package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.namespace.QName;

public final class JacksonByNameNode extends AbstractJacksonNode {

    private final ObjectNode parentObject;
    private final String name;

    /**
     * Constructor.
     *
     * @param parentObject parent json object element
     * @param name         json object key
     * @param parent       parent node
     */
    public JacksonByNameNode(ObjectNode parentObject, String name, JacksonNode parent) {
        super(parent);
        this.parentObject = parentObject;
        this.name = name;
    }

    @Override
    public QName getName() {
        return new QName(name);
    }

    @Override
    public JsonNode get() {
        return parentObject.get(name);
    }

    @Override
    public void set(JsonNode jsonElement) {
        parentObject.set(name, jsonElement);
    }

    @Override
    public void remove() {
        parentObject.remove(name);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JacksonByNameNode gsonNodes = (JacksonByNameNode) o;
        return getParent().equals(gsonNodes.getParent());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

}
