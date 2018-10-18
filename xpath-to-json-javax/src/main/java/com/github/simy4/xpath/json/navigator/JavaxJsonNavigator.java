package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Predicate;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.xml.namespace.QName;

public class JavaxJsonNavigator implements Navigator<JavaxJsonNode> {

    private final JsonProvider jsonProvider;
    private final JavaxJsonNode json;

    public JavaxJsonNavigator(JsonProvider jsonProvider, JavaxJsonNode json) {
        this.jsonProvider = jsonProvider;
        this.json = json;
    }

    @Override
    public JavaxJsonNode root() {
        return json;
    }

    @Override
    public JavaxJsonNode parentOf(JavaxJsonNode node) {
        do {
            node = node.getParent();
        } while (node instanceof JavaxJsonByIndexNode);
        return node;
    }

    @Override
    public Iterable<? extends JavaxJsonNode> elementsOf(final JavaxJsonNode parent) {
        return () -> new FilteringIterator<>(parent.iterator(), new AttributePredicate(false));
    }

    @Override
    public Iterable<? extends JavaxJsonNode> attributesOf(final JavaxJsonNode parent) {
        return () -> new FilteringIterator<>(parent.iterator(), new AttributePredicate(true));
    }

    @Override
    public JavaxJsonNode createAttribute(JavaxJsonNode parent, QName attribute) throws XmlBuilderException {
        return appendElement(parent, attribute.getLocalPart(), jsonProvider.createValue(""));
    }

    @Override
    public JavaxJsonNode createElement(JavaxJsonNode parent, QName element) throws XmlBuilderException {
        return appendElement(parent, element.getLocalPart(), jsonProvider.createObjectBuilder().build());
    }

    @Override
    public void setText(JavaxJsonNode node, String text) throws XmlBuilderException {
        JsonValue jsonValue = node.get();
        switch (jsonValue.getValueType()) {
            case OBJECT:
                jsonValue = jsonProvider.createObjectBuilder(jsonValue.asJsonObject())
                        .add("text", jsonProvider.createValue(text))
                        .build();
                break;
            case ARRAY:
                throw new XmlBuilderException("Unable to set text to JSON array: " + jsonValue);
            default:
                jsonValue = jsonProvider.createValue(text);
                break;
        }
        node.set(jsonProvider, jsonValue);
    }

    @Override
    public void prependCopy(JavaxJsonNode node) throws XmlBuilderException {
        final JavaxJsonNode parent = node.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prepend copy to root node " + node.get());
        }
        final JsonValue valueToCopy = node.get();
        final JsonValue parentValue = parent.get();
        final JavaxJsonNode elementNode;
        final JavaxJsonByIndexNode copyNode;
        switch (parentValue.getValueType()) {
            case OBJECT:
                final JavaxJsonNode parentParent = parent.getParent();
                final String name = node.getName().getLocalPart();
                final JsonObject jsonObject = jsonProvider.createObjectBuilder().build();
                if (parentParent != null) {
                    final JsonValue parentParentValue = parentParent.get();
                    if (JsonValue.ValueType.ARRAY == parentParentValue.getValueType()) {
                        final JsonArray jsonArray = parentParentValue.asJsonArray();
                        copyNode = prependToArray(parentParent, parentValue, jsonArray);
                        parent.setParent(new JavaxJsonByIndexNode(jsonArray, copyNode.getIndex() + 1, node.getParent()));
                    } else {
                        copyNode = prependToNewArray(parent, parentValue);
                    }
                } else {
                    copyNode = prependToNewArray(parent, parentValue);
                }
                elementNode = new JavaxJsonByNameNode(jsonObject, name, copyNode);
                copyNode.set(jsonProvider, jsonObject);
                break;
            case ARRAY:
                final JsonArray jsonArray = parentValue.asJsonArray();
                copyNode = prependToArray(parent, valueToCopy, jsonArray);
                node.setParent(new JavaxJsonByIndexNode(jsonArray, copyNode.getIndex() + 1, node.getParent()));
                elementNode = copyNode;
                break;
            default:
                throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentValue);
        }
        elementNode.set(jsonProvider, valueToCopy);
    }

    @Override
    public void remove(JavaxJsonNode node) throws XmlBuilderException {
        node.remove(jsonProvider);
    }

    private JavaxJsonNode appendElement(JavaxJsonNode parent, String name, JsonValue newValue) {
        final JsonValue parentValue = parent.get();
        final JavaxJsonNode elementNode;
        switch (parentValue.getValueType()) {
            case OBJECT:
                final JsonObject parentObject = parentValue.asJsonObject();
                if (null == parentObject.get(name)) {
                    elementNode = new JavaxJsonByNameNode(parentObject, name, parent);
                } else {
                    final JavaxJsonNode parentParent = parent.getParent();
                    if (parentParent != null) {
                        final JsonValue parentParentValue = parentParent.get();
                        if (JsonValue.ValueType.ARRAY == parentParentValue.getValueType()) {
                            elementNode = appendToArray(parentParent, name, parentParentValue.asJsonArray());
                        } else {
                            elementNode = appendToNewArray(parent, name, parentObject);
                        }
                    } else {
                        elementNode = appendToNewArray(parent, name, parentObject);
                    }
                }
                break;
            case ARRAY:
                elementNode = appendToArray(parent, name, parentValue.asJsonArray());
                break;
            default:
                throw new XmlBuilderException("Unable to create element for primitive node: " + parentValue);
        }
        elementNode.set(jsonProvider, newValue);
        return elementNode;
    }

    private JavaxJsonNode appendToNewArray(JavaxJsonNode parent, String name, JsonObject parentObject) {
        final JsonArray jsonArray = jsonProvider.createArrayBuilder()
                .add(parentObject)
                .build();
        return appendToArray(parent, name, jsonArray);
    }

    private JavaxJsonNode appendToArray(JavaxJsonNode parent, String name, JsonArray parentArray) {
        final JsonObject jsonObject = jsonProvider.createObjectBuilder().build();
        final JsonArray jsonArray = jsonProvider.createArrayBuilder(parentArray)
                .add(jsonObject)
                .build();
        parent.set(jsonProvider, jsonArray);
        final JavaxJsonNode parentObjectNode = new JavaxJsonByIndexNode(jsonArray, parentArray.size(), parent);
        return new JavaxJsonByNameNode(jsonObject, name, parentObjectNode);
    }

    private JavaxJsonByIndexNode prependToNewArray(JavaxJsonNode parent, JsonValue valueToCopy) {
        final JsonArray jsonArray = jsonProvider.createArrayBuilder()
                .add(valueToCopy)
                .build();
        final JavaxJsonByIndexNode elementNode = prependToArray(parent, valueToCopy, jsonArray);
        if (null != parent) {
            parent.set(jsonProvider, jsonArray);
            parent.setParent(new JavaxJsonByIndexNode(jsonArray, 1, parent.getParent()));
        }
        return elementNode;
    }

    private JavaxJsonByIndexNode prependToArray(JavaxJsonNode parent, JsonValue valueToCopy, JsonArray parentArray) {
        int i = parentArray.indexOf(valueToCopy);
        parentArray.add(i, valueToCopy);
        return new JavaxJsonByIndexNode(parentArray, i, parent);
    }

    private static final class AttributePredicate implements Predicate<JavaxJsonNode> {

        private final boolean attribute;

        private AttributePredicate(boolean attribute) {
            this.attribute = attribute;
        }

        @Override
        public boolean test(JavaxJsonNode javaxJsonNode) {
            final JsonValue jsonValue = javaxJsonNode.get();
            switch (jsonValue.getValueType()) {
                case OBJECT:
                case ARRAY:
                    return !attribute;
                default:
                    return attribute;
            }
        }

    }

}
