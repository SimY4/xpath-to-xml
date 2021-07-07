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

import org.w3c.dom.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class DomElementsIterable implements Iterable<DomNode> {

  private final Node parent;

  DomElementsIterable(Node parent) {
    this.parent = parent;
  }

  @Override
  public Iterator<DomNode> iterator() {
    return new DomElementsIterator(parent);
  }

  private static final class DomElementsIterator implements Iterator<DomNode> {

    private Node child;

    private DomElementsIterator(Node parent) {
      this.child = nextElement(parent.getFirstChild());
    }

    @Override
    public boolean hasNext() {
      return null != child;
    }

    @Override
    public DomNode next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements");
      }
      final Node next = child;
      child = nextElement(next.getNextSibling());
      return new DomNode(next);
    }

    private Node nextElement(Node next) {
      while (next != null && Node.ELEMENT_NODE != next.getNodeType()) {
        next = next.getNextSibling();
      }
      return next;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
