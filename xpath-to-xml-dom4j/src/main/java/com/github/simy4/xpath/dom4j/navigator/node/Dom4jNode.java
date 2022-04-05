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
package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.QName;

/**
 * DOM4J node contract.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Dom4jNode extends Node {

  org.dom4j.Node getNode();

  /**
   * Retrieves parent node of this node.
   *
   * @return parent node
   */
  Dom4jNode getParent();

  /**
   * Retrieves all child element nodes of this node.
   *
   * @return child element nodes
   */
  Iterable<Dom4jElement> elements();

  /**
   * Retrieves all attributes of this node.
   *
   * @return attributes
   */
  Iterable<Dom4jAttribute> attributes();

  /**
   * Creates XML attribute node and appends to ths node.
   *
   * @param attribute new XML attribute's name
   * @return new attribute node
   * @throws XmlBuilderException if failure occur during XML attribute creation
   */
  Dom4jNode createAttribute(QName attribute) throws XmlBuilderException;

  /**
   * Creates XML element node and appends to ths node.
   *
   * @param element new XML element's name
   * @return new element node
   * @throws XmlBuilderException if failure occur during XML element creation
   */
  Dom4jNode createElement(QName element) throws XmlBuilderException;
}
