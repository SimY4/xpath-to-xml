package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
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
    public void set(JsonProvider jsonProvider, JsonValue jsonValue) {
        final JsonObjectBuilder objectBuilder = jsonProvider.createObjectBuilder(getParentObject());
        final JsonObject newJsonObject = null == jsonValue
                ? objectBuilder.remove(name).build()
                : objectBuilder.add(name, jsonValue).build();
        getParent().set(jsonProvider, newJsonObject);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        var javaxJsonNodes = (JavaxJsonByNameNode) o;
        return getParent().equals(javaxJsonNodes.getParent());
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + getParent().hashCode();
        return result;
    }

    private JsonObject getParentObject() {
        return getParent().get().asJsonObject();
    }

}
