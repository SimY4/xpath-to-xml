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

import com.github.simy4.xpath.XmlBuilderException;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import javax.xml.namespace.QName;

public final class JakartaJsonRootNode extends AbstractJakartaJsonNode {

  private JsonValue root;

  public JakartaJsonRootNode(JsonValue root) {
    super(null);
    this.root = root;
  }

  @Override
  public QName getName() {
    return new QName(DOCUMENT);
  }

  @Override
  public JsonValue get() {
    return root;
  }

  @Override
  public void set(JsonProvider jsonProvider, JsonValue jsonValue) throws XmlBuilderException {
    if (null == jsonValue) {
      throw new XmlBuilderException("Unable to remove from root element");
    }
    root = jsonValue;
  }
}
