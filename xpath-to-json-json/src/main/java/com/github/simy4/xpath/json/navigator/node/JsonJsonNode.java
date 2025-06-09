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
package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.util.stream.Stream;

/**
 * org.JSON node contract.
 *
 * @author Alex Simkin
 * @since 2.4
 */
public interface JsonJsonNode extends Node {

  JsonJsonNode getParent();

  void setParent(JsonJsonNode parent);

  Object get();

  void set(Object jsonValue) throws XmlBuilderException;

  Stream<JsonJsonNode> elements();

  Stream<JsonJsonNode> attributes();
}
