package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

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
  public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to append attribute to a document node " + getNode());
  }

  @Override
  public XomNode appendElement(Element element) throws XmlBuilderException {
    throw new XmlBuilderException(
        "Unable to append element. Document has root: " + getNode().getRootElement());
  }

  @Override
  public void setText(String text) throws XmlBuilderException {
    throw new XmlBuilderException("Unable to set value to a document node " + getNode());
  }
}
