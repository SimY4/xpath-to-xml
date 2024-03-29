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
package com.github.simy4.xpath.xom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.github.simy4.xpath.xom.navigator.XomNavigator;
import com.github.simy4.xpath.xom.navigator.node.XomAttribute;
import com.github.simy4.xpath.xom.navigator.node.XomDocument;
import com.github.simy4.xpath.xom.navigator.node.XomElement;
import com.github.simy4.xpath.xom.navigator.node.XomNode;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParentNode;

/** XOM model navigator extension SPI. */
public class XomNavigatorSpi implements NavigatorSpi {

  @Override
  public boolean canHandle(Object o) {
    return (o instanceof ParentNode && null != ((ParentNode) o).getDocument())
        || (o instanceof Attribute && null != ((Attribute) o).getDocument());
  }

  @Override
  public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
    final XomNode node;
    if (xml instanceof Document) {
      node = new XomDocument((Document) xml);
    } else if (xml instanceof Element) {
      node = new XomElement((Element) xml);
    } else if (xml instanceof Attribute) {
      node = new XomAttribute((Attribute) xml);
    } else {
      throw new IllegalArgumentException("XML model is not supported");
    }
    final Navigator<XomNode> navigator =
        new XomNavigator(new XomDocument(node.getNode().getDocument()));
    for (Effect effect : effects) {
      effect.perform(navigator, node);
    }
    return xml;
  }
}
