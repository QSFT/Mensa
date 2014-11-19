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
 * {@link IOutputFunction} specifies the <i>output function</i> interface. An output function maps a given state to a
 * set of keywords, if any, for that state (via the {@link #output(int)} method).
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IOutputFunction<S>
{
	/**
	 * Returns the output keywords, if any, for a specified state.
	 *
	 * @param state_
	 *            the specified state.F
	 * @return Returns a non-empty IKeywords or {@code null}.
	 */
	IKeywords<S> output(final int state_);

	/**
	 * Adds a keyword a given state.
	 *
	 * @param state_
	 *            specifies the state whose output keywords are to be updated.
	 * @param keyword_
	 *            specifies the keyword to add.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified state is negative or if the specified keyword is {@code null}
	 */
	void put(int state_, IKeyword<S> keyword_);

	/**
	 * Adds a set of keywords a given state.
	 *
	 * @param state_
	 *            specifies the state whose output keywords are to be updated.
	 * @param keywords_
	 *            specifies the keyword set to add; may be {@code null} or empty.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified state is negative
	 */
	void put(int state_, IKeywords<S> keywords_);
}
