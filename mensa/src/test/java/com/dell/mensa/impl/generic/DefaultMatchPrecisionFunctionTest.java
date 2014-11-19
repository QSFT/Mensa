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

import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.character.CharacterKeyword;
import com.dell.mensa.impl.character.CharacterStringTextSource;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class DefaultMatchPrecisionFunctionTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private static final String TEXT = "Hello!";
	private static final String KEYWORD = "Hello";
	private static final double DELTA = 0.0;

	private IMatchPrecisionFunction<Character> fn;
	private ITextSource<Character> textSource;
	private IKeyword<Character> keyword;
	private long bufferStart;
	private long bufferEnd;

	@Before
	public void setUp() throws Exception
	{
		fn = new DefaultMatchPrecisionFunction<>();
		textSource = new CharacterStringTextSource(TEXT);
		keyword = new CharacterKeyword(KEYWORD);

		textSource.open();
		while (!textSource.isEof())
		{
			textSource.read();
		}

		bufferStart = textSource.getTailBuffer().start();
		bufferEnd = textSource.getTailBuffer().end();
	}

	@After
	public void tearDown() throws IOException
	{
		textSource.close();
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for
	 * {@link com.dell.mensa.impl.generic.DefaultMatchPrecisionFunction#eval(com.dell.mensa.IKeyword, com.dell.mensa.ITextSource, long, long)}
	 * .
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testEval() throws IOException
	{
		for (long start = bufferStart; start <= bufferEnd; ++start)
		{
			for (long end = start; end <= bufferEnd; ++end)
			{
				final double expectedPrecision = start < end
						? IMatchPrecisionFunction.MAX_PRECISION : IMatchPrecisionFunction.MIN_PRECISION;
				Assert.assertEquals(expectedPrecision, fn.eval(keyword, textSource, start, end), DELTA);
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEval_NullKeyword()
	{
		fn.eval(null, textSource, bufferStart, bufferEnd);
	}

	@Test(expected = IllegalStateException.class)
	public void testEval_TextSourceNotOpen() throws IOException
	{
		textSource.close();
		fn.eval(keyword, textSource, bufferStart, bufferEnd);
	}

	@Test(expected = IllegalStateException.class)
	public void testEval_BadRange() throws IOException
	{
		textSource.close();
		fn.eval(keyword, textSource, bufferStart + 1, bufferStart);
	}

	@Test(expected = IllegalStateException.class)
	public void testEval_BadRangeOverOutOfBounds() throws IOException
	{
		textSource.close();
		fn.eval(keyword, textSource, bufferStart - 1, bufferStart - 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testEval_StartUnderflow()
	{
		fn.eval(keyword, textSource, bufferStart - 1, bufferEnd);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testEval_EndOverflow()
	{
		fn.eval(keyword, textSource, bufferStart, bufferEnd + 1);
	}
}
