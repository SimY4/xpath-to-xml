module com.github.simych.xpath.jackson {
    requires transitive com.github.simych.xpath.core;
    requires com.fasterxml.jackson.databind;

    provides com.github.simy4.xpath.spi.NavigatorSpi with com.github.simy4.xpath.jackson.spi.JacksonNavigatorSpi;
}