package com.github.simy4.xpath.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LazyMemoizedServiceLoaderTest {

    @Test
    void isLazyAndMemoized() {
        Function<Class<ServiceLoaderTest>, Iterable<ServiceLoaderTest>> serviceLoader =
                new LazyMemoizedServiceLoader<>();

        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", null);

        Iterable<ServiceLoaderTest> services = serviceLoader.apply(ServiceLoaderTest.class);
        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", services);

        Iterable<ServiceLoaderTest> newServices = serviceLoader.apply(ServiceLoaderTest.class);
        assertThat(serviceLoader).hasFieldOrPropertyWithValue("memoized", services);
        assertThat(services).isSameAs(newServices);
    }

}