/*
 * Copyright 2018-2025 Alex Simkin
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
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.xml.namespace.QName;

public final class JacksonByIndexNode extends AbstractJacksonNode {

  private final int index;

  /**
   * Constructor.
   *
   * @param index json array index
   * @param parent parent node
   */
  public JacksonByIndexNode(int index, JacksonNode parent) {
    super(parent);
    this.index = index;
  }

  @Override
  public QName getName() {
    return QName.valueOf("array[" + index + ']');
  }

  @Override
  public JsonNode get() {
    return getParent().get().get(index);
  }

  @Override
  public void set(JsonNode jsonElement) {
    final ArrayNode parentArray = getParentArray();
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

    JacksonByIndexNode jacksonNodes = (JacksonByIndexNode) o;
    return index == jacksonNodes.index;
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

  private ArrayNode getParentArray() {
    return (ArrayNode) getParent().get();
  }
}
