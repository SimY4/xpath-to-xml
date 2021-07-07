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

import javax.xml.namespace.QName;

/**
 * XML node contract.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Node {

  /** Document node constant name. */
  String DOCUMENT = "#document";

  /**
   * XML node name.
   *
   * @return node name.
   */
  QName getName();

  /**
   * XML node text content.
   *
   * @return text content.
   */
  String getText();
}
