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
package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.namespace.QName;

public final class JacksonByNameNode extends AbstractJacksonNode {

  private final ObjectNode parentObject;
  private final String name;

  /**
   * Constructor.
   *
   * @param parentObject parent json object element
   * @param name json object key
   * @param parent parent node
   */
  public JacksonByNameNode(ObjectNode parentObject, String name, JacksonNode parent) {
    super(parent);
    this.parentObject = parentObject;
    this.name = name;
  }

  @Override
  public QName getName() {
    return new QName(name);
  }

  @Override
  public JsonNode get() {
    return parentObject.get(name);
  }

  @Override
  public void set(JsonNode jsonElement) {
    if (null == jsonElement) {
      parentObject.remove(name);
    } else {
      parentObject.set(name, jsonElement);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    var jacksonNodes = (JacksonByNameNode) o;
    return getParent().equals(jacksonNodes.getParent());
  }

  @Override
  public int hashCode() {
    var result = super.hashCode();
    result = 31 * result + getParent().hashCode();
    return result;
  }
}
