/*
 * Copyright 2018-2021 Alex Simkin
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
package com.github.simy4.xpath.gson.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.gson.navigator.GsonNavigator;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.gson.navigator.node.GsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.google.gson.JsonElement;

/** Gson model navigator extension SPI. */
public class GsonNavigatorSpi implements NavigatorSpi {

  @Override
  public boolean canHandle(Object o) {
    return o instanceof JsonElement;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T process(T json, Iterable<Effect> effects) throws XmlBuilderException {
    if (!canHandle(json)) {
      throw new IllegalArgumentException("JSON model is not supported");
    }
    final GsonNode root = new GsonRootNode((JsonElement) json);
    final Navigator<GsonNode> navigator = new GsonNavigator(root);
    for (Effect effect : effects) {
      effect.perform(navigator, root);
    }
    return (T) root.get();
  }
}
