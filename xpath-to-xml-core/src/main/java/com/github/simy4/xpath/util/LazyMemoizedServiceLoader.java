package com.github.simy4.xpath.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ServiceLoader;

public final class LazyMemoizedServiceLoader<T> implements Function<Class<T>, Iterable<T>> {

    private volatile Collection<T> memoized;

    @Override
    public Iterable<T> apply(Class<T> clazz) {
        return memoized == null ? loadAndMemoize(clazz) : memoized;
    }

    private synchronized Iterable<T> loadAndMemoize(Class<T> clazz) {
        if (memoized == null) {
            final ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
            final Collection<T> services = new ArrayList<T>();
            for (T service : serviceLoader) {
                services.add(service);
            }
            memoized = Collections.unmodifiableCollection(services);
        }
        return memoized;
    }

}
