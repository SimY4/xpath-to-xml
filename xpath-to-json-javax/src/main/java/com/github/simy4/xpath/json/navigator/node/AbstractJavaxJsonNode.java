package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.util.TransformingIterator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Collections;
import java.util.Iterator;

abstract class AbstractJavaxJsonNode implements JavaxJsonNode {

    private JavaxJsonNode parent;

    AbstractJavaxJsonNode(JavaxJsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final JavaxJsonNode getParent() {
        return parent;
    }

    @Override
    public void setParent(JavaxJsonNode parent) {
        this.parent = parent;
    }

    @Override
    public final String getText() {
        final JsonValue jsonValue = get();
        switch (jsonValue.getValueType()) {
            case OBJECT:
                return jsonValue.asJsonObject().getString("text", "null");
            case ARRAY:
                return "";
            case NULL:
                return "null";
            default:
                return jsonValue.toString();
        }
    }

    @Override
    public Iterator<JavaxJsonNode> iterator() {
        return traverse(get(), this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractJavaxJsonNode that = (AbstractJavaxJsonNode) o;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        final JsonValue jsonValue = get();
        return null == jsonValue ? "???" : jsonValue.toString();
    }

    private static Iterator<JavaxJsonNode> traverse(JsonValue jsonValue, JavaxJsonNode parent) {
        switch (jsonValue.getValueType()) {
            case OBJECT:
                final JsonObject jsonObject = jsonValue.asJsonObject();
                return new TransformingIterator<>(jsonObject.keySet().iterator(),
                        new JsonObjectWrapper(jsonObject, parent));
            case ARRAY:
                final JsonArray jsonArray = jsonValue.asJsonArray();
                return new TransformingAndFlatteningIterator<>(jsonArray.iterator(),
                        new JsonArrayWrapper(jsonArray, parent));
            default:
                return Collections.<JavaxJsonNode>emptyList().iterator();
        }
    }

    private static final class JsonObjectWrapper implements Function<String, JavaxJsonNode> {

        private final JsonObject parentObject;
        private final JavaxJsonNode parent;

        private JsonObjectWrapper(JsonObject parentObject, JavaxJsonNode parent) {
            this.parentObject = parentObject;
            this.parent = parent;
        }

        @Override
        public JavaxJsonNode apply(String name) {
            return new JavaxJsonByNameNode(parentObject, name, parent);
        }

    }

    private static final class JsonArrayWrapper implements Function<JsonValue, Iterator<JavaxJsonNode>> {

        private final JsonArray parentArray;
        private final JavaxJsonNode parent;
        private int index;

        private JsonArrayWrapper(JsonArray parentArray, JavaxJsonNode parent) {
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public Iterator<JavaxJsonNode> apply(JsonValue jsonValue) {
            final JavaxJsonNode arrayElemNode = new JavaxJsonByIndexNode(parentArray, index++, parent);
            switch (jsonValue.getValueType()) {
                case OBJECT:
                case ARRAY:
                    return traverse(jsonValue, arrayElemNode);
                default:
                    return Collections.singleton(arrayElemNode).iterator();
            }
        }

    }

}
