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
  @SuppressWarnings({"unchecked", "BanSerializableRead"})
  public static <T> T serializeAndDeserializeBack(T t) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new ObjectOutputStream(out).writeObject(t);
    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    return (T) new ObjectInputStream(in).readObject();
  }

  private SerializationHelper() {}
}
