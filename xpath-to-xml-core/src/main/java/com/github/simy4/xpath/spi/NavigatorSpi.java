package com.github.simy4.xpath.spi;

import com.github.simy4.xpath.XmlBuilderException;

/**
 * Navigator extension SPI.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface NavigatorSpi {

  /**
   * Checks whether this SPI can navigate models of given type.
   *
   * @param o XML model to check
   * @return {@code true} if SPI can handle this model or {@code false} otherwise
   */
  boolean canHandle(Object o);

  /**
   * Applies expression modifications to a given XML model.
   *
   * @param xml XML model to modify
   * @param effects effects to apply
   * @param <T> XML model type
   * @return modified XML model
   * @throws XmlBuilderException if error occur during XML model modification
   */
  <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException;
}
