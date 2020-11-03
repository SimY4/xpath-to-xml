package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import javax.xml.namespace.QName;

public final class JakartaJsonRootNode extends AbstractJakartaJsonNode {

    private JsonValue root;

    public JakartaJsonRootNode(JsonValue root) {
        super(null);
        this.root = root;
    }

    @Override
    public QName getName() {
        return new QName(DOCUMENT);
    }

    @Override
    public JsonValue get() {
        return root;
    }

    @Override
    public void set(JsonProvider jsonProvider, JsonValue jsonValue) throws XmlBuilderException {
        if (null == jsonValue) {
            throw new XmlBuilderException("Unable to remove from root element");
        }
        root = jsonValue;
    }

}
