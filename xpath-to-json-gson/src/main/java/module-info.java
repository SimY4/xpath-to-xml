module com.github.simych.xpath.gson {
  requires transitive com.github.simych.xpath.core;
  requires com.google.gson;

  exports com.github.simy4.xpath.gson.spi;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.gson.spi.GsonNavigatorSpi;
}
