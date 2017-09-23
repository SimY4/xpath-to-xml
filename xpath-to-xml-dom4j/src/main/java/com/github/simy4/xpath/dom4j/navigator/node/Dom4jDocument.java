package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class Dom4jDocument implements Dom4jNode<Document> {

    private final Document document;

    public Dom4jDocument(Document document) {
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
        return document.getText();
    }

    @Override
    public Iterable<Dom4jNode<Element>> elements() {
        final Element root = document.getRootElement();
        return null == root ? Collections.<Dom4jNode<Element>>emptyList()
                : Collections.<Dom4jNode<Element>>singletonList(new Dom4jElement(root));
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return Collections.emptyList();
    }

    @Override
    public Dom4jNode<Attribute> createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a document node " + document);
    }

    @Override
    public Dom4jNode<Element> createElement(org.dom4j.QName element) throws XmlBuilderException {
        if (null != document.getRootElement()) {
            throw new XmlBuilderException("Unable to create element " + element + " . Root element already exist");
        }
        return new Dom4jElement(document.addElement(element));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dom4jDocument that = (Dom4jDocument) o;

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
