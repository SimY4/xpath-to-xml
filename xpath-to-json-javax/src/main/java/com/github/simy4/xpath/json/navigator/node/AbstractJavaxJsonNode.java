package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingAndFlatteningIterator;
import com.github.simy4.xpath.util.TransformingIterator;

import javax.json.JsonString;
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
    public final void setParent(JavaxJsonNode parent) {
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
            case STRING:
                return ((JsonString) jsonValue).getString();
            case NULL:
                return "null";
            default:
                return jsonValue.toString();
        }
    }

    @Override
    public final Iterator<JavaxJsonNode> iterator() {
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
                return new TransformingIterator<>(jsonValue.asJsonObject().keySet().iterator(), name ->
                        new JavaxJsonByNameNode(name, parent));
            case ARRAY:
                return new TransformingAndFlatteningIterator<>(jsonValue.asJsonArray().iterator(),
                        new JsonArrayWrapper(parent));
            default:
                return Collections.emptyIterator();
        }
    }

    private static final class JsonArrayWrapper implements Function<JsonValue, Iterator<JavaxJsonNode>> {

        private final JavaxJsonNode parent;
        private int index;

        private JsonArrayWrapper(JavaxJsonNode parent) {
            this.parent = parent;
        }

        @Override
        public Iterator<JavaxJsonNode> apply(JsonValue jsonValue) {
            final JavaxJsonNode arrayElemNode = new JavaxJsonByIndexNode(index++, parent);
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
