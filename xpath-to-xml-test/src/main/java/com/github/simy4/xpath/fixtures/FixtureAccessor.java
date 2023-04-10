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
package com.github.simy4.xpath.fixtures;

import com.github.simy4.xpath.helpers.OrderedProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
    final var resource =
        String.format("/com/github/simy4/xpath/fixtures/%1$s/%1$s.properties", fixtureName);
    try (var xpathPropertiesStream = getClass().getResourceAsStream(resource)) {
      var xpathProperties = new OrderedProperties();
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
    final var resource = String.format(format, fixtureName, fixtureType);
    try (var xmlStream = getClass().getResourceAsStream(resource)) {
      assert null != xmlStream;
      return new Scanner(xmlStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
    } catch (IOException ioe) {
      throw new UncheckedIOException("Unable to fetch XML document " + resource, ioe);
    }
  }

  @Override
  public String toString() {
    return fixtureName + " for " + fixtureType;
  }
}
