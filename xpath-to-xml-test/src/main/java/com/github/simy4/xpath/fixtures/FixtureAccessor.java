package com.github.simy4.xpath.fixtures;

import com.github.simy4.xpath.util.OrderedProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

/**
 * XmlBuilder unified fixture accessor.
 */
public final class FixtureAccessor {

    private static final String XML_PROPERTIES_PATH_FORMAT =
            "/com/github/simy4/xpath/fixtures/%1$s/%1$s.properties";
    private static final String XML_PUT_PATH_FORMAT = "/com/github/simy4/xpath/fixtures/%1$s/%1$s-put.xml";
    private static final String XML_PUT_VALUE_PATH_FORMAT = "/com/github/simy4/xpath/fixtures/%1$s/%1$s-put-value.xml";

    private final String fixtureName;

    public FixtureAccessor(String fixtureName) {
        this.fixtureName = fixtureName;
    }

    /**
     * Reads XPath to Value properties from fixture resource as an ordered map.
     *
     * @return ordered XPath to Value mappings
     * @throws IOException is failed to access fixture resource
     */
    public Map<String, Object> getXmlProperties() throws IOException {
        final InputStream xpathPropertiesStream = getClass().getResourceAsStream(
                String.format(XML_PROPERTIES_PATH_FORMAT, fixtureName));
        try {
            OrderedProperties xpathProperties = new OrderedProperties();
            xpathProperties.load(xpathPropertiesStream);
            return xpathProperties.toMap();
        } finally {
            if (xpathPropertiesStream != null) {
                xpathPropertiesStream.close();
            }
        }
    }

    public String getPutXml() throws IOException {
        return getXml(XML_PUT_PATH_FORMAT);
    }

    public String getPutValueXml() throws IOException {
        return getXml(XML_PUT_VALUE_PATH_FORMAT);
    }

    private String getXml(String format) throws IOException {
        final InputStream xmlStream = getClass().getResourceAsStream(String.format(format, fixtureName));
        try {
            return new Scanner(xmlStream, "UTF-8").useDelimiter("\\A").next();
        } finally {
            if (xmlStream != null) {
                xmlStream.close();
            }
        }
    }

    @Override
    public String toString() {
        return fixtureName;
    }

}
