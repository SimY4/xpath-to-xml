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
package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * XOM node contract.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface XomNode extends Node {

  nu.xom.Node getNode();

  /**
   * Retrieves all child element nodes of this node.
   *
   * @return child element nodes
   */
  Iterable<? extends XomNode> elements();

  /**
   * Retrieves all attributes of this node.
   *
   * @return attributes
   */
  Iterable<? extends XomNode> attributes();

  /**
   * Creates XML attribute node and appends to ths node.
   *
   * @param attribute new XML attribute's name
   * @return new attribute node
   * @throws XmlBuilderException if failure occur during XML attribute creation
   */
  XomNode appendAttribute(Attribute attribute) throws XmlBuilderException;

  /**
   * Creates XML element node and appends to ths node.
   *
   * @param element new XML element's name
   * @return new element node
   * @throws XmlBuilderException if failure occur during XML element creation
   */
  XomNode appendElement(Element element) throws XmlBuilderException;

  /**
   * Sets the given text content to this node.
   *
   * @param text text content to set
   * @throws XmlBuilderException if failure occur during setting the text content
   */
  void setText(String text) throws XmlBuilderException;
}
