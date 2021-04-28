package com.github.simy4.xpath.util;

import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;

public class TestNavigator implements NavigatorSpi {

  @Override
  public boolean canHandle(Object o) {
    return null != o;
  }

  @Override
  public <T> T process(T xml, Iterable<Effect> effects) {
    if (!canHandle(xml)) {
      throw new IllegalArgumentException("Argument can't be handled: " + xml);
    }
    return xml;
  }
}
