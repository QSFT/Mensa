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
 * {@link IMatchPrecisionFunction} specifies the interface used to compute <i>match precisions</i> returned by
 * {@link IMatch#getPrecision()}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IMatchPrecisionFunction<S>
{
	/**
	 * The minimum precision value.
	 */
	double MIN_PRECISION = 0.0;

	/**
	 * The maximum precision value.
	 */
	double MAX_PRECISION = 1.0;

	/**
	 * Computes the precision metric for a specific match.
	 *
	 * <p>
	 * By definition, <i>match precision</i> (sometimes referred to as <i>strength of match</i>) is a number in the
	 * range [{@value #MIN_PRECISION}, {@value #MAX_PRECISION}] where {@value #MAX_PRECISION} indicates an exact match
	 * and progressively lesser values indicate progressively less precise matches.
	 * </p>
	 *
	 * @param keyword_
	 *            the matched keyword
	 * @param textSource_
	 *            the text source in which the keyword was matched
	 * @param start_
	 *            the keyword starting position (i.e., position of first matched symbol
	 * @param end_
	 *            the keyword ending position (i.e., position of after last symbol of match)
	 *
	 * @return Returns a precision value in the range [{@value #MIN_PRECISION}, {@value #MAX_PRECISION}].
	 *
	 * @throws IllegalArgumentException
	 *             thrown if the keyword or text source is {@code null}.
	 *
	 * @throws IllegalStateException
	 *             thrown if the text source is not open or if the starting position is beyond the ending position.
	 *
	 * @throws IndexOutOfBoundsException
	 *             thrown if the starting and/or ending position is not contained the text source tail buffers.
	 */
	double eval(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_);
}
