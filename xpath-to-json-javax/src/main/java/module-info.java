module com.github.simych.xpath.javax {
    requires transitive com.github.simych.xpath.core;
    requires java.json;

    provides com.github.simy4.xpath.spi.NavigatorSpi with com.github.simy4.xpath.json.spi.JavaxJsonNavigatorSpi;
}