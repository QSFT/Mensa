/*******************************************************************************
 * Copyright (C) 2014 Dell, Inc.
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
 *******************************************************************************/
package com.dell.mensa.example;

import java.io.IOException;
import java.util.Iterator;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchListener;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.character.CharacterAhoCorasickMachine;
import com.dell.mensa.impl.character.CharacterKeyword;
import com.dell.mensa.impl.character.CharacterStringTextSource;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Keywords;

/**
 * A simple example showing how to use {@link CharacterAhoCorasickMachine} to match character keywords within text.
 * Matching is first performed using a match iterator, then performed again using a match listener (callback). Results
 * are written to standard out.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public final class Example1
{
	/**
	 * This is is the source texts that will be searched for keywords.
	 */
	private static final String TEXT =
			"Darkness at the break of noon\n" +
					"Shadows even the silver spoon\n" +
					"The hand-made blade, the child's balloon\n" +
					"Eclipses both the sun and moon\n" +
					"To understand you know too soon\n" +
					"There is no sense in trying.";

	/**
	 * These are the keywords to search for in the source text.
	 */
	private static final String[] KEYWORDS =
	{
			"is", "the"
	};

	private Example1()
	{
		// Do not instantiate
	}

	/**
	 * The program entry point; there are no command line arguments.
	 *
	 * @param args_
	 *            unused
	 * @throws IOException
	 */
	public static void main(final String[] args_) throws IOException
	{
		// Construct the machine instance.
		final CharacterAhoCorasickMachine machine = new CharacterAhoCorasickMachine();

		// Initialize the machine for the desired keywords.
		final IKeywords<Character> keywords = new Keywords<>();
		for (final String keyword : KEYWORDS)
		{
			keywords.add(new CharacterKeyword(keyword));
		}
		machine.build(keywords);

		// Define the input text source.
		final ITextSource<Character> textSource = new CharacterStringTextSource(TEXT);

		// Run the machine to perform matching.
		matchUsingAnIterator(machine, textSource);
		matchUsingACallback(machine, textSource);
	}

	/**
	 * Demonstrates matching using an iterator.
	 *
	 * @param machine_
	 *            the matching machine, which must already be initialized with the desired keywords.
	 * @param textSource_
	 *            the text source to match against.
	 * @throws IOException
	 *             if an error occurs opening or reading the text source.
	 */
	private static void matchUsingAnIterator(
			final CharacterAhoCorasickMachine machine_,
			final ITextSource<Character> textSource_) throws IOException
	{
		println("Match Using an Iterator");
		println("-----------------------");
		textSource_.open();
		try
		{
			final Iterator<IMatch<Character>> iterator = machine_.matchIterator(textSource_);
			while (iterator.hasNext())
			{
				printMatch(iterator.next());
			}
		}
		finally
		{
			textSource_.close();
		}
		println("Done.\n");
	}

	/**
	 * Demonstrates matching using a callback.
	 *
	 * @param machine_
	 *            the matching machine, which must already be initialized with the desired keywords.
	 * @param textSource_
	 *            the text source to match against.
	 * @throws IOException
	 *             if an error occurs opening or reading the text source.
	 */
	private static void matchUsingACallback(
			final CharacterAhoCorasickMachine machine_,
			final ITextSource<Character> textSource_) throws IOException
	{
		/**
		 * The callback that will be receive notifications as the match operation proceeds.
		 */
		final IMatchListener<Character> listener = new IMatchListener<Character>()
		{
			@Override
			public boolean notifyBeginMatching(final AhoCorasickMachine<Character> machine_)
			{
				println("Match Using a Callback");
				println("----------------------");
				return true;
			}

			@Override
			public void notifyEndMatching(final AhoCorasickMachine<Character> machine_)
			{
				println("Done.\n");
			}

			@Override
			public boolean notifyMatch(final IMatch<Character> match_)
			{
				printMatch(match_);
				return true;
			}
		};

		textSource_.open();
		try
		{
			machine_.match(textSource_, listener);
		}
		finally
		{
			textSource_.close();
		}
	}

	private static void printMatch(final IMatch<Character> match_)
	{
		println(match_.toString());
	}

	private static void println(final String s_)
	{
		System.out.println(s_);
	}
}
