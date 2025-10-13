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

import org.json.JSONObject;

import javax.xml.namespace.QName;

public final class JsonJsonByNameNode extends AbstractJsonJsonNode {

  private final QName name;

  /**
   * Constructor.
   *
   * @param name json object key
   * @param parent parent node
   */
  public JsonJsonByNameNode(QName name, JsonJsonNode parent) {
    super(parent);
    this.name = name;
  }

  @Override
  public QName getName() {
    return name;
  }

  @Override
  public Object get() {
    return getParentObject().get(name.getLocalPart());
  }

  @Override
  public void set(Object jsonValue) {
    final JSONObject object = getParentObject();
    if (null == jsonValue) {
      object.remove(name.getLocalPart());
    } else {
      object.put(name.getLocalPart(), jsonValue);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!super.equals(o)) {
      return false;
    }

    JsonJsonByNameNode javaxJsonNodes = (JsonJsonByNameNode) o;
    return getParent().equals(javaxJsonNodes.getParent());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + getParent().hashCode();
    return result;
  }

  private JSONObject getParentObject() {
    return (JSONObject) getParent().get();
  }
}
