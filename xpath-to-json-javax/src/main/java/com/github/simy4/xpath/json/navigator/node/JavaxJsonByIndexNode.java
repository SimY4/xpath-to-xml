package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.xml.namespace.QName;

public final class JavaxJsonByIndexNode extends AbstractJavaxJsonNode {

    private final JsonArray parentArray;
    private final int index;

    /**
     * Constructor.
     *
     * @param parentArray parent json array element
     * @param index       json array index
     * @param parent      parent node
     */
    public JavaxJsonByIndexNode(JsonArray parentArray, int index, JavaxJsonNode parent) {
        super(parent);
        this.parentArray = parentArray;
        this.index = index;
    }

    @Override
    public QName getName() {
        return new QName("array[" + index + ']');
    }

    @Override
    public JsonValue get() {
        return parentArray.get(index);
    }

    @Override
    public void set(JsonValue jsonValue) {
        parentArray.set(index, jsonValue);
    }

    @Override
    public void remove() {
        parentArray.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JavaxJsonByIndexNode gsonNodes = (JavaxJsonByIndexNode) o;
        return index == gsonNodes.index;
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

}
