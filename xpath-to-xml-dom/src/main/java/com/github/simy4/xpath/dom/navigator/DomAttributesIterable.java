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
package com.github.simy4.xpath.dom.navigator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomAttributesIterable implements Iterable<DomNode> {

  private final Node parent;

  DomAttributesIterable(Node parent) {
    this.parent = parent;
  }

  @Override
  public Iterator<DomNode> iterator() {
    return new DomAttributesIterator(parent);
  }

  private static final class DomAttributesIterator implements Iterator<DomNode> {

    private final NamedNodeMap attributes;
    private int cursor;

    DomAttributesIterator(Node parent) {
      attributes = parent.getAttributes();
    }

    @Override
    public boolean hasNext() {
      return cursor < attributes.getLength();
    }

    @Override
    public DomNode next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more attributes");
      }
      return new DomNode(attributes.item(cursor++));
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
