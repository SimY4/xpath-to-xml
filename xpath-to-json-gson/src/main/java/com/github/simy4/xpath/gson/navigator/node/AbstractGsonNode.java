package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public final Stream<GsonNode> stream() {
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
        return Objects.toString(get(), "???");
    }

    private static Stream<GsonNode> traverse(JsonElement jsonElement, GsonNode parent) {
        if (jsonElement.isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            return jsonObject.keySet().stream()
                    .map(name -> new GsonByNameNode(jsonObject, name, parent));
        } else if (jsonElement.isJsonArray()) {
            final JsonArray jsonArray = jsonElement.getAsJsonArray();
            return StreamSupport.stream(jsonArray.spliterator(), false)
                    .flatMap(new JsonArrayWrapper(jsonArray, parent));
        } else {
            return Stream.empty();
        }
    }

    private static final class JsonArrayWrapper implements Function<JsonElement, Stream<GsonNode>> {

        private final JsonArray parentArray;
        private final GsonNode parent;
        private int index;

        private JsonArrayWrapper(JsonArray parentArray, GsonNode parent) {
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public Stream<GsonNode> apply(JsonElement jsonElement) {
            final GsonNode arrayElemNode = new GsonByIndexNode(parentArray, index++, parent);
            return jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()
                    ? Stream.of(arrayElemNode) : traverse(jsonElement, arrayElemNode);
        }

    }

}
