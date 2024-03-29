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
package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Node;

import java.io.Serializable;

abstract class AbstractDom4jNode<N extends Node> implements Dom4jNode, Serializable {

  private static final long serialVersionUID = 1L;

  private final N node;

  protected AbstractDom4jNode(N node) {
    this.node = node;
  }

  @Override
  public final N getNode() {
    return node;
  }

  @Override
  public final String getText() {
    return node.getText();
  }

  @Override
  public final boolean equals(Object o) {
    return (this == o
        || (o instanceof AbstractDom4jNode && node.equals(((AbstractDom4jNode<?>) o).node)));
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
