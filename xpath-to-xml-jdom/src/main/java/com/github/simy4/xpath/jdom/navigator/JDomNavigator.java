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
import com.github.simy4.xpath.jdom.navigator.node.JDomAttribute;
import com.github.simy4.xpath.jdom.navigator.node.JDomDocument;
import com.github.simy4.xpath.jdom.navigator.node.JDomElement;
import com.github.simy4.xpath.jdom.navigator.node.JDomNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.IllegalAddException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import java.util.List;

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
  public JDomNode createAttribute(JDomNode parent, QName attribute) {
    final Attribute attr = new Attribute(attribute.getLocalPart(), "");
    if (!XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
      attr.setNamespace(Namespace.getNamespace(attribute.getPrefix(), attribute.getNamespaceURI()));
    }
    return new JDomAttribute(attr);
  }

  @Override
  public JDomNode createElement(JDomNode parent, QName element) {
    final Element elem = new Element(element.getLocalPart());
    if (!XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
      elem.setNamespace(Namespace.getNamespace(element.getPrefix(), element.getNamespaceURI()));
    }
    return new JDomElement(elem);
  }

  @Override
  public void setText(JDomNode node, String text) throws XmlBuilderException {
    node.visit(new SetTextVisitor(text));
  }

  @Override
  public void appendPrev(JDomNode node, JDomNode prepend) throws XmlBuilderException {
    node.appendPrev(prepend);
  }

  @Override
  public void appendChild(JDomNode parent, JDomNode node) throws XmlBuilderException {
    parent.appendChild(node);
  }

  @Override
  public void appendNext(JDomNode node, JDomNode append) throws XmlBuilderException {
    node.appendNext(append);
  }

  @Override
  public void remove(JDomNode node) throws XmlBuilderException {
    node.visit(new RemoveVisitor());
  }

  private static final class RemoveVisitor implements JDomNode.Visitor {
    @Override
    public void visit(JDomAttribute attribute) {
      attribute.getNode().detach();
    }

    @Override
    public void visit(JDomDocument document) throws XmlBuilderException {
      throw new XmlBuilderException("Unable to remove document node " + document.getNode());
    }

    @Override
    public void visit(JDomElement element) {
      element.getNode().detach();
    }
  }

  private static final class SetTextVisitor implements JDomNode.Visitor {
    private final String text;

    private SetTextVisitor(String text) {
      this.text = text;
    }

    @Override
    public void visit(JDomAttribute attribute) {
      attribute.getNode().setValue(text);
    }

    @Override
    public void visit(JDomDocument document) {
      throw new XmlBuilderException("Unable to set value to a document node " + document.getNode());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(JDomElement element) {
      final Element wrappedNode = element.getNode();
      try {
        final Filter<Content> filter = (Filter<Content>) Filters.text().negate();
        final List<Content> content = wrappedNode.getContent(filter);
        wrappedNode.setContent(content);
        wrappedNode.addContent(new Text(text));
      } catch (IllegalAddException iae) {
        throw new XmlBuilderException("Unable to set value to " + wrappedNode, iae);
      }
    }
  }
}
