package com.github.simy4.xpath.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LazyMemoizedServiceLoaderTest {

    @Test
    void isLazyAndMemoized() {
        var serviceLoader = new LazyMemoizedServiceLoader<ServiceLoaderTest>();

        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", null);

        var services = serviceLoader.apply(ServiceLoaderTest.class);
        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", services);

        var newServices = serviceLoader.apply(ServiceLoaderTest.class);
        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", services);
        assertThat(services).isSameAs(newServices);
    }

}