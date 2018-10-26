package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonNode;
import com.github.simy4.xpath.json.navigator.node.JavaxJsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaxJsonNavigatorTest {

    @Test
    void shouldReturnRoot() {
        JavaxJsonNode root = new JavaxJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);

        assertThat(navigator.root()).isEqualTo(root);
    }

    @Test
    void shouldReturnNullParentForRoot() {
        JavaxJsonNode root = new JavaxJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);

        assertThat(navigator.parentOf(root)).isNull();
    }

    @Test
    void shouldReturnParentForElementChild() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode childNode = new JavaxJsonByNameNode("child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForArrayChild() {
        JsonArray json = Json.createArrayBuilder()
                .add("zero")
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode childNode = new JavaxJsonByIndexNode(0, root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForNestedArrayChild() {
        JsonArray child = Json.createArrayBuilder()
                .add("zero")
                .build();
        JsonArray json = Json.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode array1Node = new JavaxJsonByIndexNode(0, root);
        JavaxJsonNode array2Node = new JavaxJsonByIndexNode(0, array1Node);

        assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
    }

    @Test
    void shouldSetTextForElementChild() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);

        navigator.setText(root, "test");

        assertThat(root.get().asJsonObject().get("text")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldSetTextForArrayChild() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        assertThatThrownBy(() -> navigator.setText(root, "test"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetTextForPrimitiveChild() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode childNode = new JavaxJsonByNameNode("child", root);

        navigator.setText(childNode, "test");

        assertThat(root.get().asJsonObject().get("child")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldCreateElementForElementParent() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        JavaxJsonNode child = navigator.createElement(root, new QName("child"));

        assertThat(child).isEqualTo(new JavaxJsonByNameNode("child", root));
        assertThat(root.get().asJsonObject().get("child")).isEqualTo(JsonValue.EMPTY_JSON_OBJECT);
    }

    @Test
    void shouldCreateElementForNestedObjectInArrayChild() {
        JsonObject child = Json.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        JsonArray json = Json.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode objectNode = new JavaxJsonByIndexNode(0, root);

        JavaxJsonNode newChild = navigator.createElement(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JavaxJsonByNameNode("child", new JavaxJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateElementForArrayParent() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        JavaxJsonNode child = navigator.createElement(root, new QName("child"));

        JsonObject expected = Json.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        assertThat(child).isEqualTo(new JavaxJsonByNameNode("child", new JavaxJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateElementForPrimitiveParent() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        Navigator<JavaxJsonNode> navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode childNode = new JavaxJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldCreateAttributeForElementParent() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        JavaxJsonNode child = navigator.createAttribute(root, new QName("child"));

        assertThat(child).isEqualTo(new JavaxJsonByNameNode("child", root));
        assertThat(root.get().asJsonObject().get("child")).isEqualTo(Json.createValue(""));
    }

    @Test
    void shouldCreateAttributeForNestedObjectInArrayChild() {
        JsonObject child = Json.createObjectBuilder()
                .add("child", Json.createValue(""))
                .build();
        JsonArray json = Json.createArrayBuilder()
                .add(child)
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);
        JavaxJsonNode objectNode = new JavaxJsonByIndexNode(0, root);

        JavaxJsonNode newChild = navigator.createAttribute(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JavaxJsonByNameNode("child", new JavaxJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateAttributeForArrayParent() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        JavaxJsonNode child = navigator.createAttribute(root, new QName("child"));

        JsonObject expected = Json.createObjectBuilder()
                .add("child", Json.createValue(""))
                .build();
        assertThat(child).isEqualTo(new JavaxJsonByNameNode("child", new JavaxJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateAttributeForPrimitiveParent() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JavaxJsonNode root = new JavaxJsonRootNode(json);
        JavaxJsonNavigator navigator = new JavaxJsonNavigator(root);

        JavaxJsonNode childNode = new JavaxJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

}