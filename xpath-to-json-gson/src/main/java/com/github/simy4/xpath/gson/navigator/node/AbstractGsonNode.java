package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Iterator;

abstract class AbstractGsonNode implements GsonNode {

    private GsonNode parent;

    AbstractGsonNode(GsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final GsonNode getParent() {
        return parent;
    }

    @Override
    public void setParent(GsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final String getText() {
        return get().toString();
    }

    @Override
    public Iterator<GsonNode> iterator() {
        final JsonElement jsonElement = get();
        if (jsonElement.isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            return new TransformingIterator<String, GsonNode>(jsonObject.keySet().iterator(),
                    new JsonObjectWrapper(jsonObject, this));
        } else if (jsonElement.isJsonArray()) {
            final JsonArray jsonArray = jsonElement.getAsJsonArray();
            return new FlatteningIterator<GsonNode>(new TransformingIterator<JsonElement, Iterator<GsonNode>>(
                    jsonArray.iterator(), new JsonArrayWrapper(jsonArray, this)));
        } else {
            return Collections.<GsonNode>emptyList().iterator();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractGsonNode that = (AbstractGsonNode) o;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        final JsonElement jsonElement = get();
        return null == jsonElement ? "null" : jsonElement.toString();
    }

    private static final class JsonObjectWrapper implements Function<String, GsonNode> {

        private final JsonObject parentObject;
        private final GsonNode parent;

        private JsonObjectWrapper(JsonObject parentObject, GsonNode parent) {
            this.parentObject = parentObject;
            this.parent = parent;
        }

        @Override
        public GsonNode apply(String name) {
            return new GsonByNameNode(parentObject, name, parent);
        }

    }

    private static final class JsonArrayWrapper implements Function<JsonElement, Iterator<GsonNode>> {

        private final JsonArray parentArray;
        private final GsonNode parent;
        private int index;

        private JsonArrayWrapper(JsonArray parentArray, GsonNode parent) {
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public Iterator<GsonNode> apply(JsonElement jsonElement) {
            final GsonNode arrayElemNode = new GsonByIndexNode(parentArray, index++, parent);
            if (jsonElement.isJsonObject()) {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                return new TransformingIterator<String, GsonNode>(jsonObject.keySet().iterator(),
                        new JsonObjectWrapper(jsonObject, arrayElemNode));
            } else {
                return Collections.singletonList(arrayElemNode).iterator();
            }
        }

    }

}
