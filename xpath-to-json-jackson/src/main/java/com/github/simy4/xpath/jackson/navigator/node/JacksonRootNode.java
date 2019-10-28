package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.simy4.xpath.XmlBuilderException;

import javax.xml.namespace.QName;

public final class JacksonRootNode extends AbstractJacksonNode {

    private JsonNode root;

    public JacksonRootNode(JsonNode root) {
        super(null);
        this.root = root;
    }

    @Override
    public QName getName() {
        return new QName(DOCUMENT);
    }

    @Override
    public JsonNode get() {
        return root;
    }

    @Override
    public void set(JsonNode jsonElement) {
        if (null == jsonElement) {
            throw new XmlBuilderException("Unable to remove from root element");
        }
        root = jsonElement;
    }

}
