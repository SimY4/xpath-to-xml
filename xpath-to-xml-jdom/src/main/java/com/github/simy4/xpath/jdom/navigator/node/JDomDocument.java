package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.IllegalAddException;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class JDomDocument extends AbstractJDomNode<Document> {

    public JDomDocument(Document document) {
        super(document);
    }

    @Override
    public QName getName() {
        return new QName(DOCUMENT);
    }

    @Override
    public String getText() {
        return getNode().hasRootElement() ? getNode().getRootElement().getText() : "";
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
        return getNode().hasRootElement() ? Collections.singletonList(new JDomElement(getNode().getRootElement()))
                : Collections.emptyList();
    }

    @Override
    public Iterable<? extends JDomNode> attributes() {
        return Collections.emptyList();
    }

    @Override
    public JDomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a document node " + getNode());
    }

    @Override
    public JDomNode appendElement(Element element) throws XmlBuilderException {
        if (getNode().hasRootElement()) {
            throw new XmlBuilderException("Unable to append element " + element + " . Root element already exist");
        }
        try {
            getNode().setRootElement(element);
            return new JDomElement(element);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append element " + element, iae);
        }
    }

    @Override
    public void prependCopy() throws XmlBuilderException {
        throw new XmlBuilderException("Unable to prepend copy of a document " + getNode());
    }

    @Override
    public void setText(String text) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to set value to a document node " + getNode());
    }

    @Override
    public void remove() throws XmlBuilderException {
        throw new XmlBuilderException("Unable to remove document node " + getNode());
    }

}
