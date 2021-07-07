/*
 * Copyright 2021 Alex Simkin
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
package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;

import javax.xml.namespace.QName;

import java.io.Serializable;

public final class DomNode implements Node, Serializable {

  private static final long serialVersionUID = 1L;

  private final org.w3c.dom.Node node;

  public DomNode(org.w3c.dom.Node node) {
    this.node = node;
  }

  org.w3c.dom.Node getNode() {
    return node;
  }

  @Override
  public QName getName() {
    final String localPart = node.getLocalName();
    if (null == localPart) {
      return new QName(node.getNodeName());
    } else {
      return new QName(node.getNamespaceURI(), localPart, node.getPrefix());
    }
  }

  @Override
  public String getText() {
    return node.getTextContent();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DomNode that = (DomNode) o;

    return node.equals(that.node);
  }

  @Override
  public int hashCode() {
    return node.hashCode();
  }

  @Override
  public String toString() {
    return node.toString();
  }
}
