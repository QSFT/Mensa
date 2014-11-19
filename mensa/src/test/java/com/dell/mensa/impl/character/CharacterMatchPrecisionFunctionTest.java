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

package com.dell.mensa.impl.character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ITextSource;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
@RunWith(Parameterized.class)
public class CharacterMatchPrecisionFunctionTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private static final double MAX = IMatchPrecisionFunction.MAX_PRECISION;
	private static final double DELTA = 0.0000001;
	private static final IMatchPrecisionFunction<Character> fn = new CharacterSymbolClassifier().getMatchPrecisionFunction();
	private final IKeyword<Character> keyword;
	private final ITextSource<Character> textSource;
	private final long start;
	private final long end;
	private final double expected;

	private static class TestCase
	{
		public final String keyword;
		public final String text;
		public final long start;
		public final long end;
		public final double expected;

		public TestCase(final String keyword_, final String text_, final long start_, final long end_, final double expected_)
		{
			super();
			this.keyword = keyword_;
			this.text = text_;
			this.start = start_;
			this.end = end_;
			this.expected = expected_;
		}

		public TestCase(final String keyword_, final String text_, final double expected_)
		{
			this(keyword_, text_, 0, text_.length(), expected_);
		}

		public TestCase(final String keyword_, final double expected_)
		{
			this(keyword_, keyword_, expected_);
		}
	}

	// =========================================================================
	// Parameters
	// =========================================================================
	@Parameterized.Parameters
	public static Collection<Object[]> generateData()
	{
		final Collection<Object[]> data = new ArrayList<>();

		add(data, new TestCase("a", MAX));
		add(data, new TestCase("a", "A", lcMatch()));
		add(data, new TestCase("A", "a", lcMatch()));

		add(data, new TestCase("Abc", MAX));
		add(data, new TestCase("Abc", "ABC", (MAX + 2 * lcMatch()) / 3));
		add(data, new TestCase("Abc", "aBC", lcMatch()));

		String keyword = "abc def";
		add(data, new TestCase(keyword, MAX));
		add(data, new TestCase(keyword, "abc  def", (6 * MAX + white(1)) / 7));
		add(data, new TestCase(keyword, "abc   def", (6 * MAX + white(2)) / 7));
		add(data, new TestCase(keyword, "abc    def", (6 * MAX + white(3)) / 7));

		keyword = "a b c";
		add(data, new TestCase(keyword, MAX));
		add(data, new TestCase(keyword, "a  b c", (MAX + white(1) + MAX + white(0) + MAX) / 5));
		add(data, new TestCase(keyword, "a b  c", (MAX + white(0) + MAX + white(1) + MAX) / 5));
		add(data, new TestCase(keyword, "a  b  c", (MAX + white(1) + MAX + white(1) + MAX) / 5));
		add(data, new TestCase(keyword, "a   b    c", (MAX + white(2) + MAX + white(3) + MAX) / 5));

		keyword = "a\tb";
		add(data, new TestCase(keyword, MAX));
		add(data, new TestCase(keyword, "a b", MAX));
		add(data, new TestCase(keyword, "a  b", (MAX + white(1) + MAX) / 3));

		keyword = "a.b";
		add(data, new TestCase(keyword, MAX));
		add(data, new TestCase(keyword, "a,b", (MAX + puncutationIgnored(0) + MAX) / 3));
		add(data, new TestCase(keyword, "a, b", (MAX + puncutationIgnored(1) + MAX) / 3));
		add(data, new TestCase(keyword, "a,  b", (MAX + puncutationIgnored(2) + MAX) / 3));
		add(data, new TestCase(keyword, "a ,b", (MAX + puncutationIgnored(1) + MAX) / 3));
		add(data, new TestCase(keyword, "a  ,b", (MAX + puncutationIgnored(2) + MAX) / 3));
		add(data, new TestCase(keyword, "a , b", (MAX + puncutationIgnored(2) + MAX) / 3));
		add(data, new TestCase(keyword, "a  ,  b", (MAX + puncutationIgnored(4) + MAX) / 3));
		add(data, new TestCase(keyword, "a b", (MAX + puncutationOmitted(0) + MAX) / 3));
		add(data, new TestCase(keyword, "a  b", (MAX + puncutationOmitted(1) + MAX) / 3));
		add(data, new TestCase(keyword, "a   b", (MAX + puncutationOmitted(2) + MAX) / 3));
		add(data, new TestCase(keyword, "a\tb", (MAX + puncutationOmitted(0) + MAX) / 3));
		add(data, new TestCase(keyword, "a\t\tb", (MAX + puncutationOmitted(1) + MAX) / 3));
		add(data, new TestCase(keyword, "a\n\t\tb", (MAX + puncutationOmitted(2) + MAX) / 3));

		return data;
	}

	private static void add(final Collection<Object[]> data_, final TestCase testCase_)
	{
		final Object[] parms = new Object[1];
		parms[0] = testCase_;
		data_.add(parms);
	}

	private static double decay(final double precision_, final double extra_)
	{
		return precision_ / (CharacterMatchPrecisionFunction.EXTRA_DECAY_RATE * extra_ + 1);
	}

	/**
	 * @return
	 */
	private static double lcMatch()
	{
		return CharacterMatchPrecisionFunction.NORMAL_LOWERCASE_MATCH;
	}

	private static double white(final int extra_)
	{
		return decay(CharacterMatchPrecisionFunction.WHITE_SPACE_EXACT_MATCH, extra_);
	}

	private static double puncutationIgnored(final int extra_)
	{
		return decay(CharacterMatchPrecisionFunction.PUNCTUATION_IGNORED, extra_);
	}

	private static double puncutationOmitted(final int extra_)
	{
		return decay(CharacterMatchPrecisionFunction.PUNCTUATION_OMITTED, extra_);
	}

	public CharacterMatchPrecisionFunctionTest(final TestCase testCase_)
	{
		super();

		this.keyword = new CharacterKeyword(testCase_.keyword);
		this.textSource = new CharacterStringTextSource(testCase_.text);
		this.start = testCase_.start;
		this.end = testCase_.end;
		this.expected = testCase_.expected;
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link CharacterMatchPrecisionFunction#eval(IKeyword, ITextSource, long, long)}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testEval() throws IOException
	{
		textSource.open();
		while (!textSource.isEof())
		{
			textSource.read();
		}

		Assert.assertEquals(
				String.format("unexpected precision for match %s (start=%d, end=%d", keyword, start, end),
				expected,
				fn.eval(keyword, textSource, start, end),
				DELTA);

		textSource.close();
	}
}
