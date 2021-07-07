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
package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.IllegalAddException;
import org.jdom2.Parent;
import org.jdom2.Text;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;

import javax.xml.namespace.QName;

import java.util.Iterator;
import java.util.List;

public final class JDomElement extends AbstractJDomNode<Element> {

  private static final long serialVersionUID = 1L;

  public JDomElement(Element element) {
    super(element);
  }

  @Override
  public QName getName() {
    return new QName(
        getNode().getNamespaceURI(), getNode().getName(), getNode().getNamespacePrefix());
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
    return new Iterable<JDomElement>() {
      @Override
      public Iterator<JDomElement> iterator() {
        return new JDomElementIterator(getNode().getChildren().iterator());
      }
    };
  }

  @Override
  public Iterable<? extends JDomNode> attributes() {
    return new Iterable<JDomAttribute>() {
      @Override
      public Iterator<JDomAttribute> iterator() {
        return new JDomAttributeIterator(getNode().getAttributes().iterator());
      }
    };
  }

  @Override
  public JDomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
    try {
      getNode().setAttribute(attribute);
      return new JDomAttribute(attribute);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an attribute to " + getNode(), iae);
    }
  }

  @Override
  public JDomNode appendElement(Element element) throws XmlBuilderException {
    try {
      getNode().addContent(element);
      return new JDomElement(element);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an element to " + getNode(), iae);
    }
  }

  @Override
  public void prependCopy() throws XmlBuilderException {
    final Element node = getNode();
    final Parent parent = node.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
    }
    final int prependIndex = parent.indexOf(node);
    final Element copy = node.clone();
    parent.addContent(prependIndex, copy);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setText(String text) throws XmlBuilderException {
    try {
      final Filter<Content> filter = (Filter<Content>) Filters.text().negate();
      final List<Content> content = getNode().getContent(filter);
      getNode().setContent(content);
      getNode().addContent(new Text(text));
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to set value to " + getNode(), iae);
    }
  }

  @Override
  public void remove() {
    getNode().detach();
  }

  private static final class JDomAttributeIterator implements Iterator<JDomAttribute> {

    private final Iterator<Attribute> attributeIterator;

    private JDomAttributeIterator(Iterator<Attribute> attributeIterator) {
      this.attributeIterator = attributeIterator;
    }

    @Override
    public boolean hasNext() {
      return attributeIterator.hasNext();
    }

    @Override
    public JDomAttribute next() {
      return new JDomAttribute(attributeIterator.next());
    }

    @Override
    public void remove() {
      attributeIterator.remove();
    }
  }

  private static final class JDomElementIterator implements Iterator<JDomElement> {

    private final Iterator<Element> elementIterator;

    private JDomElementIterator(Iterator<Element> elementIterator) {
      this.elementIterator = elementIterator;
    }

    @Override
    public boolean hasNext() {
      return elementIterator.hasNext();
    }

    @Override
    public JDomElement next() {
      return new JDomElement(elementIterator.next());
    }

    @Override
    public void remove() {
      elementIterator.remove();
    }
  }
}
