package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Document;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class Dom4jDocument extends AbstractDom4jNode<Document> {

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
    public Iterable<? extends Dom4jNode> elements() {
        final var root = getNode().getRootElement();
        return null == root ? Collections.emptyList() : Collections.singletonList(new Dom4jElement(root));
    }

    @Override
    public Iterable<Dom4jNode> attributes() {
        return Collections.emptyList();
    }

    @Override
    public Dom4jNode createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a document node " + getNode());
    }

    @Override
    public Dom4jNode createElement(org.dom4j.QName element) throws XmlBuilderException {
        if (null != getNode().getRootElement()) {
            throw new XmlBuilderException("Unable to create element " + element + " . Root element already exist");
        }
        return new Dom4jElement(getNode().addElement(element));
    }

}
