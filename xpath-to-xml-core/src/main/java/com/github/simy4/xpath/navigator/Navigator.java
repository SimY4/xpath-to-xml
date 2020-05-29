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
package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;

import javax.xml.namespace.QName;

/**
 * XML model navigator contract.
 *
 * @param <N> XML model nodes type
 * @author Alex Simkin
 * @since 1.0
 */
public interface Navigator<N extends Node> {

  /**
   * Wrapped XML root.
   *
   * @return XML root
   */
  N root();

  /**
   * Wrapped parent of given XML node.
   *
   * @param node XML node to scan
   * @return XML node parent
   */
  N parentOf(N node);

  /**
   * Child element nodes of given XML node.
   *
   * @param parent XML node to scan
   * @return child element nodes
   */
  Iterable<? extends N> elementsOf(N parent);

  /**
   * Child attribute nodes of given XML node.
   *
   * @param parent XML node to scan
   * @return child attribute nodes
   */
  Iterable<? extends N> attributesOf(N parent);

    /**
     * Creates XML attribute node.
     *
     * @param parent    parent XML node
     * @param attribute new XML attribute's name
     * @return newly created attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    N createAttribute(N parent, QName attribute) throws XmlBuilderException;

    /**
     * Creates XML element node.
     *
     * @param parent  parent XML node
     * @param element new XML element's name
     * @return newly created element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    N createElement(N parent, QName element) throws XmlBuilderException;

  /**
   * Sets the given text content to a given node.
   *
   * @param node XML node to modify
   * @param text text content to set
   * @throws XmlBuilderException if failure occur during setting the text content
   */
  void setText(N node, String text) throws XmlBuilderException;

    /**
     * Appends given node to parent node as child node.
     *
     * @param parent parent XML node to modify
     * @param node XML node to append as a child
     * @throws XmlBuilderException if failure occur during node appending
     */
    void appendChild(N parent, N node) throws XmlBuilderException;

    /**
     * Appends given node to this node as following node.
     *
     * @param node XML node to append
     * @param append appending XML node
     * @throws XmlBuilderException if failure occur during node appending
     */
    void appendNext(N node, N append) throws XmlBuilderException;

    /**
     * Prepends given node to this node as preceding node.
     *
     * @param node XML node to prepend
     * @param prepend prepending XML node
     * @throws XmlBuilderException if failure occur during node prepending
     */
    void appendPrev(N node, N prepend) throws XmlBuilderException;

  /**
   * Removes/detaches given node from XML model.
   *
   * @param node node to remove
   * @throws XmlBuilderException if failure occur during node removal
   */
  void remove(N node) throws XmlBuilderException;
}
