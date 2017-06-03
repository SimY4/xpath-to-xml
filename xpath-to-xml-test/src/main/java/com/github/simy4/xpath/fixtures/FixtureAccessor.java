package com.github.simy4.xpath.fixtures;

import com.github.simy4.xpath.utils.OrderedProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public final class FixtureAccessor {

    private static final String XML_PROPERTIES_PATH_FORMAT =
            "/com/github/simy4/xpath/fixtures/%1$s/%1$s.properties";
    private static final String XML_PUT_PATH_FORMAT = "/com/github/simy4/xpath/fixtures/%1$s/%1$s-put.xml";
    private static final String XML_PUT_VALUE_PATH_FORMAT = "/com/github/simy4/xpath/fixtures/%1$s/%1$s-put-value.xml";

    private final String fixtureName;

    public FixtureAccessor(String fixtureName) {
        this.fixtureName = fixtureName;
    }

    public Map<String, Object> getXmlProperties() throws IOException {
        InputStream xpathPropertiesStream = getClass().getResourceAsStream(
                String.format(XML_PROPERTIES_PATH_FORMAT, fixtureName));
        try {
            OrderedProperties xpathProperties = new OrderedProperties();
            xpathProperties.load(xpathPropertiesStream);
            return xpathProperties.toMap();
        } finally {
            xpathPropertiesStream.close();
        }
    }

    public String getPutXml() throws IOException {
        return getXml(XML_PUT_PATH_FORMAT);
    }

    public String getPutValueXml() throws IOException {
        return getXml(XML_PUT_VALUE_PATH_FORMAT);
    }

    private String getXml(String format) throws IOException {
        InputStream xmlStream = getClass().getResourceAsStream(String.format(format, fixtureName));
        try {
            return new Scanner(xmlStream).useDelimiter("\\A").next();
        } finally {
            xmlStream.close();
        }
    }

}
