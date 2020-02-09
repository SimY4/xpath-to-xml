package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.xml.namespace.QName;

public final class JavaxJsonByIndexNode extends AbstractJavaxJsonNode {

    private final int index;

    /**
     * Constructor.
     *
     * @param index       json array index
     * @param parent      parent node
     */
    public JavaxJsonByIndexNode(int index, JavaxJsonNode parent) {
        super(parent);
        this.index = index;
    }

    @Override
    public QName getName() {
        return new QName("array[" + index + ']');
    }

    @Override
    public JsonValue get() {
        return getParentArray().get(index);
    }

    @Override
    public void set(JsonProvider jsonProvider, JsonValue jsonValue) {
        final JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder(getParentArray());
        final JsonArray newJsonArray = null == jsonValue
                ? arrayBuilder.remove(index).build()
                : arrayBuilder.set(index, jsonValue).build();
        getParent().set(jsonProvider, newJsonArray);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        var javaxJsonNodes = (JavaxJsonByIndexNode) o;
        return index == javaxJsonNodes.index;
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + index;
        return result;
    }

    public int getIndex() {
        return index;
    }

    private JsonArray getParentArray() {
        return getParent().get().asJsonArray();
    }

}
