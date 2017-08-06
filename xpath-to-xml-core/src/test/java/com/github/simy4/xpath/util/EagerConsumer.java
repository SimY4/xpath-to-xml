package com.github.simy4.xpath.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("JavadocMethod")
public final class EagerConsumer {

    public static <T> Collection<T> consume(Iterable<T> iterable) {
        List<T> eager = new ArrayList<T>();
        for (T anIterable : iterable) {
            eager.add(anIterable);
        }
        return Collections.unmodifiableList(eager);
    }

}
