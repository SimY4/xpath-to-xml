# XPath-to-XML
[![Build Status](https://github.com/SimY4/xpath-to-xml/workflows/Build%20and%20Test/badge.svg?branch=2.x)](https://github.com/SimY4/xpath-to-xml/actions?query=workflow%3A"Build+and+Test")
[![codecov](https://codecov.io/gh/SimY4/xpath-to-xml/branch/2.x/graph/badge.svg)](https://codecov.io/gh/SimY4/xpath-to-xml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.simy4.xpath/xpath-to-xml-core.svg)](https://search.maven.org/search?q=g:com.github.simy4.xpath)
[![Javadocs](http://www.javadoc.io/badge/com.github.simy4.xpath/xpath-to-xml-core.svg)](http://www.javadoc.io/doc/com.github.simy4.xpath/xpath-to-xml-core)

Convenient utility to build XML models by evaluating XPath expressions.

# Supported XML models

 - DOM
 - DOM4J
 - JDOM
 - Scala XML
 - XOM
 
## Additionally supported models

 - jakarta.json
 - Gson
 - Jackson

# Usage

Include an artifact with necessary model extension into your project:

```xml
<dependency>
    <groupId>com.github.simy4.xpath</groupId>
    <artifactId>xpath-to-xml-dom</artifactId>
    <version>2.2.0</version>
</dependency>
```

Now having a XML structure i.e.:

```xml
<breakfast_menu>
    <food>
        <name>Belgian Waffles</name>
        <price>$5.95</price>
        <description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>
        <calories>650</calories>
    </food>
</breakfast_menu>
```

and one of supported models:

```java
import java.io.StringReader;
import javax.xml.parsers.*;

import org.xml.sax.InputSource;

class Example0 {
  private static final String xmlSource = "my-xml-source";

  public static Document document(String xml) throws Exception {
    var documentBuilderFactory = DocumentBuilderFactory.newInstance();
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var inputSource = new InputSource(new StringReader(xml));
    return documentBuilder.parse(inputSource);
  }
}
```

you can:

- alter existing paths

```java
import com.github.simy4.xpath.XmlBuilder;

class Example1 extends Example0 { 
    public static void main(String... args) throws Exception {
        new XmlBuilder()
                .put("/breakfast_menu/food[1]/price", "$7.95")
                .build(document(xmlSource));
    }
}
```

- append new elements

```java
import com.github.simy4.xpath.XmlBuilder;

class Example2 extends Example0 { 
    public static void main(String... args) throws Exception {
        new XmlBuilder()
                .put("/breakfast_menu/food[name='Homestyle Breakfast'][price='$6.95'][description='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
                .build(document(xmlSource));
    }
}
```

- remove paths

```java
import com.github.simy4.xpath.XmlBuilder;

class Example3 extends Example0 { 
    public static void main(String... args) throws Exception {
        new XmlBuilder()
                .remove("/breakfast_menu/food[name='Belgian Waffles']")
                .build(document(xmlSource));
    }
}
```

- combine any of the above actions into a single modification action

```java
import com.github.simy4.xpath.XmlBuilder;

class Example4 extends Example0 { 
    public static void main(String... args) throws Exception {
        new XmlBuilder()
                .remove("/breakfast_menu/food[name='Homestyle Breakfast']")
                .put("/breakfast_menu/food[name='Homestyle Breakfast'][price='$6.95'][description='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
                .build(document(xmlSource));
    }
}
```
