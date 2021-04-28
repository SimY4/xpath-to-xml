package com.github.simy4.xpath.fixtures;

import com.github.simy4.xpath.helpers.OrderedProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Scanner;

/** XmlBuilder unified fixture accessor. */
public final class FixtureAccessor {

  private final String fixtureName;
  private final String fixtureType;

  public FixtureAccessor(String fixtureName) {
    this(fixtureName, "xml");
  }

  public FixtureAccessor(String fixtureName, String fixtureType) {
    this.fixtureName = fixtureName;
    this.fixtureType = fixtureType;
  }

  /**
   * Reads XPath to Value properties from fixture resource as an ordered map.
   *
   * @return ordered XPath to Value mappings
   */
  public Map<String, Object> getXmlProperties() {
    final String resource =
        String.format("/com/github/simy4/xpath/fixtures/%1$s/%1$s.properties", fixtureName);
    try (InputStream xpathPropertiesStream = getClass().getResourceAsStream(resource)) {
      OrderedProperties xpathProperties = new OrderedProperties();
      xpathProperties.load(xpathPropertiesStream);
      return xpathProperties.toMap();
    } catch (IOException ioe) {
      throw new UncheckedIOException("Unable to fetch XML properties " + resource, ioe);
    }
  }

  public String getPutXml() {
    return getXml("/com/github/simy4/xpath/fixtures/%1$s/%1$s-put.%2$s");
  }

  public String getPutValueXml() {
    return getXml("/com/github/simy4/xpath/fixtures/%1$s/%1$s-put-value.%2$s");
  }

  private String getXml(String format) {
    final String resource = String.format(format, fixtureName, fixtureType);
    try (InputStream xmlStream = getClass().getResourceAsStream(resource)) {
      return new Scanner(xmlStream, "UTF-8").useDelimiter("\\A").next();
    } catch (IOException ioe) {
      throw new UncheckedIOException("Unable to fetch XML document " + resource, ioe);
    }
  }

  @Override
  public String toString() {
    return fixtureName + " for " + fixtureType;
  }
}
