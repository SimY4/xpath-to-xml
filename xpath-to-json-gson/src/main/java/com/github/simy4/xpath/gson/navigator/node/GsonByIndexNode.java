package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.xml.namespace.QName;

public final class GsonByIndexNode extends AbstractGsonNode {

    private final JsonArray jsonArray;
    private final int index;

    public GsonByIndexNode(JsonArray jsonArray, int index, GsonNode parent) {
        super(parent);
        this.jsonArray = jsonArray;
        this.index = index;
    }

    @Override
    public QName getName() {
        return new QName("array[" + index + ']');
    }

    @Override
    public JsonElement get() {
        return jsonArray.get(index);
    }

    @Override
    public void set(JsonElement jsonElement) {
        jsonArray.set(index, jsonElement);
    }

    @Override
    public void remove() {
        jsonArray.remove(index);
    }

}
