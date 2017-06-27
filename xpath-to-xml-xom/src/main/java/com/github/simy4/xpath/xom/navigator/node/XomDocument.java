package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import java.util.Collections;

@Immutable
public final class XomDocument implements XomNode<Document> {

    private final Document document;

    public XomDocument(Document document) {
        this.document = document;
    }

    @Override
    public Document getNode() {
        return document;
    }

    @Override
    public QName getName() {
        throw new UnsupportedOperationException("getName");
    }

    @Override
    public String getText() {
        return document.getValue();
    }

    @Override
    public Iterable<XomNode<Element>> elements() {
        final Element root = document.getRootElement();
        return null == root ? Collections.<XomNode<Element>>emptyList()
                : Collections.<XomNode<Element>>singletonList(new XomElement(root));
    }

    @Override
    public Iterable<XomNode<Attribute>> attributes() {
        return Collections.emptyList();
    }

    @Override
    public XomNode<Attribute> appendAttribute(Attribute attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a document node " + document);
    }

    @Override
    public XomNode<Element> appendElement(Element element) throws XmlBuilderException {
        return null;
    }

    @Override
    public void setValue(String value) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to set value to a document node " + document);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XomDocument that = (XomDocument) o;

        return document.equals(that.document);
    }

    @Override
    public int hashCode() {
        return document.hashCode();
    }

    @Override
    public String toString() {
        return document.toString();
    }

}
