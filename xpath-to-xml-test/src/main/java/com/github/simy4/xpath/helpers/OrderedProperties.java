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
    Map<String, Object> map = new LinkedHashMap<>(keys.size());
    for (Object orderedKey : keys) {
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
