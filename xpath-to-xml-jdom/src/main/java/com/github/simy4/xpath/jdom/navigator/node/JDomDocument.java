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
package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.jdom2.Document;
import org.jdom2.IllegalAddException;

import javax.xml.namespace.QName;

import java.util.Collections;

public final class JDomDocument extends AbstractJDomNode<Document> {

  private static final long serialVersionUID = 1L;

  public JDomDocument(Document document) {
    super(document);
  }

  @Override
  public QName getName() {
    return new QName(DOCUMENT);
  }

  @Override
  public String getText() {
    final Document wrappedNode = getNode();
    return wrappedNode.hasRootElement() ? wrappedNode.getRootElement().getText() : "";
  }

  @Override
  public JDomNode getRoot() {
    return this;
  }

  @Override
  public JDomNode getParent() {
    return null;
  }

  @Override
  public Iterable<? extends JDomNode> elements() {
    final Document wrappedNode = getNode();
    return wrappedNode.hasRootElement()
        ? Collections.singletonList(new JDomElement(wrappedNode.getRootElement()))
        : Collections.emptyList();
  }

  @Override
  public Iterable<? extends JDomNode> attributes() {
    return Collections.emptyList();
  }

  @Override
  public void appendPrev(JDomNode prepend) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append node to a document node " + getNode());
  }

  @Override
  public void appendChild(JDomNode node) throws XmlBuilderException {
    if (getNode().hasRootElement()) {
      throw new XmlBuilderException(
          "Unable to append node " + node + " . Root element already exist");
    }
    node.visit(new AppendChild(getNode()));
  }

  @Override
  public void appendNext(JDomNode append) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append node to a document node " + getNode());
  }

  @Override
  public void visit(Visitor visitor) throws XmlBuilderException {
    visitor.visit(this);
  }

  private static final class AppendChild implements JDomNode.Visitor {
    private final Document document;

    private AppendChild(Document document) {
      this.document = document;
    }

    @Override
    public void visit(JDomAttribute attribute) throws XmlBuilderException {
      throw new XmlBuilderException(
          "Unable to append non-element node to a document node " + attribute);
    }

    @Override
    public void visit(JDomDocument document) throws XmlBuilderException {
      throw new XmlBuilderException(
          "Unable to append non-element node to a document node " + document);
    }

    @Override
    public void visit(JDomElement element) throws XmlBuilderException {
      try {
        document.setRootElement(element.getNode());
      } catch (IllegalAddException iae) {
        throw new XmlBuilderException("Unable to append element " + element, iae);
      }
    }
  }
}
