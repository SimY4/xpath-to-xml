package com.github.simy4.xpath.gson.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.gson.navigator.node.GsonByIndexNode;
import com.github.simy4.xpath.gson.navigator.node.GsonByNameNode;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.FilteringIterator;
import com.github.simy4.xpath.util.Predicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.xml.namespace.QName;
import java.util.Iterator;

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
        return node.getParent();
    }

    @Override
    public Iterable<? extends GsonNode> elementsOf(final GsonNode parent) {
        return new Iterable<GsonNode>() {
            @Override
            public Iterator<GsonNode> iterator() {
                return new FilteringIterator<GsonNode>(parent.iterator(), new AttributePredicate(false));
            }
        };
    }

    @Override
    public Iterable<? extends GsonNode> attributesOf(final GsonNode parent) {
        return new Iterable<GsonNode>() {
            @Override
            public Iterator<GsonNode> iterator() {
                return new FilteringIterator<GsonNode>(parent.iterator(), new AttributePredicate(true));
            }
        };
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
        node.set(new JsonPrimitive(text));
    }

    @Override
    public void prependCopy(GsonNode node) throws XmlBuilderException {
        final GsonNode parent = node.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prependcopy to root node " + node.get());
        }
        final JsonElement elementToCopy = node.get();
        final JsonElement parentElement = parent.get();
        final GsonNode elementNode;
        if (parentElement.isJsonObject()) {
            final GsonNode parentParent = parent.getParent();
            final String name = node.getName().getLocalPart();
            final JsonObject jsonObject = new JsonObject();
            final GsonByIndexNode copyNode;
            if (parentParent != null) {
                final JsonElement parentParentElement = parentParent.get();
                if (parentParentElement.isJsonArray()) {
                    final JsonArray jsonArray = parentParentElement.getAsJsonArray();
                    copyNode = prependToArray(parentParent, parentElement, jsonArray);
                    parent.setParent(new GsonByIndexNode(jsonArray, copyNode.getIndex() + 1, node.getParent()));
                } else {
                    copyNode = prependToNewArray(parent, parentElement);
                }
            } else {
                copyNode = prependToNewArray(parent, parentElement);
            }
            elementNode = new GsonByNameNode(jsonObject, name, copyNode);
            copyNode.set(jsonObject);
        } else if (parentElement.isJsonArray()) {
            final JsonArray jsonArray = parentElement.getAsJsonArray();
            final GsonByIndexNode copyNode = prependToArray(parent, elementToCopy, jsonArray);
            node.setParent(new GsonByIndexNode(jsonArray, copyNode.getIndex() + 1, node.getParent()));
            elementNode = copyNode;
        } else {
            throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentElement);
        }
        elementNode.set(elementToCopy.deepCopy());
    }

    @Override
    public void remove(GsonNode node) throws XmlBuilderException {
        node.remove();
    }

    private GsonNode appendElement(GsonNode parent, String name, JsonElement newElement) {
        final JsonElement parentElement = parent.get();
        final GsonNode elementNode;
        if (parentElement.isJsonObject()) {
            final JsonObject parentObject = parentElement.getAsJsonObject();
            if (null == parentObject.get(name)) {
                elementNode = new GsonByNameNode(parentObject, name, parent);
            } else {
                final GsonNode parentParent = parent.getParent();
                if (parentParent != null) {
                    final JsonElement parentParentElement = parentParent.get();
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
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(parentObject);
        final GsonNode elementNode = appendToArray(parent, name, jsonArray);
        if (null != parent) {
            parent.set(jsonArray);
        }
        return elementNode;
    }

    private GsonNode appendToArray(GsonNode parent, String name, JsonArray parentArray) {
        final JsonObject jsonObject = new JsonObject();
        parentArray.add(jsonObject);
        final GsonNode parentObjectNode = new GsonByIndexNode(parentArray, parentArray.size() - 1, parent);
        return new GsonByNameNode(jsonObject, name, parentObjectNode);
    }

    private GsonByIndexNode prependToNewArray(GsonNode parent, JsonElement elementToCopy) {
        final JsonArray jsonArray = new JsonArray();
        jsonArray.add(elementToCopy);
        final GsonByIndexNode elementNode = prependToArray(parent, elementToCopy, jsonArray);
        if (null != parent) {
            parent.set(jsonArray);
            parent.setParent(new GsonByIndexNode(jsonArray, 1, parent.getParent()));
        }
        return elementNode;
    }

    private GsonByIndexNode prependToArray(GsonNode parent, JsonElement elementToCopy, JsonArray parentArray) {
        int i = parentArray.size() - 1;
        JsonElement arrayElement = parentArray.get(i);
        parentArray.add(arrayElement);
        while (elementToCopy != arrayElement && i > 0) {
            arrayElement = parentArray.get(i - 1);
            parentArray.set(i, arrayElement);
            i -= 1;
        }
        return new GsonByIndexNode(parentArray, i, parent);
    }

    private static final class AttributePredicate implements Predicate<GsonNode> {

        private final boolean attribute;

        private AttributePredicate(boolean attribute) {
            this.attribute = attribute;
        }

        @Override
        public boolean test(GsonNode gsonNode) {
            final JsonElement jsonElement = gsonNode.get();
            return attribute == (jsonElement.isJsonNull() || jsonElement.isJsonPrimitive());
        }

    }

}
