/*
 * Copyright 2017-2021 Alex Simkin
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
package com.github.simy4.xpath.jdom.navigator.node;

import java.io.Serializable;

abstract class AbstractJDomNode<N extends Serializable> implements JDomNode, Serializable {

  private static final long serialVersionUID = 1L;

  private final N node;

  protected AbstractJDomNode(N node) {
    this.node = node;
  }

  protected final N getNode() {
    return node;
  }

  @Override
  public final boolean equals(Object o) {
    return (this == o
        || (o instanceof AbstractJDomNode && node.equals(((AbstractJDomNode<?>) o).node)));
  }

  @Override
  public final int hashCode() {
    return node.hashCode();
  }

  @Override
  public String toString() {
    return node.toString();
  }
}
