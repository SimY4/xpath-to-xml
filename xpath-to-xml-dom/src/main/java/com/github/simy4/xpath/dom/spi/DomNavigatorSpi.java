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
package com.github.simy4.xpath.dom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom.navigator.DomNavigator;
import com.github.simy4.xpath.dom.navigator.DomNode;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Standard DOM model navigator extension SPI. */
public class DomNavigatorSpi implements NavigatorSpi {

  public DomNavigatorSpi() {}

  @Override
  public boolean canHandle(Object o) {
    return o instanceof Node && (o instanceof Document || (null != ((Node) o).getOwnerDocument()));
  }

  @Override
  public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
    if (!canHandle(xml)) {
      throw new IllegalArgumentException("XML model is not supported");
    }
    final var xmlNode = (Node) xml;
    final var node = new DomNode(xmlNode);
    final var navigator = new DomNavigator(xmlNode);
    for (var effect : effects) {
      effect.perform(navigator, node);
    }
    return xml;
  }
}
