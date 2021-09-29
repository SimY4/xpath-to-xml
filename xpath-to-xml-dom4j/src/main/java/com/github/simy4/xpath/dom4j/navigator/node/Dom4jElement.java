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

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.IllegalAddException;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;

import java.util.Iterator;

public final class Dom4jElement extends AbstractDom4jNode<Element> {

  private static final long serialVersionUID = 1L;

  public Dom4jElement(Element element) {
    super(element);
  }

  @Override
  public QName getName() {
    final Namespace namespace = getNode().getNamespace();
    return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
  }

  @Override
  public Dom4jNode getParent() {
    final Element node = getNode();
    final Element parent = node.getParent();
    return null == parent
        ? node.getDocument().getRootElement() == node ? new Dom4jDocument(node.getDocument()) : null
        : new Dom4jElement(parent);
  }

  @Override
  public Iterable<Dom4jElement> elements() {
    return () -> new Dom4jElementIterator(getNode().elementIterator());
  }

  @Override
  public Iterable<Dom4jAttribute> attributes() {
    return () -> new Dom4jAttributeIterator(getNode().attributeIterator());
  }

  @Override
  public void appendChild(Dom4jNode child) throws XmlBuilderException {
    try {
      getNode().add(child.getNode());
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append child to node " + getNode(), iae);
    }
  }

  private static final class Dom4jAttributeIterator implements Iterator<Dom4jAttribute> {

    private final Iterator<Attribute> attributeIterator;

    private Dom4jAttributeIterator(Iterator<Attribute> attributeIterator) {
      this.attributeIterator = attributeIterator;
    }

    @Override
    public boolean hasNext() {
      return attributeIterator.hasNext();
    }

    @Override
    public Dom4jAttribute next() {
      return new Dom4jAttribute(attributeIterator.next());
    }

    @Override
    public void remove() {
      attributeIterator.remove();
    }
  }

  private static final class Dom4jElementIterator implements Iterator<Dom4jElement> {

    private final Iterator<Element> elementIterator;

    private Dom4jElementIterator(Iterator<Element> elementIterator) {
      this.elementIterator = elementIterator;
    }

    @Override
    public boolean hasNext() {
      return elementIterator.hasNext();
    }

    @Override
    public Dom4jElement next() {
      return new Dom4jElement(elementIterator.next());
    }

    @Override
    public void remove() {
      elementIterator.remove();
    }
  }
}
