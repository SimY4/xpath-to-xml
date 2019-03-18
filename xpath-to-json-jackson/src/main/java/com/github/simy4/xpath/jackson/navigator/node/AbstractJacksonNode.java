package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

abstract class AbstractJacksonNode implements JacksonNode {

    private JacksonNode parent;

    AbstractJacksonNode(JacksonNode parent) {
        this.parent = parent;
    }

    @Override
    public final JacksonNode getParent() {
        return parent;
    }

    @Override
    public final void setParent(JacksonNode parent) {
        this.parent = parent;
    }

    @Override
    public final String getText() {
        final JsonNode jsonNode = get();
        if (jsonNode.isValueNode()) {
            return jsonNode.asText();
        } else if (jsonNode.isObject()) {
            final JsonNode text = jsonNode.get("text");
            if (text != null) {
                return text.asText("");
            }
        }
        return "";
    }

    @Override
    public final Stream<JacksonNode> stream() {
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

        AbstractJacksonNode that = (AbstractJacksonNode) o;
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

    private static Stream<JacksonNode> traverse(JsonNode jsonNode, JacksonNode parent) {
        if (jsonNode.isObject()) {
            final ObjectNode objectNode = (ObjectNode) jsonNode;
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.fieldNames(), 0), false)
                    .map(name -> new JacksonByNameNode(objectNode, name, parent));
        } else if (jsonNode.isArray()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.elements(), 0), false)
                    .flatMap(new JsonArrayWrapper((ArrayNode) jsonNode, parent));
        } else {
            return Stream.empty();
        }
    }

    private static final class JsonArrayWrapper implements Function<JsonNode, Stream<JacksonNode>> {

        private final ArrayNode parentArray;
        private final JacksonNode parent;
        private int index;

        private JsonArrayWrapper(ArrayNode parentArray, JacksonNode parent) {
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public Stream<JacksonNode> apply(JsonNode jsonNode) {
            final JacksonNode arrayElemNode = new JacksonByIndexNode(parentArray, index++, parent);
            return jsonNode.isValueNode() ? Stream.of(arrayElemNode)
                    : traverse(jsonNode, arrayElemNode);
        }

    }

}
