package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.util.TransformingIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

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
    public final void setParent(GsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final String getText() {
        final var jsonElement = get();
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else if (jsonElement.isJsonNull()) {
            return "null";
        } else if (jsonElement.isJsonObject()) {
            final var text = jsonElement.getAsJsonObject().get("text");
            if (null != text) {
                if (text.isJsonNull()) {
                    return "null";
                } else if (text.isJsonPrimitive()) {
                    return text.getAsString();
                }
            }
        }
        return "";
    }

    @Override
    public final Iterator<GsonNode> iterator() {
        return traverse(get(), this);
    }

    @Override
    @SuppressWarnings("EqualsGetClass")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        var that = (AbstractGsonNode) o;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(get(), "???");
    }

    private static Iterator<GsonNode> traverse(JsonElement jsonElement, GsonNode parent) {
        if (jsonElement.isJsonObject()) {
            final var jsonObject = jsonElement.getAsJsonObject();
            return new TransformingIterator<>(jsonObject.keySet().iterator(), name ->
                    new GsonByNameNode(jsonObject, name, parent));
        } else if (jsonElement.isJsonArray()) {
            final var jsonArray = jsonElement.getAsJsonArray();
            return new TransformingAndFlatteningIterator<>(jsonArray.iterator(),
                    new JsonArrayWrapper(jsonArray, parent));
        } else {
            return Collections.emptyIterator();
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
            return jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()
                    ? Collections.singleton(arrayElemNode).iterator() : traverse(jsonElement, arrayElemNode);
        }

    }

}
