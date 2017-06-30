package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;

public interface Dom4jNode<N extends org.dom4j.Node> extends Node {

    N getNode();

    Iterable<Dom4jNode<Element>> elements();

    Iterable<Dom4jNode<Attribute>> attributes();

    Dom4jNode<Attribute> createAttribute(QName attribute) throws XmlBuilderException;

    Dom4jNode<Element> createElement(QName element) throws XmlBuilderException;

}
