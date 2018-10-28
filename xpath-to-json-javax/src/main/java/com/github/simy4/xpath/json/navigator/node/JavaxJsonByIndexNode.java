package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonArray;
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
        final JsonArray parentArray = getParentArray();
        try {
            parentArray.set(index, jsonValue);
        } catch (UnsupportedOperationException uoe) {
            getParent().set(jsonProvider, jsonProvider.createArrayBuilder(parentArray)
                    .set(index, jsonValue)
                    .build());
        }
    }

    @Override
    public void remove(JsonProvider jsonProvider) {
        final JsonArray parentArray = getParentArray();
        try {
            parentArray.remove(index);
        } catch (UnsupportedOperationException uoe) {
            getParent().set(jsonProvider, jsonProvider.createArrayBuilder(parentArray)
                    .remove(index)
                    .build());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JavaxJsonByIndexNode javaxJsonNodes = (JavaxJsonByIndexNode) o;
        return index == javaxJsonNodes.index;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
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
