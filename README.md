# XPath-to-XML
[![Build Status](https://travis-ci.org/SimY4/xpath-to-xml.svg?branch=java6)](https://travis-ci.org/SimY4/xpath-to-xml)

Convenient utility to build XML models by evaluating XPath expressions.

# Supported XML models

 - DOM
 - DOM4J
 - XOM

# Usage

Include an artifact with necessary model extension into your project:

```xml
<dependency>
    <groupId>com.github.simy4</groupId>
    <artifactId>xpath-to-xml-dom</artifactId>
    <version>1.0</version>
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
you should be able to: 
- alter existing paths
```java
new XmlBuilder(document)
        .put("/breakfast_menu/food[1]/price", "$7.95")
        .build();
```
- append new elements
```java
new XmlBuilder(document)
        .put("/breakfast_menu/food[name\='Homestyle Breakfast'][price\='$6.95'][description\='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
        .build();
```
- remove paths
```java
new XmlBuilder(document)
        .remove("/breakfast_menu/food[name\='Belgian Waffles']")
        .build();
```
- combine any of the above actions into a single modification action
```java
new XmlBuilder(document)
        .remove("/breakfast_menu/food[name\='Homestyle Breakfast']")
        .put("/breakfast_menu/food[name\='Homestyle Breakfast'][price\='$6.95'][description\='Two eggs, bacon or sausage, toast, and our ever-popular hash browns']/calories", "950")
        .build();
```
