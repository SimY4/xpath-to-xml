package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonNode;
import com.github.simy4.xpath.navigator.Navigator;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
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
    public Iterable<? extends JavaxJsonNode> elementsOf(JavaxJsonNode parent) {
        return () -> parent.elements().iterator();
    }

    @Override
    public Iterable<? extends JavaxJsonNode> attributesOf(JavaxJsonNode parent) {
        return () -> parent.attributes().iterator();
    }

    @Override
    public JavaxJsonNode createAttribute(JavaxJsonNode parent, QName attribute) throws XmlBuilderException {
        return appendElement(parent, attribute.getLocalPart(), jsonProvider.createValue(""));
    }

    @Override
    public JavaxJsonNode createElement(JavaxJsonNode parent, QName element) throws XmlBuilderException {
        return appendElement(parent, element.getLocalPart(), JsonValue.EMPTY_JSON_OBJECT);
    }

    @Override
    public void setText(JavaxJsonNode node, String text) throws XmlBuilderException {
        final JsonString jsonText = jsonProvider.createValue(text);
        JsonValue jsonValue = node.get();
        switch (jsonValue.getValueType()) {
            case OBJECT:
                final JsonObject jsonObject = jsonValue.asJsonObject();
                jsonValue = jsonProvider.createObjectBuilder(jsonObject)
                        .add("text", jsonProvider.createValue(text))
                        .build();
                break;
            case ARRAY:
                throw new XmlBuilderException("Unable to set text to JSON array: " + jsonValue);
            default:
                jsonValue = jsonText;
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
                final JsonObject jsonObject = JsonValue.EMPTY_JSON_OBJECT;
                if (parentParent != null) {
                    final JsonValue parentParentValue = parentParent.get();
                    if (JsonValue.ValueType.ARRAY == parentParentValue.getValueType()) {
                        final JsonArray jsonArray = parentParentValue.asJsonArray();
                        copyNode = prependToArray(parentParent, parentValue, jsonArray);
                        node.setParent(new JavaxJsonByIndexNode(copyNode.getIndex() + 1, parentParent));
                    } else {
                        copyNode = prependToNewArray(parent, parentValue);
                        node.setParent(new JavaxJsonByIndexNode(copyNode.getIndex() + 1, parent));
                    }
                } else {
                    copyNode = prependToNewArray(parent, parentValue);
                }
                elementNode = new JavaxJsonByNameNode(name, copyNode);
                copyNode.set(jsonProvider, jsonObject);
                break;
            case ARRAY:
                final JsonArray jsonArray = parentValue.asJsonArray();
                copyNode = prependToArray(parent, valueToCopy, jsonArray);
                node.setParent(new JavaxJsonByIndexNode(copyNode.getIndex() + 1, parent));
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
                if (!parentObject.containsKey(name)) {
                    elementNode = new JavaxJsonByNameNode(name, parent);
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
        final int index = parentArray.size();
        parentArray = jsonProvider.createArrayBuilder(parentArray)
                .add(JsonValue.EMPTY_JSON_OBJECT)
                .build();
        parent.set(jsonProvider, parentArray);
        return new JavaxJsonByNameNode(name, new JavaxJsonByIndexNode(index, parent));
    }

    private JavaxJsonByIndexNode prependToNewArray(JavaxJsonNode parent, JsonValue valueToCopy) {
        final JsonArray jsonArray = jsonProvider.createArrayBuilder()
                .add(valueToCopy)
                .build();
        return prependToArray(parent, valueToCopy, jsonArray);
    }

    private JavaxJsonByIndexNode prependToArray(JavaxJsonNode parent, JsonValue valueToCopy, JsonArray parentArray) {
        final int index = parentArray.indexOf(valueToCopy);
        parentArray = jsonProvider.createArrayBuilder(parentArray)
                .add(index, valueToCopy)
                .build();
        parent.set(jsonProvider, parentArray);
        return new JavaxJsonByIndexNode(index, parent);
    }

}
