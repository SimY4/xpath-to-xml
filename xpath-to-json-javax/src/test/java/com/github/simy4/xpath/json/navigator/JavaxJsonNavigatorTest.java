package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.junit.jupiter.api.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaxJsonNavigatorTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonObject json = jsonProvider.createObjectBuilder().build();
    private final JavaxJsonNode root = new JavaxJsonRootNode(json);
    private final Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(jsonProvider, root);

    @Test
    void shouldReturnRoot() {
        assertThat(navigator.root()).isEqualTo(root);
    }

    @Test
    void shouldReturnNullParentForRoot() {
        assertThat(navigator.parentOf(root)).isNull();
    }

    @Test
    void shouldReturnParentForElementChild() {
        json.put("child", jsonProvider.createValue("zero"));
        JavaxJsonNode childNode = new JavaxJsonByNameNode(json, "child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForArrayChild() {
        JsonArray json = jsonProvider.createArrayBuilder()
                .add("zero")
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);
        JavaxJsonNode childNode = new JavaxJsonByIndexNode(json, 0, root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForNestedArrayChild() {
        JsonArray child = jsonProvider.createArrayBuilder()
                .add("zero")
                .build();
        JsonArray json = jsonProvider.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);
        JavaxJsonNode array1Node = new JavaxJsonByIndexNode(json, 0, root);
        JavaxJsonNode array2Node = new JavaxJsonByIndexNode(child, 0, array1Node);

        assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
    }

    @Test
    void shouldSetTextForElementChild() {
        navigator.setText(root, "test");

        assertThat(json.get("text")).isEqualTo(jsonProvider.createValue("test"));
    }

    @Test
    void shouldSetTextForArrayChild() {
        JsonArray json = jsonProvider.createArrayBuilder().build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);

        assertThatThrownBy(() -> navigator.setText(root, "test"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetTextForPrimitiveChild() {
        json.put("child", jsonProvider.createValue("zero"));
        JavaxJsonNode childNode = new JavaxJsonByNameNode(json, "child", root);
        navigator.setText(childNode, "test");

        assertThat(json.get("child")).isEqualTo(jsonProvider.createValue("zero"));
    }

    @Test
    void shouldCreateElementForElementParent() {
        JavaxJsonNode child = navigator.createElement(root, new QName("child"));

        assertThat(child).isEqualTo(new JavaxJsonByNameNode(json, "child", root));
        assertThat(json.get("child")).isEqualTo(JsonValue.EMPTY_JSON_OBJECT);
    }

    @Test
    void shouldCreateElementForNestedObjectInArrayChild() {
        JsonObject child = jsonProvider.createObjectBuilder()
                .add("child", jsonProvider.createObjectBuilder().build())
                .build();
        JsonArray json = jsonProvider.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);
        JavaxJsonNode objectNode = new JavaxJsonByIndexNode(json, 0, root);

        JavaxJsonNode newChild = navigator.createElement(objectNode, new QName("child"));

        JsonObject expected = jsonProvider.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JavaxJsonByNameNode(expected, "child", new JavaxJsonByIndexNode(json, 1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateElementForArrayParent() {
        JsonArray json = jsonProvider.createArrayBuilder().build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);

        JavaxJsonNode child = navigator.createElement(root, new QName("child"));

        JsonObject expected = jsonProvider.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        assertThat(child).isEqualTo(new JavaxJsonByNameNode(expected, "child", new JavaxJsonByIndexNode(json, 0, root)));
        assertThat(json.get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateElementForPrimitiveParent() {
        json.put("child", jsonProvider.createValue("zero"));
        JavaxJsonNode childNode = new JavaxJsonByNameNode(json, "child", root);

        assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldCreateAttributeForElementParent() {
        JavaxJsonNode child = navigator.createAttribute(root, new QName("child"));

        assertThat(child).isEqualTo(new JavaxJsonByNameNode(json, "child", root));
        assertThat(json.get("child")).isEqualTo(jsonProvider.createValue(""));
    }

    @Test
    void shouldCreateAttributeForNestedObjectInArrayChild() {
        JsonObject child = jsonProvider.createObjectBuilder()
                .add("child", jsonProvider.createValue(""))
                .build();
        JsonArray json = jsonProvider.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);
        JavaxJsonNode objectNode = new JavaxJsonByIndexNode(json, 0, root);

        JavaxJsonNode newChild = navigator.createAttribute(objectNode, new QName("child"));

        JsonObject expected = jsonProvider.createObjectBuilder()
                .add("child", jsonProvider.createValue(""))
                .build();
        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JavaxJsonByNameNode(expected, "child", new JavaxJsonByIndexNode(json, 1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateAttributeForArrayParent() {
        JsonArray json = jsonProvider.createArrayBuilder().build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(jsonProvider, root);

        JavaxJsonNode child = navigator.createAttribute(root, new QName("child"));

        JsonObject expected = jsonProvider.createObjectBuilder()
                .add("child", jsonProvider.createValue(""))
                .build();
        assertThat(child).isEqualTo(new JavaxJsonByNameNode(expected, "child", new JavaxJsonByIndexNode(json, 0, root)));
        assertThat(json.get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateAttributeForPrimitiveParent() {
        json.put("child", jsonProvider.createValue("zero"));
        JavaxJsonNode childNode = new JavaxJsonByNameNode(json, "child", root);

        assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

}