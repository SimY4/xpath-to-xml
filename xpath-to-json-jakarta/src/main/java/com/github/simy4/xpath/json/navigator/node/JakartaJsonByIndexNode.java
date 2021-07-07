/*
 * Copyright 2018-2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.json.navigator.node;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import javax.xml.namespace.QName;

public final class JakartaJsonByIndexNode extends AbstractJakartaJsonNode {

  private final int index;

  /**
   * Constructor.
   *
   * @param index json array index
   * @param parent parent node
   */
  public JakartaJsonByIndexNode(int index, JakartaJsonNode parent) {
    super(parent);
    this.index = index;
  }

  @Override
  public QName getName() {
    return new QName("array[" + index + ']');
  }

  @Override
  public JsonValue get() {
    return getParentArray().get(index);
  }

  @Override
  public void set(JsonProvider jsonProvider, JsonValue jsonValue) {
    final JsonArrayBuilder arrayBuilder = jsonProvider.createArrayBuilder(getParentArray());
    final JsonArray newJsonArray =
        null == jsonValue
            ? arrayBuilder.remove(index).build()
            : arrayBuilder.set(index, jsonValue).build();
    getParent().set(jsonProvider, newJsonArray);
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    JakartaJsonByIndexNode javaxJsonNodes = (JakartaJsonByIndexNode) o;
    return index == javaxJsonNodes.index;
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

  private JsonArray getParentArray() {
    return getParent().get().asJsonArray();
  }
}
