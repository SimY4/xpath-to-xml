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
package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML elements view visitor.
 *
 * @param <N> XML node type
 * @param <T> type of return value
 * @author Alex Simkin
 * @since 1.0
 */
public interface ViewVisitor<N extends Node, T> {

  T visit(BooleanView<N> bool) throws XmlBuilderException;

  T visit(IterableNodeView<N> nodeSet) throws XmlBuilderException;

  T visit(LiteralView<N> literal) throws XmlBuilderException;

  T visit(NumberView<N> number) throws XmlBuilderException;
}
