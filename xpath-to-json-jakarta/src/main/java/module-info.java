module com.github.simych.xpath.javax {
  requires transitive com.github.simych.xpath.core;
  requires jakarta.json;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.json.spi.JakartaJsonNavigatorSpi;
}
