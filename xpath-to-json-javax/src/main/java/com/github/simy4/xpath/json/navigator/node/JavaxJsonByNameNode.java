package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.namespace.QName;

public final class JavaxJsonByNameNode extends AbstractJavaxJsonNode {

    private final JsonObject parentObject;
    private final String name;

    /**
     * Constructor.
     *
     * @param parentObject parent json object element
     * @param name         json object key
     * @param parent       parent node
     */
    public JavaxJsonByNameNode(JsonObject parentObject, String name, JavaxJsonNode parent) {
        super(parent);
        this.parentObject = parentObject;
        this.name = name;
    }

    @Override
    public QName getName() {
        return new QName(name);
    }

    @Override
    public JsonValue get() {
        return parentObject.get(name);
    }

    @Override
    public void set(JsonValue jsonValue) {
        parentObject.put(name, jsonValue);
    }

    @Override
    public void remove() {
        parentObject.remove(name);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JavaxJsonByNameNode gsonNodes = (JavaxJsonByNameNode) o;
        return getParent().equals(gsonNodes.getParent());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

}
