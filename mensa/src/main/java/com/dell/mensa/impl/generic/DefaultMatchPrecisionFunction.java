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
package com.dell.mensa.impl.generic;

import com.dell.mensa.IKeyword;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.util.Verify;

/**
 * {@link DefaultMatchPrecisionFunction} is a default {@link IMatchPrecisionFunction} implementation that performs the
 * simplest possible precision calculation. This class also may also serve as a convenient base class for more
 * sophisticated implementations, by providing standard parameter validation logic.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class DefaultMatchPrecisionFunction<S> implements IMatchPrecisionFunction<S>
{
	private static final String PARM_end = "end_";
	private static final String PARM_keyword = "keyword_";
	private static final String PARM_start = "start_";
	private static final String PARM_textSource = "textSource_";
	private static final String MSG_MATCH_RANGE_MUST_BE_VALID = "match range must be valid (i.e., start_ <= end_)";
	private static final String MSG_TEXT_SOURCE_MUST_BE_OPEN = "text source must be open";

	/**
	 * Computes the precision metric for a specific match using a very simple rule: If the length of the match is zero,
	 * the precision is {@value #MIN_PRECISION}; otherwise, the precision is {@value #MAX_PRECISION}.
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
	@Override
	public double eval(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_)
	{
		Verify.notNull(keyword_, PARM_keyword);
		Verify.notNull(textSource_, PARM_textSource);
		Verify.condition(start_ <= end_, MSG_MATCH_RANGE_MUST_BE_VALID);
		Verify.condition(textSource_.isOpen(), MSG_TEXT_SOURCE_MUST_BE_OPEN);

		final ITailBuffer<S> buffer = textSource_.getTailBuffer();
		final long bufferStart = buffer.start();
		final long bufferEnd = buffer.end();

		Verify.inClosedRange(start_, bufferStart, bufferEnd, PARM_start);
		Verify.inClosedRange(end_, bufferStart, bufferEnd, PARM_end);

		return evalImpl(keyword_, textSource_, start_, end_);
	}

	/**
	 * Performs actual precision evaluation after parameter validation has occurred. A derived class may override this
	 * method to perform custom precision evaluation without worrying about parameter validation.
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
	protected double evalImpl(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_)
	{
		return start_ < end_ ? MAX_PRECISION : MIN_PRECISION;
	}
}
