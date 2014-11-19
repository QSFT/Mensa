/*******************************************************************************
 * Copyright (C) 2014 Dell, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.dell.mensa;

/**
 * {@link INextMoveFunction} is a marker interface that identifies a deterministic {@link IGotoFunction} that contains
 * no failure transitions. That is, {@link #eval(int, Object)} returns state for all symbol values.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface INextMoveFunction<S> extends IGotoFunction<S>
{
	// Marker interface; no additional methods
}
