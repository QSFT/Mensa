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

import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Match;

/**
 * {@link Match} is an interface for reading properties of a {@link AhoCorasickMachine} match.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IMatch<S extends Comparable<S>>
{
	/**
	 * Implementations must implement both {@link #equals(Object)} and {@link #hashCode()} to provide value semantics
	 * based on all match properties <i>except</i> {@link #getRawSymbols()}.
	 *
	 * @param other_
	 *            the object to test for equality
	 *
	 * @return Returns {@code true} if the other object is an {@link IMatch} instance with the same property values
	 *         (other than {@link #getRawSymbols()}) as this instance; {@code false} otherwise.
	 */
	@Override
	boolean equals(Object other_);

	/**
	 * Gets the position in the input text of the first symbol after the matched keyword.
	 *
	 * @return Returns the end position.
	 *
	 * @see #getStart()
	 */
	long getEnd();

	/**
	 * Gets the matched keyword.
	 *
	 * @return Returns the matched keyword.
	 */
	IKeyword<S> getKeyword();

	/**
	 * Gets the machine that performed the match.
	 *
	 * @return Returns the machine that performed the match.
	 */
	AhoCorasickMachine<S> getMachine();

	/**
	 * Gets the precision metric for this match.
	 *
	 * @return Returns the precision metric for this match.
	 *
	 * @see IMatchPrecisionFunction#eval(IKeyword, ITextSource, long, long)
	 */
	double getPrecision();

	/**
	 * Gets the raw symbols from the input text that were used for this match, if available.
	 *
	 * <p>
	 * The raw symbols might be different than the symbols contained in the keyword returned by {@link #getKeyword()}.
	 * The reason for this is that {@link #getKeyword()} returns a canonical keyword, while this method returns the
	 * symbols that actually appeared in the input text. The two may differ if any type of fuzzy matching was used
	 * (e.g., case-insensitivity, whitespace normalization, etc.).
	 * </p>
	 *
	 * @return Returns the raw symbols from the input text used for this match, or {@code null} if the raw symbols are
	 *         not available.
	 */
	S[] getRawSymbols();

	/**
	 * Gets the position in the input text of the first symbol of the matched the keyword. Thus, the symbols matched by
	 * they keyword are in the range [{@link #getStart()} , {@link #getEnd()}) in the input text. Note that the length
	 * of this range may be longer than the length of the keyword returned by {@link #getKeyword()}. This can happen if
	 * the extra symbols were consumed in the matching process (e.g., multiple space characters might have been consumed
	 * to match a single space in the keyword).
	 *
	 * @return Returns the start position.
	 */
	long getStart();

	/**
	 * Implementations must implement both {@link #equals(Object)} and {@link #hashCode()} to provide value semantics
	 * based on all match properties <i>except</i> {@link #getRawSymbols()}.
	 *
	 * @return Returns an integer hash code for this instance.
	 */
	@Override
	int hashCode();
}
