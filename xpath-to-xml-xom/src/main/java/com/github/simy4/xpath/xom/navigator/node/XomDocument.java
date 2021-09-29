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
import nu.xom.Document;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class XomDocument extends AbstractXomNode<Document> {

  public XomDocument(Document document) {
    super(document);
  }

  @Override
  public QName getName() {
    return new QName(DOCUMENT);
  }

  @Override
  public String getText() {
    return getNode().getValue();
  }

  @Override
  public Iterable<? extends XomNode> elements() {
    return Collections.singletonList(new XomElement(getNode().getRootElement()));
  }

  @Override
  public Iterable<? extends XomNode> attributes() {
    return Collections.emptyList();
  }

  @Override
  public void appendChild(XomNode node) throws XmlBuilderException {
    throw new XmlBuilderException(
        "Unable to append element. Document has root: " + getNode().getRootElement());
  }

  @Override
  public void setText(String text) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to set value to a document node " + getNode());
  }
}
