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
import org.dom4j.Document;
import org.dom4j.Element;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class Dom4jDocument extends AbstractDom4jNode<Document> {

  private static final long serialVersionUID = 1L;

  public Dom4jDocument(Document document) {
    super(document);
  }

  @Override
  public QName getName() {
    return new QName(DOCUMENT);
  }

  @Override
  public Dom4jNode getParent() {
    return null;
  }

  @Override
  public Iterable<Dom4jElement> elements() {
    final Element root = getNode().getRootElement();
    return null == root
        ? Collections.emptyList()
        : Collections.singletonList(new Dom4jElement(root));
  }

  @Override
  public Iterable<Dom4jAttribute> attributes() {
    return Collections.emptyList();
  }

  @Override
  public Dom4jNode createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append attribute to a document node " + getNode());
  }

  @Override
  public Dom4jNode createElement(org.dom4j.QName element) throws XmlBuilderException {
    if (null != getNode().getRootElement()) {
      throw new XmlBuilderException(
          "Unable to create element " + element + " . Root element already exist");
    }
    return new Dom4jElement(getNode().addElement(element));
  }
}
