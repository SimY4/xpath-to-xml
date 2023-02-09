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

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties that preserves the order in which they were read from the file.
 *
 * @author Alex Simkin
 */
@SuppressWarnings("JdkObsolete")
public class OrderedProperties extends Properties {

  private static final long serialVersionUID = 1L;

  private final Set<Object> keys = new LinkedHashSet<>();

  /**
   * Transforms properties to linked hash map.
   *
   * @return linked hash map of property values
   */
  public synchronized Map<String, Object> toMap() {
    Map<String, Object> map = new LinkedHashMap<>((int) Math.ceil(keys.size() / 0.75d));
    for (var orderedKey : keys) {
      map.put(String.valueOf(orderedKey), get(orderedKey));
    }
    return map;
  }

  @Override
  public synchronized Enumeration<Object> keys() {
    return Collections.enumeration(keys);
  }

  @Override
  public synchronized Object put(Object key, Object value) {
    keys.add(key);
    return super.put(key, value);
  }

  @Override
  public synchronized Object remove(Object key) {
    keys.remove(key);
    return super.remove(key);
  }
}
