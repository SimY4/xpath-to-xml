package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.namespace.QName;

public final class GsonByNameNode extends AbstractGsonNode {

    private final JsonObject parentObject;
    private final String name;

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
        parentObject.add(name, jsonElement);
    }

    @Override
    public void remove() {
        parentObject.remove(name);
    }

}
