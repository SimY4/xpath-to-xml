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
package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class Dom4jAttribute extends AbstractDom4jNode<Attribute> {

  private static final long serialVersionUID = 1L;

  public Dom4jAttribute(Attribute attribute) {
    super(attribute);
  }

  @Override
  public QName getName() {
    final Namespace namespace = getNode().getNamespace();
    return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
  }

  @Override
  public Dom4jNode getParent() {
    final Element parent = getNode().getParent();
    return null == parent ? null : new Dom4jElement(parent);
  }

  @Override
  public Iterable<Dom4jElement> elements() {
    return Collections.emptyList();
  }

  @Override
  public Iterable<Dom4jAttribute> attributes() {
    return Collections.emptyList();
  }

  @Override
  public Dom4jNode createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append attribute to a non-element node " + getNode());
  }

  @Override
  public Dom4jNode createElement(org.dom4j.QName element) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append element to an attribute " + getNode());
  }
}
