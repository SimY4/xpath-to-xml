package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.namespace.QName;

public final class GsonByNameNode extends AbstractGsonNode {

    private final JsonObject parentObject;
    private final String name;

    /**
     * Constructor.
     *
     * @param parentObject parent json object element
     * @param name         json object key
     * @param parent       parent node
     */
    public GsonByNameNode(JsonObject parentObject, String name, GsonNode parent) {
        super(parent);
        this.parentObject = parentObject;
        this.name = name;
    }

    @Override
    public QName getName() {
        return new QName(name);
    }

    @Override
    public JsonElement get() {
        return parentObject.get(name);
    }

    @Override
    public void set(JsonElement jsonElement) {
        if (null == jsonElement) {
            parentObject.remove(name);
        } else {
            parentObject.add(name, jsonElement);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        var gsonNodes = (GsonByNameNode) o;
        return getParent().equals(gsonNodes.getParent());
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

}
