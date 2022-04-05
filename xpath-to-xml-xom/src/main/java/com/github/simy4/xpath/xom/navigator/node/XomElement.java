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
package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.Text;

import javax.xml.namespace.QName;

import java.util.stream.IntStream;

public final class XomElement extends AbstractXomNode<Element> {

  public XomElement(Element element) {
    super(element);
  }

  @Override
  public QName getName() {
    return new QName(
        getNode().getNamespaceURI(), getNode().getLocalName(), getNode().getNamespacePrefix());
  }

  @Override
  public String getText() {
    return getNode().getValue();
  }

  @Override
  public Iterable<? extends XomNode> elements() {
    final var childElements = getNode().getChildElements();
    return (Iterable<XomElement>)
        () ->
            IntStream.range(0, childElements.size())
                .mapToObj(i -> new XomElement(childElements.get(i)))
                .iterator();
  }

  @Override
  public Iterable<? extends XomNode> attributes() {
    final var node = getNode();
    return (Iterable<XomAttribute>)
        () ->
            IntStream.range(0, node.getAttributeCount())
                .mapToObj(i -> new XomAttribute(node.getAttribute(i)))
                .iterator();
  }

  @Override
  public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
    try {
      getNode().addAttribute(attribute);
      return new XomAttribute(attribute);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an attribute to " + getNode(), iae);
    }
  }

  @Override
  public XomNode appendElement(Element element) throws XmlBuilderException {
    try {
      getNode().appendChild(element);
      return new XomElement(element);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an element to " + getNode(), iae);
    }
  }

  @Override
  public void setText(String text) throws XmlBuilderException {
    try {
      for (var i = 0; i < getNode().getChildCount(); i++) {
        final var child = getNode().getChild(i);
        if (child instanceof Text) {
          ((Text) child).setValue(text);
          return;
        }
      }
      getNode().appendChild(text);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to set value to " + getNode(), iae);
    }
  }
}
