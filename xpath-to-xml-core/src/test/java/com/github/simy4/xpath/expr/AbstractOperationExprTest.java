package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.helpers.SerializationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractOperationExprTest {

    @Mock(serializable = true) Expr leftExpr;
    @Mock(serializable = true) Expr rightExpr;

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
        assertThat(operationExpr).hasToString(leftExpr.toString() + operationExpr.operator() + rightExpr.toString());
    }

}