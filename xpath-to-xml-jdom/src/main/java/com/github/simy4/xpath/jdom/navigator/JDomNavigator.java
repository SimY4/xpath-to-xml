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
package com.github.simy4.xpath.jdom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jdom.navigator.node.JDomNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public final class JDomNavigator implements Navigator<JDomNode> {

  private final JDomNode xml;

  public JDomNavigator(JDomNode xml) {
    this.xml = xml;
  }

  @Override
  public JDomNode root() {
    return xml;
  }

  @Override
  public JDomNode parentOf(JDomNode node) {
    return node.getParent();
  }

  @Override
  public Iterable<? extends JDomNode> elementsOf(final JDomNode parent) {
    return parent.elements();
  }

  @Override
  public Iterable<? extends JDomNode> attributesOf(final JDomNode parent) {
    return parent.attributes();
  }

  @Override
  public JDomNode createAttribute(JDomNode parent, QName attribute) throws XmlBuilderException {
    final Attribute attr = new Attribute(attribute.getLocalPart(), "");
    if (!XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
      attr.setNamespace(Namespace.getNamespace(attribute.getPrefix(), attribute.getNamespaceURI()));
    }
    return parent.appendAttribute(attr);
  }

  @Override
  public JDomNode createElement(JDomNode parent, QName element) throws XmlBuilderException {
    final Element elem = new Element(element.getLocalPart());
    if (!XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
      elem.setNamespace(Namespace.getNamespace(element.getPrefix(), element.getNamespaceURI()));
    }
    return parent.appendElement(elem);
  }

  @Override
  public void setText(JDomNode node, String text) throws XmlBuilderException {
    node.setText(text);
  }

  @Override
  public void prependCopy(JDomNode node) throws XmlBuilderException {
    node.prependCopy();
  }

  @Override
  public void remove(JDomNode node) throws XmlBuilderException {
    node.remove();
  }
}
