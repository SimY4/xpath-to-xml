package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class GsonByNameNodeTest {

  private final JsonObject jsonObject = new JsonObject();
  private final GsonNode byNameNode = new GsonByNameNode(jsonObject, "two", null);

  @BeforeEach
  void setUp() {
    jsonObject.addProperty("one", 1);
    jsonObject.addProperty("two", 2);
    jsonObject.addProperty("three", 3);
  }

  @Test
  void shouldRetrieveElementByIndexOnGet() {
    assertThat(byNameNode.get()).isEqualTo(new JsonPrimitive(2));
  }

  @Test
  void shouldSetElementByIndexOnSet() {
    byNameNode.set(new JsonPrimitive(4));

    assertThat(jsonObject.entrySet())
        .containsExactly(
            entry("one", new JsonPrimitive(1)),
            entry("two", new JsonPrimitive(4)),
            entry("three", new JsonPrimitive(3)));
  }

  @Test
  void shouldRemoveElementByIndexOnSetNull() {
    byNameNode.set(null);

    assertThat(jsonObject.entrySet())
        .containsExactly(entry("one", new JsonPrimitive(1)), entry("three", new JsonPrimitive(3)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldTraverseObjectAttributes() {
    GsonNode parent = new GsonRootNode(jsonObject);

    assertThat((Iterable<GsonNode>) parent.attributes())
        .containsExactlyInAnyOrder(
            new GsonByNameNode(jsonObject, "one", parent),
            new GsonByNameNode(jsonObject, "two", parent),
            new GsonByNameNode(jsonObject, "three", parent));
  }

  @Test
  void shouldTraverseObjectElements() {
    GsonNode parent = new GsonRootNode(jsonObject);

    assertThat((Iterable<?>) parent.elements()).isEmpty();
  }
}
