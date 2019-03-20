package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.util.ReadOnlyIterator;

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
    public final Iterator<JacksonNode> iterator() {
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
        final JsonNode jsonNode = get();
        return null == jsonNode ? "???" : jsonNode.toString();
    }

    private static Iterator<JacksonNode> traverse(JsonNode jsonNode, JacksonNode parent) {
        if (jsonNode.isObject()) {
            return new JsonObjectIterator(jsonNode.fieldNames(), (ObjectNode) jsonNode, parent);
        } else if (jsonNode.isArray()) {
            return new JsonArrayIterator(jsonNode.elements(), (ArrayNode) jsonNode, parent);
        } else {
            return Collections.<JacksonNode>emptyList().iterator();
        }
    }

    private static final class JsonObjectIterator implements Iterator<JacksonNode> {

        private final Iterator<String> keysIterator;
        private final ObjectNode parentObject;
        private final JacksonNode parent;

        private JsonObjectIterator(Iterator<String> keysIterator, ObjectNode parentObject, JacksonNode parent) {
            this.keysIterator = keysIterator;
            this.parentObject = parentObject;
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return keysIterator.hasNext();
        }

        @Override
        public JacksonNode next() {
            return new JacksonByNameNode(parentObject, keysIterator.next(), parent);
        }

        @Override
        public void remove() {
            keysIterator.remove();
        }

    }

    private static final class JsonArrayIterator extends ReadOnlyIterator<JacksonNode> {

        private final Iterator<JsonNode> arrayIterator;
        private final ArrayNode parentArray;
        private final JacksonNode parent;
        private int index;
        private Iterator<JacksonNode> current = Collections.<JacksonNode>emptyList().iterator();

        private JsonArrayIterator(Iterator<JsonNode> arrayIterator, ArrayNode parentArray, JacksonNode parent) {
            this.arrayIterator = arrayIterator;
            this.parentArray = parentArray;
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            boolean currentHasNext;
            while (!(currentHasNext = current.hasNext()) && arrayIterator.hasNext()) {
                final JsonNode jsonNode = arrayIterator.next();
                final JacksonNode arrayElemNode = new JacksonByIndexNode(parentArray, index++, parent);
                current = jsonNode.isValueNode() ? Collections.singleton(arrayElemNode).iterator()
                        : traverse(jsonNode, arrayElemNode);
            }
            return currentHasNext;
        }

        @Override
        public JacksonNode next() {
            return current.next();
        }

    }

}
