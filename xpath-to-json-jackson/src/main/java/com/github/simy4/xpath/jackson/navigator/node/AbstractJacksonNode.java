package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.util.FlatteningIterator;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;

import java.util.Collections;
import java.util.Iterator;

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
    public void setParent(JacksonNode parent) {
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
    public Iterator<JacksonNode> iterator() {
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

        AbstractJacksonNode that = (AbstractJacksonNode) o;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        final JsonNode jsonNode = get();
        return null == jsonNode ? "???" : jsonNode.toString();
    }

    private static Iterator<JacksonNode> traverse(JsonNode jsonNode, JacksonNode parent) {
        if (jsonNode.isObject()) {
            return new TransformingIterator<>(jsonNode.fieldNames(),
                    new JsonObjectWrapper((ObjectNode) jsonNode, parent));
        } else if (jsonNode.isArray()) {
            return new FlatteningIterator<>(new TransformingIterator<>(jsonNode.elements(),
                    new JsonArrayWrapper((ArrayNode) jsonNode, parent)));
        } else {
            return Collections.<JacksonNode>emptyList().iterator();
        }
    }

    private static final class JsonObjectWrapper implements Function<String, JacksonNode> {

        private final ObjectNode parentObject;
        private final JacksonNode parent;

        private JsonObjectWrapper(ObjectNode parentObject, JacksonNode parent) {
            this.parentObject = parentObject;
            this.parent = parent;
        }

        @Override
        public JacksonNode apply(String name) {
            return new JacksonByNameNode(parentObject, name, parent);
        }

    }

    private static final class JsonArrayWrapper implements Function<JsonNode, Iterator<JacksonNode>> {

        private final ArrayNode parentArray;
        private final JacksonNode parent;
        private int index;

        private JsonArrayWrapper(ArrayNode parentArray, JacksonNode parent) {
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public Iterator<JacksonNode> apply(JsonNode jsonNode) {
            final JacksonNode arrayElemNode = new JacksonByIndexNode(parentArray, index++, parent);
            return jsonNode.isValueNode() ? Collections.singleton(arrayElemNode).iterator()
                    : traverse(jsonNode, arrayElemNode);
        }

    }

}
