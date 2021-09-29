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
import org.jdom2.Element;
import org.jdom2.IllegalAddException;
import org.jdom2.Parent;

import javax.xml.namespace.QName;

public final class JDomElement extends AbstractJDomNode<Element> {

  private static final long serialVersionUID = 1L;

  public JDomElement(Element element) {
    super(element);
  }

  @Override
  public QName getName() {
    final Element wrappedNode = getNode();
    return new QName(
        wrappedNode.getNamespaceURI(), wrappedNode.getName(), wrappedNode.getNamespacePrefix());
  }

  @Override
  public String getText() {
    return getNode().getText();
  }

  @Override
  public JDomNode getRoot() {
    return new JDomDocument(getNode().getDocument());
  }

  @Override
  @SuppressWarnings("ReferenceEquality")
  public JDomNode getParent() {
    final Element node = getNode();
    final Parent parent = node.getParent();
    return null == parent
        ? node.getDocument().getRootElement() == node ? getRoot() : null
        : new JDomElement((Element) parent);
  }

  @Override
  public Iterable<? extends JDomNode> elements() {
    final Element wrappedNode = getNode();
    return (Iterable<JDomElement>)
        () -> wrappedNode.getChildren().stream().map(JDomElement::new).iterator();
  }

  @Override
  public Iterable<? extends JDomNode> attributes() {
    final Element wrappedNode = getNode();
    return (Iterable<JDomAttribute>)
        () -> wrappedNode.getAttributes().stream().map(JDomAttribute::new).iterator();
  }

  @Override
  public void appendPrev(JDomNode prepend) throws XmlBuilderException {
    prepend.visit(new AppendPrev(getNode()));
  }

  @Override
  public void appendChild(JDomNode node) throws XmlBuilderException {
    node.visit(new AppendChild(getNode()));
  }

  @Override
  public void appendNext(JDomNode append) throws XmlBuilderException {
    append.visit(new AppendNext(getNode()));
  }

  @Override
  public void visit(Visitor visitor) throws XmlBuilderException {
    visitor.visit(this);
  }

  private static final class AppendPrev implements JDomNode.Visitor {
    private final Element element;

    private AppendPrev(Element element) {
      this.element = element;
    }

    @Override
    public void visit(JDomAttribute attribute) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to append non-element node to " + element);
    }

    @Override
    public void visit(JDomDocument document) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to append non-element node to " + element);
    }

    @Override
    public void visit(JDomElement element) throws XmlBuilderException {
      final Parent parent = this.element.getParent();
      if (null == parent) {
        throw new XmlBuilderException("Unable to prepend - no parent found of " + this.element);
      }
      final Element node = element.getNode();
      final int prependIndex = parent.indexOf(this.element);
      parent.addContent(prependIndex, node);
    }
  }

  private static final class AppendChild implements JDomNode.Visitor {
    private final Element element;

    private AppendChild(Element element) {
      this.element = element;
    }

    @Override
    public void visit(JDomAttribute attribute) throws XmlBuilderException {
      try {
        element.setAttribute(attribute.getNode());
      } catch (IllegalAddException iae) {
        throw new XmlBuilderException("Unable to append an attribute to " + element, iae);
      }
    }

    @Override
    public void visit(JDomDocument document) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to append document node to " + element);
    }

    @Override
    public void visit(JDomElement element) throws XmlBuilderException {
      try {
        this.element.addContent(element.getNode());
      } catch (IllegalAddException iae) {
        throw new XmlBuilderException("Unable to append element to " + element, iae);
      }
    }
  }

  private static final class AppendNext implements JDomNode.Visitor {
    private final Element element;

    private AppendNext(Element element) {
      this.element = element;
    }

    @Override
    public void visit(JDomAttribute attribute) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to append non-element node to " + element);
    }

    @Override
    public void visit(JDomDocument document) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to append non-element node to " + element);
    }

    @Override
    public void visit(JDomElement element) throws XmlBuilderException {
      final Parent parent = this.element.getParent();
      if (null == parent) {
        throw new XmlBuilderException("Unable to append - no parent found of " + this.element);
      }
      parent.addContent(element.getNode());
    }
  }
}
