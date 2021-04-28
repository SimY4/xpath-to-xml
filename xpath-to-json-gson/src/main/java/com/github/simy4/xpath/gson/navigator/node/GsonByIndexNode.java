package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import javax.xml.namespace.QName;

public final class GsonByIndexNode extends AbstractGsonNode {

  private final JsonArray parentArray;
  private final int index;

  /**
   * Constructor.
   *
   * @param parentArray parent json array element
   * @param index json array index
   * @param parent parent node
   */
  public GsonByIndexNode(JsonArray parentArray, int index, GsonNode parent) {
    super(parent);
    this.parentArray = parentArray;
    this.index = index;
  }

  @Override
  public QName getName() {
    return new QName("array[" + index + ']');
  }

  @Override
  public JsonElement get() {
    return parentArray.get(index);
  }

  @Override
  public void set(JsonElement jsonElement) {
    if (null == jsonElement) {
      parentArray.remove(index);
    } else {
      parentArray.set(index, jsonElement);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    GsonByIndexNode gsonNodes = (GsonByIndexNode) o;
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
