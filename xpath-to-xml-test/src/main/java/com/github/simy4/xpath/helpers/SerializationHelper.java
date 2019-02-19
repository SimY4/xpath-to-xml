package com.github.simy4.xpath.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serialization and deserialization tester.
 *
 * @author Alex Simkin
 */
public final class SerializationHelper {

    /**
     * Serializes and deserializes given object back.
     *
     * @param t object to serialize and deserialize
     * @param <T> serialized type
     * @return deserialized copy
     * @throws ClassNotFoundException if serialized object cannon be found
     * @throws IOException if any exception thrown by the underlying OutputStream
     */
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
