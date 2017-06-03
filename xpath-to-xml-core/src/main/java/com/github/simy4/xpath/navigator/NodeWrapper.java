package com.github.simy4.xpath.navigator;

import javax.xml.namespace.QName;

public interface NodeWrapper<N> {

    N getWrappedNode();

    QName getNodeName();

    String getText();

}
