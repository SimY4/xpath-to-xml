package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;

import javax.json.JsonValue;
import javax.xml.namespace.QName;

public final class JavaxJsonRootNode extends AbstractJavaxJsonNode {

    private JsonValue root;

    public JavaxJsonRootNode(JsonValue root) {
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
    public void set(JsonValue jsonValue) {
        root = jsonValue;
    }

    @Override
    public void remove() throws XmlBuilderException {
        throw new XmlBuilderException("Unable to remove from root element");
    }

}