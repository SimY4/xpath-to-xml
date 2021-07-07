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
package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.google.gson.JsonElement;

/**
 * Gson node contract.
 *
 * @author Alex Simkin
 * @since 1.1
 */
public interface GsonNode extends Node {

  GsonNode getParent();

  void setParent(GsonNode parent);

  JsonElement get();

  void set(JsonElement jsonElement) throws XmlBuilderException;

  Iterable<? extends GsonNode> elements();

  Iterable<? extends GsonNode> attributes();
}
