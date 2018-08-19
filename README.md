# XPath-to-XML
[![Build Status](https://travis-ci.org/SimY4/xpath-to-xml.svg?branch=master)](https://travis-ci.org/SimY4/xpath-to-xml)
[![codecov](https://codecov.io/gh/SimY4/xpath-to-xml/branch/master/graph/badge.svg)](https://codecov.io/gh/SimY4/xpath-to-xml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f5d4a594c4b94e9980d69d4dba9b9dba)](https://www.codacy.com/app/SimY4/xpath-to-xml?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SimY4/xpath-to-xml&amp;utm_campaign=Badge_Grade) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.simy4.xpath/xpath-to-xml-core.svg)](https://search.maven.org/search?q=g:com.github.simy4.xpath)
[![Javadocs](http://www.javadoc.io/badge/com.github.simy4.xpath/xpath-to-xml-core.svg)](http://www.javadoc.io/doc/com.github.simy4.xpath/xpath-to-xml-core)

Convenient utility to build XML models by evaluating XPath expressions.

# Supported XML models

 - DOM
 - DOM4J
 - XOM
 
## Additionally supported models

 - Gson
 - Jackson

# Usage

Include an artifact with necessary modelgit extension into your project:

```xml
<dependency>
    <groupId>com.github.simy4.xpath</groupId>
    <artifactId>xpath-to-xml-dom</artifactId>
    <version>1.2.1</version>
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
DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
Document document = documentBuilder.parse(xmlSource);
```

you can:

- alter existing paths

```java
new XmlBuilder()
        .put("/breakfast_menu/food[1]/price", "$7.95")
        .build(document);
```

- append new elements

```java
new XmlBuilder()
        .put("/breakfast_menu/food[name='Homestyle Breakfast'][price='$6.95'][description='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
        .build(document);
```

- remove paths

```java
new XmlBuilder()
        .remove("/breakfast_menu/food[name='Belgian Waffles']")
        .build(document);
```

- combine any of the above actions into a single modification action

```java
new XmlBuilder()
        .remove("/breakfast_menu/food[name='Homestyle Breakfast']")
        .put("/breakfast_menu/food[name='Homestyle Breakfast'][price='$6.95'][description='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
        .build(document);
```
