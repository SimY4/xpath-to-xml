package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.util.ReadOnlyIterator;
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
    public final void setParent(GsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final String getText() {
        final JsonElement jsonElement = get();
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else if (jsonElement.isJsonNull()) {
            return "null";
        } else if (jsonElement.isJsonObject()) {
            final JsonElement text = jsonElement.getAsJsonObject().get("text");
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
        return null == jsonElement ? "???" : jsonElement.toString();
    }

    private static Iterator<GsonNode> traverse(JsonElement jsonElement, GsonNode parent) {
        if (jsonElement.isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            return new JsonObjectIterator(jsonObject.keySet().iterator(), jsonObject, parent);
        } else if (jsonElement.isJsonArray()) {
            final JsonArray jsonArray = jsonElement.getAsJsonArray();
            return new JsonArrayIterator(jsonArray.iterator(), jsonArray, parent);
        } else {
            return Collections.<GsonNode>emptyList().iterator();
        }
    }

    private static final class JsonObjectIterator implements Iterator<GsonNode> {

        private final Iterator<String> keysIterator;
        private final JsonObject parentObject;
        private final GsonNode parent;

        private JsonObjectIterator(Iterator<String> keysIterator, JsonObject parentObject, GsonNode parent) {
            this.keysIterator = keysIterator;
            this.parentObject = parentObject;
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return keysIterator.hasNext();
        }

        @Override
        public GsonNode next() {
            return new GsonByNameNode(parentObject, keysIterator.next(), parent);
        }

        @Override
        public void remove() {
            keysIterator.remove();
        }

    }

    private static final class JsonArrayIterator extends ReadOnlyIterator<GsonNode> {

        private final Iterator<JsonElement> arrayIterator;
        private final JsonArray parentArray;
        private final GsonNode parent;
        private int index;
        private Iterator<GsonNode> current = Collections.<GsonNode>emptyList().iterator();

        private JsonArrayIterator(Iterator<JsonElement> arrayIterator, JsonArray parentArray, GsonNode parent) {
            this.arrayIterator = arrayIterator;
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            boolean currentHasNext;
            while (!(currentHasNext = current.hasNext()) && arrayIterator.hasNext()) {
                final JsonElement jsonElement = arrayIterator.next();
                final GsonNode arrayElemNode = new GsonByIndexNode(parentArray, index++, parent);
                current = jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()
                        ? Collections.singleton(arrayElemNode).iterator() : traverse(jsonElement, arrayElemNode);
            }
            return currentHasNext;
        }

        @Override
        public GsonNode next() {
            return current.next();
        }

    }

}
