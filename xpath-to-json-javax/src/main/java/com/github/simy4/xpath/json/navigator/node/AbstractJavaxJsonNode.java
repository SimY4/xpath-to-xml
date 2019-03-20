package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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
        final var jsonValue = get();
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
    public final Stream<JavaxJsonNode> stream() {
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

        var that = (AbstractJavaxJsonNode) o;
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

    private static Stream<JavaxJsonNode> traverse(JsonValue jsonValue, JavaxJsonNode parent) {
        switch (jsonValue.getValueType()) {
            case OBJECT:
                return jsonValue.asJsonObject().keySet().stream().map(name -> new JavaxJsonByNameNode(name, parent));
            case ARRAY:
                return jsonValue.asJsonArray().stream().flatMap(new JsonArrayWrapper(parent));
            default:
                return Stream.empty();
        }
    }

    private static final class JsonArrayWrapper implements Function<JsonValue, Stream<JavaxJsonNode>> {

        private final JavaxJsonNode parent;
        private int index;

        private JsonArrayWrapper(JavaxJsonNode parent) {
            this.parent = parent;
        }

        @Override
        public Stream<JavaxJsonNode> apply(JsonValue jsonValue) {
            final var arrayElemNode = new JavaxJsonByIndexNode(index++, parent);
            switch (jsonValue.getValueType()) {
                case OBJECT:
                case ARRAY:
                    return traverse(jsonValue, arrayElemNode);
                default:
                    return Stream.of(arrayElemNode);
            }
        }

    }

}
