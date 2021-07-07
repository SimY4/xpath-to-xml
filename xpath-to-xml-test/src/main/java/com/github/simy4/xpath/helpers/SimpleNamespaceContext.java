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
package com.github.simy4.xpath.helpers;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;

public final class SimpleNamespaceContext implements NamespaceContext, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public String getNamespaceURI(String prefix) {
    if (null == prefix) {
      throw new IllegalArgumentException("prefix");
    } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
      return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
    } else if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
      return XMLConstants.XML_NS_URI;
    } else if ("my".equals(prefix)) {
      return "http://www.example.com/my";
    } else {
      return XMLConstants.NULL_NS_URI;
    }
  }

  @Override
  public String getPrefix(String namespaceUri) {
    if (null == namespaceUri) {
      throw new IllegalArgumentException("namespaceURI");
    } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceUri)) {
      return XMLConstants.XMLNS_ATTRIBUTE;
    } else if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
      return XMLConstants.XML_NS_PREFIX;
    } else if ("http://www.example.com/my".equals(namespaceUri)) {
      return "my";
    } else {
      return XMLConstants.DEFAULT_NS_PREFIX;
    }
  }

  @Override
  public Iterator<String> getPrefixes(String namespaceUri) {
    if (null == namespaceUri) {
      throw new IllegalArgumentException("namespaceURI");
    } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceUri)) {
      return Collections.singletonList(XMLConstants.XMLNS_ATTRIBUTE).iterator();
    } else if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
      return Collections.singletonList(XMLConstants.XML_NS_PREFIX).iterator();
    } else if ("http://www.example.com/my".equals(namespaceUri)) {
      return Collections.singletonList("my").iterator();
    } else {
      return Collections.emptyIterator();
    }
  }

  @Override
  public String toString() {
    return "{my -> http://www.example.com/my}";
  }
}
