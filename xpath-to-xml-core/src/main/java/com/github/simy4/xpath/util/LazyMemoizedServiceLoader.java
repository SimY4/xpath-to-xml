/*
 * Copyright 2019-2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
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

  private synchronized Iterable<T> loadAndMemoize(final Class<T> clazz) {
    if (memoized == null) {
      memoized = AccessController.doPrivileged(new ServiceLoaderAction<T>(clazz));
    }
    return memoized;
  }

  private static final class ServiceLoaderAction<T> implements PrivilegedAction<Collection<T>> {
    private final Class<T> clazz;

    private ServiceLoaderAction(Class<T> clazz) {
      this.clazz = clazz;
    }

    @Override
    public Collection<T> run() {
      final ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
      final Collection<T> services = new ArrayList<T>();
      for (T service : serviceLoader) {
        services.add(service);
      }
      return Collections.unmodifiableCollection(services);
    }
  }
}
