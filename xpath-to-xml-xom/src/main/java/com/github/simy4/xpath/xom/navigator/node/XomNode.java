package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import nu.xom.Attribute;
import nu.xom.Element;

public interface XomNode<N extends nu.xom.Node> extends Node {

    N getNode();

    Iterable<XomNode<Element>> elements();

    Iterable<XomNode<Attribute>> attributes();

    XomNode<Attribute> appendAttribute(Attribute attribute) throws XmlBuilderException;

    XomNode<Element> appendElement(Element element) throws XmlBuilderException;

    void setValue(String value) throws XmlBuilderException;

}
