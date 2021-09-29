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
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.Node;
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
    final Elements childElements = getNode().getChildElements();
    return (Iterable<XomElement>)
        () ->
            IntStream.range(0, childElements.size())
                .mapToObj(i -> new XomElement(childElements.get(i)))
                .iterator();
  }

  @Override
  public Iterable<? extends XomNode> attributes() {
    final Element node = getNode();
    return (Iterable<XomAttribute>)
        () ->
            IntStream.range(0, node.getAttributeCount())
                .mapToObj(i -> new XomAttribute(node.getAttribute(i)))
                .iterator();
  }

  @Override
  public void appendChild(XomNode node) throws XmlBuilderException {
    try {
      final Node wrappedNode = node.getNode();
      if (wrappedNode instanceof Attribute) {
        getNode().addAttribute((Attribute) wrappedNode);
        return;
      }
      getNode().appendChild(wrappedNode);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an node to " + getNode(), iae);
    }
  }

  @Override
  public void setText(String text) throws XmlBuilderException {
    try {
      for (int i = 0; i < getNode().getChildCount(); i++) {
        final Node child = getNode().getChild(i);
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
