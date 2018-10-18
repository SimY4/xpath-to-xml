package com.github.simy4.xpath.json.navigator.node;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.xml.namespace.QName;

public final class JavaxJsonByNameNode extends AbstractJavaxJsonNode {

    private final String name;

    private JsonObject parentObject;

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
    public void set(JsonProvider jsonProvider, JsonValue jsonValue) {
        parentObject = jsonProvider.createObjectBuilder(parentObject)
                .add(name, jsonValue)
                .build();
        getParent().set(jsonProvider, parentObject);
    }

    @Override
    public void remove(JsonProvider jsonProvider) {
        parentObject = jsonProvider.createObjectBuilder(parentObject)
                .remove(name)
                .build();
        getParent().set(jsonProvider, parentObject);
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
