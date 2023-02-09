/*
 * Copyright 2017-2021 Alex Simkin
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
package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.helpers.SerializationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.XMLConstants;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomNodeTest {

  @Mock(serializable = true)
  private org.w3c.dom.Node node;

  private DomNode nodeView;

  @BeforeEach
  void setUp() {
    nodeView = new DomNode(node);
  }

  @Test
  void shouldReturnNodeNameForNamespaceUnawareNode() {
    when(node.getNodeName()).thenReturn("node");
    var result = nodeView.getName();
    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
  }

  @Test
  void shouldReturnNodeNameForNamespaceAwareNode() {
    when(node.getNamespaceURI()).thenReturn("http://www.example.com/my");
    when(node.getLocalName()).thenReturn("node");
    when(node.getPrefix()).thenReturn("my");
    var result = nodeView.getName();
    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly("http://www.example.com/my", "node", "my");
  }

  @Test
  void shouldReturnNodeTextContent() {
    when(node.getTextContent()).thenReturn("text");

    assertThat(nodeView.getText()).isEqualTo("text");
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
    // given
    when(node.getNodeName()).thenReturn("node");

    // when
    var deserializedNode = SerializationHelper.serializeAndDeserializeBack(nodeView);

    // then
    assertThat(deserializedNode.getName()).isEqualTo(nodeView.getName());
  }
}
