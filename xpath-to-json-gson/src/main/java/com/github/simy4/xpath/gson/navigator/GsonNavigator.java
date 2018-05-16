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
        final JsonElement parentElement = parent.get();
        if (parentElement.isJsonObject()) {
            final JsonObject parentObject = parentElement.getAsJsonObject();
            final String name = attribute.getLocalPart();
            if (null == parentObject.get(name)) {
                final GsonNode attributeNode = new GsonByNameNode(parentObject, name, parent);
                attributeNode.set(new JsonPrimitive(""));
                return attributeNode;
            } else {
                final JsonArray jsonArray = new JsonArray();
                final JsonObject jsonObject = new JsonObject();
                jsonArray.add(parentObject);
                final GsonNode jsonObjectNode = new GsonByIndexNode(jsonArray, 1, parent);
                final GsonNode attributeNode = new GsonByNameNode(jsonObject, name, jsonObjectNode);
                attributeNode.set(new JsonPrimitive(""));
                return attributeNode;
            }
        } else if (parentElement.isJsonArray()) {
            final JsonArray parentArray = parentElement.getAsJsonArray();
            final JsonObject jsonObject = new JsonObject();
            parentArray.add(jsonObject);
            final GsonNode jsonObjectNode = new GsonByIndexNode(parentArray, parentArray.size() - 1, parent);
            final GsonNode attributeNode = new GsonByNameNode(jsonObject, attribute.getLocalPart(), jsonObjectNode);
            attributeNode.set(new JsonPrimitive(""));
            return attributeNode;
        } else {
            throw new XmlBuilderException("Unable to create attribute for primitive node: " + parentElement);
        }
    }

    @Override
    public GsonNode createElement(GsonNode parent, QName element) throws XmlBuilderException {
        final JsonElement parentElement = parent.get();
        if (parentElement.isJsonObject()) {
            final JsonObject parentObject = parentElement.getAsJsonObject();
            final String name = element.getLocalPart();
            if (null == parentObject.get(name)) {
                final GsonNode attributeNode = new GsonByNameNode(parentObject, name, parent);
                attributeNode.set(new JsonObject());
                return attributeNode;
            } else {
                final JsonArray jsonArray = new JsonArray();
                final JsonObject jsonObject = new JsonObject();
                jsonArray.add(parentObject);
                final GsonNode jsonObjectNode = new GsonByIndexNode(jsonArray, 1, parent);
                final GsonNode elementNode = new GsonByNameNode(jsonObject, name, jsonObjectNode);
                elementNode.set(new JsonObject());
                return elementNode;
            }
        } else if (parentElement.isJsonArray()) {
            final JsonArray parentArray = parentElement.getAsJsonArray();
            final JsonObject parentObject = new JsonObject();
            parentArray.add(parentObject);
            final GsonNode parentObjectNode = new GsonByIndexNode(parentArray, parentArray.size() - 1, parent);
            final GsonNode elementNode = new GsonByNameNode(parentObject, element.getLocalPart(), parentObjectNode);
            elementNode.set(new JsonObject());
            return elementNode;
        } else {
            throw new XmlBuilderException("Unable to create element for primitive node: " + parentElement);
        }
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
            final JsonArray jsonArray = new JsonArray();
            final JsonObject jsonObject = new JsonObject();
            final String name = node.getName().getLocalPart();
            jsonObject.add(name, elementToCopy.deepCopy());
            jsonArray.add(jsonObject);
            jsonArray.add(parentObject);
            parent.getParent().set(jsonArray);
            node.setParent(new GsonByNameNode(jsonObject, name, new GsonByIndexNode(jsonArray, 1, node)));
        } else if (parentElement.isJsonArray()) {
            final JsonArray jsonArray = parentElement.getAsJsonArray();
            int i = jsonArray.size();
            JsonElement arrayElement = jsonArray.get(i - 1);
            jsonArray.add(arrayElement);
            if (elementToCopy == arrayElement) {
                jsonArray.set(i - 1, elementToCopy.deepCopy());
            } else {
                for (; i > 0; --i) {
                    arrayElement = jsonArray.get(i - 1);
                    jsonArray.set(i, arrayElement);
                    if (elementToCopy == arrayElement) {
                        jsonArray.set(i - 1, elementToCopy.deepCopy());
                    }
                }
            }
        } else {
            throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentElement);
        }
    }

    @Override
    public void remove(GsonNode node) throws XmlBuilderException {
        node.remove();
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
