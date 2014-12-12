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
package com.dell.mensa.example;

import java.io.IOException;
import java.util.Iterator;
import com.dell.mensa.IFactory;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchListener;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.character.CharacterAhoCorasickMachine;
import com.dell.mensa.impl.generic.AbstractTextSource;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Factory;
import com.dell.mensa.impl.generic.Keyword;
import com.dell.mensa.impl.generic.Keywords;

/**
 * This example is based on {@link Example1}, instead of matching sequences of characters using
 * {@link CharacterAhoCorasickMachine}, this example uses {@link AhoCorasickMachine} directly to match sequences of
 * <i>words</i>.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public final class Example2
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
	 * A keyword composed of two symbols: "at" and "the"
	 */
	private static final String[] KEYWORD1 =
	{
			"at", "the"
	};

	/**
	 * A keyword composed of three symbols: "sun", "and", and "moon"
	 */
	private static final String[] KEYWORD2 =
	{
			"sun", "and", "moon"
	};

	private Example2()
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
		final IFactory<String> factory = new Factory<String>();
		final AhoCorasickMachine<String> machine = new AhoCorasickMachine<>(factory);

		final IKeywords<String> keywords = new Keywords<>();
		keywords.add(new Keyword<String>(KEYWORD1));
		keywords.add(new Keyword<String>(KEYWORD2));

		machine.build(keywords);

		final ITextSource<String> textSource = new MyTextSource();

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
			final AhoCorasickMachine<String> machine_,
			final ITextSource<String> textSource_) throws IOException
	{
		println("Match Using an Iterator");
		println("-----------------------");
		textSource_.open();
		try
		{
			final Iterator<IMatch<String>> iterator = machine_.matchIterator(textSource_);
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
	 * @param textSource
	 *            the text source to match against.
	 * @throws IOException
	 *             if an error occurs opening or reading the text source.
	 */
	private static void matchUsingACallback(
			final AhoCorasickMachine<String> machine_,
			final ITextSource<String> textSource) throws IOException
	{
		/**
		 * The callback that will be receive notifications as the match operation proceeds.
		 */
		final IMatchListener<String> listener = new IMatchListener<String>()
		{
			@Override
			public boolean notifyBeginMatching(final AhoCorasickMachine<String> machine_)
			{
				println("Match Using a Callback");
				println("----------------------");
				return true;
			}

			@Override
			public void notifyEndMatching(final AhoCorasickMachine<String> machine_)
			{
				println("Done.\n");
			}

			@Override
			public boolean notifyMatch(final IMatch<String> match_)
			{
				printMatch(match_);
				return true;
			}
		};

		textSource.open();
		try
		{
			machine_.match(textSource, listener);
		}
		finally
		{
			textSource.close();
		}
	}

	private static void printMatch(final IMatch<String> iMatch)
	{
		println(iMatch.toString());
	}

	private static void println(final String s_)
	{
		System.out.println(s_);
	}

	/**
	 * This a custom {@link ITextSource} used to read {@link Example2#TEXT} as a sequence of {@link String} words
	 * (delimited by white space and/or punctuation).
	 *
	 * <p>
	 * NOTE: You can always implement {@link ITextSource} from scratch, but it is usually easier to extend
	 * {@link AbstractTextSource}, as is done here.
	 * </p>
	 */
	private static class MyTextSource extends AbstractTextSource<String>
	{
		/**
		 * The input text parsed into {@link String} words.
		 */
		private String[] symbols;

		/**
		 * The index of the next available symbol to be read.
		 */
		private int position;

		@Override
		protected void closeImpl() throws IOException
		{
			symbols = null;
		}

		@Override
		protected void openImpl() throws IOException
		{
			symbols = TEXT.split("[-,. \\t\\n]+");
			position = 0;
		}

		@Override
		protected String readImpl(final ITailBuffer<String> buffer_) throws IOException
		{
			if (position == symbols.length)
			{
				return null; // eof reached
			}

			final String symbol = symbols[position++];
			buffer_.add(symbol);

			return symbol;
		}
	}
}
