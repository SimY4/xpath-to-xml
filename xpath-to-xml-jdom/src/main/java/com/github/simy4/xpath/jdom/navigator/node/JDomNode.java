/*
 * Copyright 2017-2021 Alex Simkin
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
import com.github.simy4.xpath.navigator.Node;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * JDOM node contract.
 *
 * @author Alex Simkin
 * @since 2.1
 */
public interface JDomNode extends Node {

  /**
   * Retrieves the XML model root.
   *
   * @return XML root
   */
  JDomNode getRoot();

  /**
   * Retrieves the parent of this XML node.
   *
   * @return XML node parent
   */
  JDomNode getParent();

  /**
   * Retrieves all child element nodes of this node.
   *
   * @return child element nodes
   */
  Iterable<? extends JDomNode> elements();

  /**
   * Retrieves all attributes of this node.
   *
   * @return attributes
   */
  Iterable<? extends JDomNode> attributes();

  /**
   * Creates XML attribute node and appends to ths node.
   *
   * @param attribute new XML attribute's name
   * @return new attribute node
   * @throws XmlBuilderException if failure occur during XML attribute creation
   */
  JDomNode appendAttribute(Attribute attribute) throws XmlBuilderException;

  /**
   * Creates XML element node and appends to ths node.
   *
   * @param element new XML element's name
   * @return new element node
   * @throws XmlBuilderException if failure occur during XML element creation
   */
  JDomNode appendElement(Element element) throws XmlBuilderException;

  void prependCopy() throws XmlBuilderException;

  /**
   * Sets the given text content to this node.
   *
   * @param text text content to set
   * @throws XmlBuilderException if failure occur during setting the text content
   */
  void setText(String text) throws XmlBuilderException;

  /**
   * Detach this node from the DOM.
   *
   * @throws XmlBuilderException if failure occur during node detaching
   */
  void remove() throws XmlBuilderException;
}
