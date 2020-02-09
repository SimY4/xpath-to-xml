package com.github.simy4.xpath.gson.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.gson.navigator.node.GsonByIndexNode;
import com.github.simy4.xpath.gson.navigator.node.GsonByNameNode;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.xml.namespace.QName;

public class GsonNavigator implements Navigator<GsonNode> {

    private final GsonNode json;

    public GsonNavigator(GsonNode json) {
        this.json = json;
    }

    @Override
    public GsonNode root() {
        return json;
    }

    @Override
    public GsonNode parentOf(GsonNode node) {
        do {
            node = node.getParent();
        } while (node instanceof GsonByIndexNode);
        return node;
    }

    @Override
    public Iterable<? extends GsonNode> elementsOf(GsonNode parent) {
        return parent.elements();
    }

    @Override
    public Iterable<? extends GsonNode> attributesOf(GsonNode parent) {
        return parent.attributes();
    }

    @Override
    public GsonNode createAttribute(GsonNode parent, QName attribute) throws XmlBuilderException {
        return appendElement(parent, attribute.getLocalPart(), new JsonPrimitive(""));
    }

    @Override
    public GsonNode createElement(GsonNode parent, QName element) throws XmlBuilderException {
        return appendElement(parent, element.getLocalPart(), new JsonObject());
    }

    @Override
    public void setText(GsonNode node, String text) throws XmlBuilderException {
        final var jsonElement = node.get();
        if (jsonElement.isJsonObject()) {
            jsonElement.getAsJsonObject().add("text", new JsonPrimitive(text));
        } else if (jsonElement.isJsonArray()) {
            throw new XmlBuilderException("Unable to set text to JSON array: " + jsonElement);
        } else {
            node.set(new JsonPrimitive(text));
        }
    }

    @Override
    public void prependCopy(GsonNode node) throws XmlBuilderException {
        final var parent = node.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prependcopy to root node " + node.get());
        }
        final var elementToCopy = node.get();
        final var parentElement = parent.get();
        final GsonNode elementNode;
        if (parentElement.isJsonObject()) {
            final var parentParent = parent.getParent();
            final var name = node.getName().getLocalPart();
            final var jsonObject = new JsonObject();
            final GsonByIndexNode copyNode;
            if (parentParent != null) {
                final var parentParentElement = parentParent.get();
                if (parentParentElement.isJsonArray()) {
                    final var jsonArray = parentParentElement.getAsJsonArray();
                    copyNode = prependToArray(parentParent, parentElement, jsonArray);
                    parent.setParent(new GsonByIndexNode(jsonArray, copyNode.getIndex() + 1, parentParent));
                } else {
                    copyNode = prependToNewArray(parent, parentElement);
                    node.setParent(new GsonByIndexNode(parent.get().getAsJsonArray(), copyNode.getIndex() + 1, parent));
                }
            } else {
                copyNode = prependToNewArray(parent, parentElement);
            }
            elementNode = new GsonByNameNode(jsonObject, name, copyNode);
            copyNode.set(jsonObject);
        } else if (parentElement.isJsonArray()) {
            final var jsonArray = parentElement.getAsJsonArray();
            final var copyNode = prependToArray(parent, elementToCopy, jsonArray);
            node.setParent(new GsonByIndexNode(jsonArray, copyNode.getIndex() + 1, parent));
            elementNode = copyNode;
        } else {
            throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentElement);
        }
        elementNode.set(elementToCopy.deepCopy());
    }

    @Override
    public void remove(GsonNode node) throws XmlBuilderException {
        node.set(null);
    }

    private GsonNode appendElement(GsonNode parent, String name, JsonElement newElement) {
        final var parentElement = parent.get();
        final GsonNode elementNode;
        if (parentElement.isJsonObject()) {
            final var parentObject = parentElement.getAsJsonObject();
            if (!parentObject.has(name)) {
                elementNode = new GsonByNameNode(parentObject, name, parent);
            } else {
                final var parentParent = parent.getParent();
                if (parentParent != null) {
                    final var parentParentElement = parentParent.get();
                    if (parentParentElement.isJsonArray()) {
                        elementNode = appendToArray(parentParent, name, parentParentElement.getAsJsonArray());
                    } else {
                        elementNode = appendToNewArray(parent, name, parentObject);
                    }
                } else {
                    elementNode = appendToNewArray(parent, name, parentObject);
                }
            }
        } else if (parentElement.isJsonArray()) {
            elementNode = appendToArray(parent, name, parentElement.getAsJsonArray());
        } else {
            throw new XmlBuilderException("Unable to create element for primitive node: " + parentElement);
        }
        elementNode.set(newElement);
        return elementNode;
    }

    private GsonNode appendToNewArray(GsonNode parent, String name, JsonObject parentObject) {
        final var jsonArray = new JsonArray();
        jsonArray.add(parentObject);
        final var elementNode = appendToArray(parent, name, jsonArray);
        parent.set(jsonArray);
        return elementNode;
    }

    private GsonNode appendToArray(GsonNode parent, String name, JsonArray parentArray) {
        final var jsonObject = new JsonObject();
        parentArray.add(jsonObject);
        final var parentObjectNode = new GsonByIndexNode(parentArray, parentArray.size() - 1, parent);
        return new GsonByNameNode(jsonObject, name, parentObjectNode);
    }

    private GsonByIndexNode prependToNewArray(GsonNode parent, JsonElement elementToCopy) {
        final var jsonArray = new JsonArray();
        jsonArray.add(elementToCopy);
        final var elementNode = prependToArray(parent, elementToCopy, jsonArray);
        parent.set(jsonArray);
        parent.setParent(new GsonByIndexNode(jsonArray, 1, parent.getParent()));
        return elementNode;
    }

    private GsonByIndexNode prependToArray(GsonNode parent, JsonElement elementToCopy, JsonArray parentArray) {
        var i = parentArray.size() - 1;
        var arrayElement = parentArray.get(i);
        parentArray.add(arrayElement);
        while (elementToCopy != arrayElement && i > 0) {
            arrayElement = parentArray.get(i - 1);
            parentArray.set(i, arrayElement);
            i -= 1;
        }
        return new GsonByIndexNode(parentArray, i, parent);
    }

}
