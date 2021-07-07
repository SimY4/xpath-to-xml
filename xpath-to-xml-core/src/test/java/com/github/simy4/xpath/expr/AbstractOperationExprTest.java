/*
 * Copyright 2021 Alex Simkin
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
package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.helpers.SerializationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractOperationExprTest {

  @Mock(serializable = true)
  Expr leftExpr;

  @Mock(serializable = true)
  Expr rightExpr;

  AbstractOperationExpr operationExpr;

  @Test
  @DisplayName("Should serialize and deserialize expr")
  void shouldSerializeAndDeserializeExpr() throws IOException, ClassNotFoundException {
    // when
    Expr deserializedAxis = SerializationHelper.serializeAndDeserializeBack(operationExpr);

    // then
    assertThat(deserializedAxis).hasToString(operationExpr.toString());
  }

  @Test
  void testToString() {
    assertThat(operationExpr)
        .hasToString(leftExpr.toString() + operationExpr.operator() + rightExpr.toString());
  }
}
