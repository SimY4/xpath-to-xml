package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonRootNode;
import jakarta.json.Json;
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
        var root = new JakartaJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThat(navigator.root()).isEqualTo(root);
    }

    @Test
    void shouldReturnNullParentForRoot() {
        var root = new JakartaJsonRootNode(JsonValue.EMPTY_JSON_OBJECT);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThat(navigator.parentOf(root)).isNull();
    }

    @Test
    void shouldReturnParentForElementChild() {
        var json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var childNode = new JakartaJsonByNameNode("child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForArrayChild() {
        var json = Json.createArrayBuilder()
                .add("zero")
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var childNode = new JakartaJsonByIndexNode(0, root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForNestedArrayChild() {
        var child = Json.createArrayBuilder()
                .add("zero")
                .build();
        var json = Json.createArrayBuilder()
                .add(child)
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var array1Node = new JakartaJsonByIndexNode(0, root);
        var array2Node = new JakartaJsonByIndexNode(0, array1Node);

        assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
    }

    @Test
    void shouldSetTextForElementChild() {
        var json = JsonValue.EMPTY_JSON_OBJECT;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        navigator.setText(root, "test");

        assertThat(root.get().asJsonObject().get("text")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldSetTextForArrayChild() {
        var json = JsonValue.EMPTY_JSON_ARRAY;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        assertThatThrownBy(() -> navigator.setText(root, "test"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetTextForPrimitiveChild() {
        var json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var childNode = new JakartaJsonByNameNode("child", root);

        navigator.setText(childNode, "test");

        assertThat(root.get().asJsonObject().get("child")).isEqualTo(Json.createValue("test"));
    }

    @Test
    void shouldCreateElementForElementParent() {
        var json = JsonValue.EMPTY_JSON_OBJECT;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        var child = navigator.createElement(root, new QName("child"));

        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", root));
        assertThat(root.get().asJsonObject().get("child")).isEqualTo(JsonValue.EMPTY_JSON_OBJECT);
    }

    @Test
    void shouldCreateElementForNestedObjectInArrayChild() {
        var child = Json.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        var json = Json.createArrayBuilder()
                .add(child)
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var objectNode = new JakartaJsonByIndexNode(0, root);

        var newChild = navigator.createElement(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateElementForArrayParent() {
        var json = JsonValue.EMPTY_JSON_ARRAY;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        var child = navigator.createElement(root, new QName("child"));

        var expected = Json.createObjectBuilder()
                .add("child", JsonValue.EMPTY_JSON_OBJECT)
                .build();
        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateElementForPrimitiveParent() {
        var json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var childNode = new JakartaJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldCreateAttributeForElementParent() {
        var json = JsonValue.EMPTY_JSON_OBJECT;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        var child = navigator.createAttribute(root, new QName("child"));

        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", root));
        assertThat(root.get().asJsonObject().get("child")).isEqualTo(Json.createValue(""));
    }

    @Test
    void shouldCreateAttributeForNestedObjectInArrayChild() {
        var child = Json.createObjectBuilder()
                .add("child", Json.createValue(""))
                .build();
        var json = Json.createArrayBuilder()
                .add(child)
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);
        var objectNode = new JakartaJsonByIndexNode(0, root);

        var newChild = navigator.createAttribute(objectNode, new QName("child"));

        assertThat(newChild)
                .isNotSameAs(child)
                .isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(1, root)));
        assertThat(objectNode.get()).isSameAs(child);
    }

    @Test
    void shouldCreateAttributeForArrayParent() {
        var json = JsonValue.EMPTY_JSON_ARRAY;
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        var child = navigator.createAttribute(root, new QName("child"));

        var expected = Json.createObjectBuilder()
                .add("child", Json.createValue(""))
                .build();
        assertThat(child).isEqualTo(new JakartaJsonByNameNode("child", new JakartaJsonByIndexNode(0, root)));
        assertThat(root.get().asJsonArray().get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateAttributeForPrimitiveParent() {
        var json = Json.createObjectBuilder()
                .add("child", Json.createValue("zero"))
                .build();
        var root = new JakartaJsonRootNode(json);
        var navigator = new JakartaJsonNavigator(jsonProvider, root);

        var childNode = new JakartaJsonByNameNode("child", root);

        assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

}