package com.github.simy4.xpath.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationHelper {

    @SuppressWarnings("unchecked")
    public static <T> T serializeAndDeserializeBack(T t) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(t);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return (T) new ObjectInputStream(in).readObject();
    }

    private SerializationHelper() {
    }

}
