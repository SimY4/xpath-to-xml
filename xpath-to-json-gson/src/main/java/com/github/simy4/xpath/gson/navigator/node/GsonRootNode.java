package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.google.gson.JsonElement;

import javax.xml.namespace.QName;

public final class GsonRootNode extends AbstractGsonNode {

    private final JsonElement root;

    public GsonRootNode(JsonElement root) {
        super(null);
        this.root = root;
    }

    @Override
    public QName getName() {
        return new QName("_root_");
    }

    @Override
    public JsonElement get() {
        return root;
    }

    @Override
    public void set(JsonElement jsonElement) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to set to root element " + jsonElement);
    }

    @Override
    public void remove() throws XmlBuilderException {
        throw new XmlBuilderException("Unable to remove from root element");
    }

}
