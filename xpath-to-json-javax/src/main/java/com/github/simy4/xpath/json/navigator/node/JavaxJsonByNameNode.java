package com.github.simy4.xpath.json.navigator.node;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.namespace.QName;

public final class JavaxJsonByNameNode extends AbstractJavaxJsonNode {

    private final String name;

    /**
     * Constructor.
     *
     * @param name         json object key
     * @param parent       parent node
     */
    public JavaxJsonByNameNode(String name, JavaxJsonNode parent) {
        super(parent);
        this.name = name;
    }

    @Override
    public QName getName() {
        return new QName(name);
    }

    @Override
    public JsonValue get() {
        return getParentObject().get(name);
    }

    @Override
    public void set(JsonValue jsonValue) {
        final JsonObject parentObject = getParentObject();
        try {
            parentObject.put(name, jsonValue);
        } catch (UnsupportedOperationException uoe) {
            getParent().set(Json.createObjectBuilder(parentObject)
                    .add(name, jsonValue)
                    .build());
        }
    }

    @Override
    public void remove() {
        final JsonObject parentObject = getParentObject();
        try {
            parentObject.remove(name);
        } catch (UnsupportedOperationException uoe) {
            getParent().set(Json.createObjectBuilder(parentObject)
                    .remove(name)
                    .build());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        JavaxJsonByNameNode javaxJsonNodes = (JavaxJsonByNameNode) o;
        return getParent().equals(javaxJsonNodes.getParent());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

    private JsonObject getParentObject() {
        return getParent().get().asJsonObject();
    }

}
