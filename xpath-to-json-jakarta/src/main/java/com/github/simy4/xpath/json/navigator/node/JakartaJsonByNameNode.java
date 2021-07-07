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

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import javax.xml.namespace.QName;

public final class JakartaJsonByNameNode extends AbstractJakartaJsonNode {

  private final String name;

  /**
   * Constructor.
   *
   * @param name json object key
   * @param parent parent node
   */
  public JakartaJsonByNameNode(String name, JakartaJsonNode parent) {
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
    final JsonObject newJsonObject =
        null == jsonValue
            ? objectBuilder.remove(name).build()
            : objectBuilder.add(name, jsonValue).build();
    getParent().set(jsonProvider, newJsonObject);
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    JakartaJsonByNameNode javaxJsonNodes = (JakartaJsonByNameNode) o;
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
