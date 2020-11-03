package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JakartaJsonNavigatorTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    @Test
    void shouldReturnRoot() {
        JakartaJsonNode root = new JakartaJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThat(navigator.root()).isEqualTo(root);
    }

    @Test
    void shouldReturnNullParentForRoot() {
        JakartaJsonNode root = new JakartaJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThat(navigator.parentOf(root)).isNull();
    }

    @Test
    void shouldReturnParentForElementChild() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode childNode = new JakartaJsonByNameNode("child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForArrayChild() {
        JsonArray json = Json.createArrayBuilder()
                .add("zero")
                .build();
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode childNode = new JakartaJsonByIndexNode(0, root);

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
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode array1Node = new JakartaJsonByIndexNode(0, root);
        JakartaJsonNode array2Node = new JakartaJsonByIndexNode(0, array1Node);

        assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
    }

    @Test
    void shouldSetTextForElementChild() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);

        navigator.setText(root, "test");

        assertThat(root.get().asJsonObject().get("text")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldSetTextForArrayChild() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThatThrownBy(() -> navigator.setText(root, "test"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetTextForPrimitiveChild() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode childNode = new JakartaJsonByNameNode("child", root);

        navigator.setText(childNode, "test");

        assertThat(root.get().asJsonObject().get("child")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldCreateElementForElementParent() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        JakartaJsonNode child = navigator.createElement(root, new QName("child"));

        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", root));
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
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode objectNode = new JakartaJsonByIndexNode(0, root);

        JakartaJsonNode newChild = navigator.createElement(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateElementForArrayParent() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        JakartaJsonNode child = navigator.createElement(root, new QName("child"));

        JsonObject expected = Json.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateElementForPrimitiveParent() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        Navigator<JakartaJsonNode> navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode childNode = new JakartaJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldCreateAttributeForElementParent() {
        JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        JakartaJsonNode child = navigator.createAttribute(root, new QName("child"));

        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", root));
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
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);
        JakartaJsonNode objectNode = new JakartaJsonByIndexNode(0, root);

        JakartaJsonNode newChild = navigator.createAttribute(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateAttributeForArrayParent() {
        JsonArray json = JsonValue.EMPTY_JSON_ARRAY;
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        JakartaJsonNode child = navigator.createAttribute(root, new QName("child"));

        JsonObject expected = Json.createObjectBuilder()
                .add("child", Json.createValue(""))
                .build();
        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateAttributeForPrimitiveParent() {
        JsonObject json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        JakartaJsonNode root = new JakartaJsonRootNode(json);
        JakartaJsonNavigator navigator = new JakartaJsonNavigator(jsonProvider, root);

        JakartaJsonNode childNode = new JakartaJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

}