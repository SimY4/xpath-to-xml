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
package com.github.simy4.xpath.jdom.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jdom.navigator.JDomNavigator;
import com.github.simy4.xpath.jdom.navigator.node.JDomAttribute;
import com.github.simy4.xpath.jdom.navigator.node.JDomDocument;
import com.github.simy4.xpath.jdom.navigator.node.JDomElement;
import com.github.simy4.xpath.jdom.navigator.node.JDomNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;

/** XOM model navigator extension SPI. */
public class JDomNavigatorSpi implements NavigatorSpi {

  @Override
  public boolean canHandle(Object o) {
    return (o instanceof Parent && null != ((Parent) o).getDocument())
        || (o instanceof Attribute && null != ((Attribute) o).getDocument());
  }

  @Override
  public <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException {
    final JDomNode node;
    if (xml instanceof Document) {
      node = new JDomDocument((Document) xml);
    } else if (xml instanceof Element) {
      node = new JDomElement((Element) xml);
    } else if (xml instanceof Attribute) {
      node = new JDomAttribute((Attribute) xml);
    } else {
      throw new IllegalArgumentException("XML model is not supported");
    }
    final Navigator<JDomNode> navigator = new JDomNavigator(node.getRoot());
    for (Effect effect : effects) {
      effect.perform(navigator, node);
    }
    return xml;
  }
}
