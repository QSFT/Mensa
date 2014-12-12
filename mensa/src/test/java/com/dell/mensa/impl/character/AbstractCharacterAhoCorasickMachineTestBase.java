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
import java.util.List;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.generic.AbstractAhoCorasickMachineTest;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Keywords;
import com.dell.mensa.impl.generic.Match;

/**
 * {@link AbstractCharacterAhoCorasickMachineTestBase} extends {@link AbstractAhoCorasickMachineTest} for testing
 * machines that work with {@link Character} symbols.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public abstract class AbstractCharacterAhoCorasickMachineTestBase extends AbstractAhoCorasickMachineTest<Character>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.AbstractAhoCorasickMachineTest#getRawText(com.dell.mensa.ITextSource, long,
	 * long)
	 */
	@Override
	protected String getRawText(final ITextSource<Character> textSource_, final long start_, final long end_)
	{
		final ITailBuffer<Character> tailBuffer = textSource_.getRawTailBuffer();
		final StringBuffer buf = new StringBuffer();

		for (long i = start_; i < end_ && i < tailBuffer.end(); ++i)
		{
			buf.append(tailBuffer.symbolAt(i));
		}

		return buf.toString();
	}

	/**
	 * Verifies that a machine can perform a single match.
	 *
	 * @param machine_
	 *            the machine to test
	 * @param symbols_
	 *            the symbols that define a keyword to match
	 * @param flags_
	 *            keyword option flags (e.g., to control case- or punctuation-sensitivity)
	 * @param head_
	 *            symbols that appear in the input text before the raw symbols to match; may be {@code null}
	 * @param rawSymbols_
	 *            the raw symbols to match in the input text
	 * @param tail_
	 *            symbols that appear after the raw symbols to match; may be {@code null} _
	 * @throws IOException
	 *             if there is an error accessing the text source
	 */
	protected void verifySingleMatch(final AhoCorasickMachine<Character> machine_,
			final String symbols_, final int flags_, final String head_,
			final String rawSymbols_, final String tail_)
			throws IOException
	{
		final IKeywords<Character> keywords = new Keywords<>();
		final IKeyword<Character> keyword = new CharacterKeyword(symbols_, null, flags_);
		keywords.add(keyword);

		machine_.reset();
		machine_.build(keywords);

		final StringBuffer text = new StringBuffer();

		if (head_ == null)
		{
			text.append(head_);
		}
		final int headLength = text.length();

		text.append(rawSymbols_);

		if (tail_ != null)
		{
			text.append(tail_);
		}

		final ITextSource<Character> textSource = new CharacterStringTextSource(text.toString());

		final List<Match<Character>> expectedMatches = new ArrayList<>();
		final Match<Character> match = new Match<>(machine_, keyword, headLength, headLength + rawSymbols_.length());
		add(expectedMatches, match);

		// Try matching without raw symbol reporting.
		machine_.setNotifyRawSymbols(false);
		verifyMatch(machine_, textSource, expectedMatches);

		// Try again, with raw symbol reporting.
		match.setRawSymbols(createRawSymbols(rawSymbols_));
		machine_.setNotifyRawSymbols(true);
		verifyMatch(machine_, textSource, expectedMatches);
	}

	protected static Character[] createRawSymbols(final String s_)
	{
		if (s_ == null)
		{
			return null;
		}

		final Character[] symbols = new Character[s_.length()];

		for (int i = 0; i < s_.length(); i++)
		{
			symbols[i] = s_.charAt(i);
		}

		return symbols;
	}
}
