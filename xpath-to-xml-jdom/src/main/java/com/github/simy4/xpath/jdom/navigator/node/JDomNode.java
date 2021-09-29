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
   * Prepends XML node to ths node.
   *
   * @param prepend new XML node
   * @throws XmlBuilderException if failure occur during XML element append
   */
  void appendPrev(JDomNode prepend) throws XmlBuilderException;

  /**
   * Appends XML node to ths node.
   *
   * @param node new XML node
   * @throws XmlBuilderException if failure occur during XML element append
   */
  void appendChild(JDomNode node) throws XmlBuilderException;

  /**
   * Appends XML node to ths node as a successor node.
   *
   * @param append new XML node
   * @throws XmlBuilderException if failure occur during XML element append
   */
  void appendNext(JDomNode append) throws XmlBuilderException;

  /**
   * JSON node visitor.
   *
   * @param visitor visitor instance
   */
  void visit(Visitor visitor) throws XmlBuilderException;

  interface Visitor {
    void visit(JDomAttribute attribute) throws XmlBuilderException;

    void visit(JDomDocument document) throws XmlBuilderException;

    void visit(JDomElement element) throws XmlBuilderException;
  }
}
