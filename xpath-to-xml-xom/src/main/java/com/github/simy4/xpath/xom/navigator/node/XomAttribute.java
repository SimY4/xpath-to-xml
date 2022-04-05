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
import nu.xom.IllegalDataException;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class XomAttribute extends AbstractXomNode<Attribute> {

  public XomAttribute(Attribute attribute) {
    super(attribute);
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
    return Collections.emptyList();
  }

  @Override
  public Iterable<? extends XomNode> attributes() {
    return Collections.emptyList();
  }

  @Override
  public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append attribute to a non-element node " + getNode());
  }

  @Override
  public XomNode appendElement(Element element) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append element to an attribute " + getNode());
  }

  @Override
  public void setText(String text) throws XmlBuilderException {
    try {
      getNode().setValue(text);
    } catch (IllegalDataException ide) {
      throw new XmlBuilderException("Unable to set value to " + getNode(), ide);
    }
  }
}
