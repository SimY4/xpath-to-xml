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
package com.github.simy4.xpath.util;

import com.github.simy4.xpath.navigator.Node;

import javax.xml.namespace.QName;

import java.io.Serializable;

public final class TestNode implements Node, Serializable {

  private static final long serialVersionUID = 1L;

  private final QName value;

  public static TestNode node(String value) {
    return node(new QName(value));
  }

  public static TestNode node(QName value) {
    return new TestNode(value);
  }

  private TestNode(QName value) {
    this.value = value;
  }

  @Override
  public QName getName() {
    return value;
  }

  @Override
  public String getText() {
    return value.getLocalPart();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TestNode that = (TestNode) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return getName().toString() + ':' + getText();
  }
}
