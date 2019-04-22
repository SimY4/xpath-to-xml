package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonObject;
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
    public Stream<JavaxJsonNode> elements() {
        return traverse(get(), this, false);
    }

    @Override
    public Stream<JavaxJsonNode> attributes() {
        return traverse(get(), this, true);
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
        return Objects.toString(get(), "???");
    }

    private static Stream<JavaxJsonNode> traverse(JsonValue jsonValue, JavaxJsonNode parent, boolean attribute) {
        switch (jsonValue.getValueType()) {
            case OBJECT:
                final JsonObject jsonObject = jsonValue.asJsonObject();
                return jsonObject.keySet().stream()
                        .filter(name -> attribute == isAttribute(jsonObject.get(name)))
                        .map(name -> new JavaxJsonByNameNode(name, parent));
            case ARRAY:
                return jsonValue.asJsonArray().stream().flatMap(new JsonArrayWrapper(parent, attribute));
            default:
                return Stream.empty();
        }
    }

    private static boolean isAttribute(JsonValue jsonValue) {
        return JsonValue.ValueType.OBJECT != jsonValue.getValueType()
                && JsonValue.ValueType.ARRAY != jsonValue.getValueType();
    }

    private static final class JsonArrayWrapper implements Function<JsonValue, Stream<JavaxJsonNode>> {

        private final JavaxJsonNode parent;
        private final boolean attribute;
        private int index;

        private JsonArrayWrapper(JavaxJsonNode parent, boolean attribute) {
            this.parent = parent;
            this.attribute = attribute;
        }

        @Override
        public Stream<JavaxJsonNode> apply(JsonValue jsonValue) {
            final JavaxJsonNode arrayElemNode = new JavaxJsonByIndexNode(index++, parent);
            return isAttribute(jsonValue) ? attribute ? Stream.of(arrayElemNode) : Stream.empty()
                    : traverse(jsonValue, arrayElemNode, attribute);
        }

    }

}
