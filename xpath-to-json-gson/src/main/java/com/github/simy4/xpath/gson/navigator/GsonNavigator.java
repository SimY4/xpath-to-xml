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
        if (parentElement.isJsonObject()) {
            final JsonObject parentObject = parentElement.getAsJsonObject();
            final GsonNode parentParent = parent.getParent();
            final String name = node.getName().getLocalPart();
            if (parentParent != null) {
                final JsonElement parentParentElement = parentParent.get();
                if (parentParentElement.isJsonArray()) {
                    final JsonArray parentParentArray = parentParentElement.getAsJsonArray();
                    final int index = prependToArray(parentElement, parentParentArray);
                    node.setParent(new GsonByNameNode(parentObject, name,
                            new GsonByIndexNode(parentParentArray, index + 1, node)));
                    return;
                }
            }
            final JsonArray jsonArray = new JsonArray();
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add(name, elementToCopy.deepCopy());
            jsonArray.add(jsonObject);
            jsonArray.add(parentObject);
            parent.getParent().set(jsonArray);
            node.setParent(new GsonByNameNode(jsonObject, name, new GsonByIndexNode(jsonArray, 1, node)));
        } else if (parentElement.isJsonArray()) {
            final JsonArray jsonArray = parentElement.getAsJsonArray();
            final int index = prependToArray(elementToCopy, jsonArray);
            node.setParent(new GsonByIndexNode(jsonArray, index + 1, node));
        } else {
            throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentElement);
        }
    }

    private int prependToArray(JsonElement elementToCopy, JsonArray parentArray) {
        int i = parentArray.size() - 1;
        JsonElement arrayElement = parentArray.get(i);
        parentArray.add(arrayElement);
        if (elementToCopy == arrayElement) {
            parentArray.set(i, elementToCopy.deepCopy());
        } else {
            for (; i > 0; --i) {
                arrayElement = parentArray.get(i);
                parentArray.set(i, arrayElement);
                if (elementToCopy == arrayElement) {
                    parentArray.set(i, elementToCopy.deepCopy());
                }
            }
        }
        return i;
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
                elementNode.set(newElement);
            } else {
                final GsonNode parentParent = parent.getParent();
                if (parentParent != null) {
                    final JsonElement parentParentElement = parentParent.get();
                    if (parentParentElement.isJsonArray()) {
                        elementNode = appendToArray(parent, name, parentParentElement.getAsJsonArray());
                    } else {
                        final JsonArray jsonArray = new JsonArray();
                        jsonArray.add(parentObject);
                        elementNode = appendToArray(parent, name, jsonArray);
                        parent.set(jsonArray);
                    }
                } else {
                    final JsonArray jsonArray = new JsonArray();
                    jsonArray.add(parentObject);
                    elementNode = appendToArray(parent, name, jsonArray);
                    parent.set(jsonArray);
                }
                elementNode.set(newElement);
            }
        } else if (parentElement.isJsonArray()) {
            elementNode = appendToArray(parent, name, parentElement.getAsJsonArray());
            elementNode.set(newElement);
        } else {
            throw new XmlBuilderException("Unable to create element for primitive node: " + parentElement);
        }
        return elementNode;
    }

    private GsonNode appendToArray(GsonNode parent, String name, JsonArray parentArray) {
        final JsonObject jsonObject = new JsonObject();
        parentArray.add(jsonObject);
        final GsonNode parentObjectNode = new GsonByIndexNode(parentArray, parentArray.size() - 1, parent);
        return new GsonByNameNode(jsonObject, name, parentObjectNode);
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
