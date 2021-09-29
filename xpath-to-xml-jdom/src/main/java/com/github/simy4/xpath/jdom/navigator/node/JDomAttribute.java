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

import com.github.simy4.xpath.XmlBuilderException;
import org.jdom2.Attribute;
import org.jdom2.Element;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class JDomAttribute extends AbstractJDomNode<Attribute> {

  private static final long serialVersionUID = 1L;

  public JDomAttribute(Attribute attribute) {
    super(attribute);
  }

  @Override
  public QName getName() {
    return new QName(
        getNode().getNamespaceURI(), getNode().getName(), getNode().getNamespacePrefix());
  }

  @Override
  public String getText() {
    return getNode().getValue();
  }

  @Override
  public JDomNode getRoot() {
    return new JDomDocument(getNode().getDocument());
  }

  @Override
  public JDomNode getParent() {
    final Element parent = getNode().getParent();
    return null == parent ? null : new JDomElement(parent);
  }

  @Override
  public Iterable<? extends JDomNode> elements() {
    return Collections.emptyList();
  }

  @Override
  public Iterable<? extends JDomNode> attributes() {
    return Collections.emptyList();
  }

  @Override
  public void appendPrev(JDomNode prepend) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append node to a non-element node " + getNode());
  }

  @Override
  public void appendChild(JDomNode node) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append node to a non-element node " + getNode());
  }

  @Override
  public void appendNext(JDomNode append) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append node to a non-element node " + getNode());
  }

  @Override
  public void visit(Visitor visitor) throws XmlBuilderException {
    visitor.visit(this);
  }
}
