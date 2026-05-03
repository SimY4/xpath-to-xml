/*
 * Copyright 2018-2026 Alex Simkin
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
package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.namespace.QName;

public final class GsonByNameNode extends AbstractGsonNode {

  private final QName name;

  /**
   * Constructor.
   *
   * @param name json object key
   * @param parent parent node
   */
  public GsonByNameNode(QName name, GsonNode parent) {
    super(parent);
    this.name = name;
  }

  @Override
  public QName getName() {
    return name;
  }

  @Override
  public JsonElement get() {
    return getParent().get().getAsJsonObject().get(name.toString());
  }

  @Override
  public void set(JsonElement jsonElement) {
    final JsonObject parentObject = getParent().get().getAsJsonObject();
    if (null == jsonElement) {
      parentObject.remove(name.getLocalPart());
    } else {
      parentObject.add(name.getLocalPart(), jsonElement);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    GsonByNameNode byNameNode = (GsonByNameNode) o;
    return getParent().equals(byNameNode.getParent());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + getParent().hashCode();
    return result;
  }
}
