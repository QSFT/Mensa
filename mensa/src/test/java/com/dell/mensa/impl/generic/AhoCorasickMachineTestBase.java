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
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ITextSource;
import com.dell.mensa.util.Verify;

/**
 * {@link AhoCorasickMachineTestBase} is an abstract base class that simplifies writing unit test for
 * {@link AhoCorasickMachine} or derived classes.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public abstract class AhoCorasickMachineTestBase<S extends Comparable<S>>
{
	private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	protected abstract class AbstractMatcher implements IMatcher<S>
	{
		protected final AhoCorasickMachine<S> wrappedMachine;
		protected int numMatches;

		/**
		 * @param machine_
		 *            the machine being tested
		 */
		protected AbstractMatcher(final AhoCorasickMachine<S> machine_)
		{
			super();
			this.wrappedMachine = machine_;
			this.numMatches = -1;
		}

		@Override
		public int getNumMatches()
		{
			return this.numMatches;
		}

		protected MatchCollector<S> logAndReturn(final MatchCollector<S> matchCollector_)
		{
			logMatches(getClass().getSimpleName(), matchCollector_);
			logCalls("goto", wrappedMachine.getGotoFunction());
			logCalls("next move", wrappedMachine.getNextMoveFunction());
			return matchCollector_;
		}
	}

	/**
	 * {@link com.dell.mensa.impl.generic.AhoCorasickMachineTestBase.IMatcher} defines an abstraction that layer used to
	 * hide the matching API used by an {@link AhoCorasickMachine} instance. This simplifies writing tests capable of
	 * testing matching using either the lister or iterator methods.
	 *
	 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
	 *
	 */
	protected interface IMatcher<S extends Comparable<S>>
	{
		int getNumMatches();

		MatchCollector<S> match(ITextSource<S> textSource_) throws IOException;
	}

	protected class IteratorMatcher extends AbstractMatcher
	{
		public IteratorMatcher(final AhoCorasickMachine<S> machine_)
		{
			super(machine_);
		}

		@Override
		public MatchCollector<S> match(final ITextSource<S> textSource_) throws IOException
		{
			final MatchCollector<S> matchCollector = new MatchCollector<>();

			matchCollector.notifyBeginMatching(wrappedMachine);

			final Iterator<IMatch<S>> iterator = wrappedMachine.matchIterator(textSource_);
			while (iterator.hasNext())
			{
				matchCollector.notifyMatch(iterator.next());
			}

			matchCollector.notifyEndMatching(wrappedMachine);

			numMatches = matchCollector.getMatches().size();

			return logAndReturn(matchCollector);
		}
	}

	protected class ListenerMatcher extends AbstractMatcher
	{
		private final int limit;

		public ListenerMatcher(final AhoCorasickMachine<S> machine_)
		{
			this(machine_, MatchCollector.DEFAULT_LIMIT);
		}

		public ListenerMatcher(final AhoCorasickMachine<S> machine_, final int limit_)
		{
			super(machine_);
			this.limit = limit_;
		}

		@Override
		public MatchCollector<S> match(final ITextSource<S> textSource_) throws IOException
		{
			final MatchCollector<S> matchCollector = new MatchCollector<>();
			matchCollector.setLimit(limit);
			numMatches = wrappedMachine.match(textSource_, matchCollector);
			return logAndReturn(matchCollector);
		}
	}

	/**
	 * Conditionally adds a match to a list of matches.
	 *
	 * @param bCondition_
	 *            specifies whether or not the match should be added.
	 * @param matches_
	 *            the list of matches to (conditionally) update.
	 * @param match_
	 *            the match to (conditionally) add to the list of matches.
	 */
	protected void add(final List<Match<S>> matches_, final Match<S> match_, final boolean bCondition_)
	{
		assert matches_ != null;
		assert match_ != null;

		if (bCondition_)
		{
			matches_.add(match_);
		}
	}

	/**
	 * Adds a match to a list of matches.
	 *
	 * @param matches_
	 *            the list of matches to (conditionally) update.
	 * @param match_
	 *            the match to (conditionally) add to the list of matches.
	 */
	protected void add(final List<Match<S>> matches_, final Match<S> match_)
	{
		assert matches_ != null;
		assert match_ != null;

		matches_.add(match_);
	}

	protected void logCalls(final String label_, final IGotoFunction<S> function_)
	{
		if (function_ != null)
		{
			final String msg = String.format("%s calls: %d", label_, function_.getCalls());
			logMsg(msg);
		}
	}

	protected void logMatches(final String matcherClass_, final MatchCollector<S> matchCollector_)
	{
		if (log.isTraceEnabled())
		{
			logMsg(String.format("%s: %s", matcherClass_, matchCollector_.toString()));
		}
	}

	protected static void logMsg(final String msg_)
	{
		if (log.isTraceEnabled())
		{
			log.trace(msg_);
		}
	}

	protected static void unexpected(final IllegalStateException e)
	{
		Assert.fail("unexpected exception: " + e.getMessage());
	}

	/**
	 * Runs a matching machine several times--once using an iterator, once using an unlimited listener, and several
	 * times using limited listeners--against a text source, each time verifying that it produced the expected matches.
	 *
	 * @param machine_
	 *            the machine to run
	 * @param textSource_
	 *            the text source to match against
	 * @param expectedMatches_
	 *            the expected matches
	 *
	 * @throws IOException
	 *             if there is an error accessing the text source
	 */
	protected void verifyMatch(
			final AhoCorasickMachine<S> machine_,
			final ITextSource<S> textSource_,
			final List<Match<S>> expectedMatches_)
			throws IOException
	{
		assert machine_ != null;
		assert textSource_ != null;
		assert expectedMatches_ != null;

		// Verify the matches using a iterator
		verifyMatch(new IteratorMatcher(machine_), textSource_, expectedMatches_);

		// Verify the matches using a listener
		verifyMatch(new ListenerMatcher(machine_), textSource_, expectedMatches_);

		// Verify the matches using a listener with varying limits.
		int limit = 0;
		final List<Match<S>> limitedExpectedMatches = new ArrayList<>();

		while (limit < expectedMatches_.size())
		{
			verifyMatch(new ListenerMatcher(machine_, limit), textSource_, limitedExpectedMatches);
			limitedExpectedMatches.add(expectedMatches_.get(limit++));
		}

		verifyMatch(new ListenerMatcher(machine_, 2 * expectedMatches_.size()), textSource_, expectedMatches_);
	}

	/**
	 * Runs a matching machine against a text source, and then verifies that it produced the expected matches.
	 *
	 * @param matcher_
	 *            the matcher use for the test
	 * @param textSource_
	 *            the text source to match against
	 * @param expectedMatches_
	 *            the expected matches
	 *
	 * @throws IOException
	 *             if there is an error accessing the text source
	 */
	protected void verifyMatch(final IMatcher<S> matcher_, final ITextSource<S> textSource_, final List<Match<S>> expectedMatches_) throws IOException
	{
		assert matcher_ != null;
		assert textSource_ != null;
		assert expectedMatches_ != null;

		textSource_.open();

		final MatchCollector<S> matchCollector = matcher_.match(textSource_);

		final List<IMatch<S>> actualMatches = matchCollector.getMatches();

		final String matchDetails = String.format("Expected matches:\n%s\n\nActual matches:\n%s",
				format(expectedMatches_), format(actualMatches));

		// Verify we have all the expected matches.
		for (final Match<S> expectedMatch : expectedMatches_)
		{
			Assert.assertTrue(
					String.format("expected match missing: %s\ninput text '%s'\n\n%s",
							expectedMatch,
							getRawText(textSource_, expectedMatch.getStart(), expectedMatch.getEnd()),
							matchDetails),
					actualMatches.contains(expectedMatch));

		}

		// Verify we don't have any unexpected matches
		for (final IMatch<S> match : actualMatches)
		{
			Assert.assertTrue(
					String.format("unexpected match: %s\ninput text: '%s'\n\n%s",
							match,
							getRawText(textSource_, match.getStart(), match.getEnd()),
							matchDetails),
					expectedMatches_.contains(match));
		}

		// Verify actual matches have legal precisions values
		for (final IMatch<S> match : actualMatches)
		{
			Verify.inClosedRange(match.getPrecision(),
					IMatchPrecisionFunction.MIN_PRECISION, IMatchPrecisionFunction.MAX_PRECISION, "precision");
		}

		// Verify match() reported the correct number of matches.
		Assert.assertEquals(expectedMatches_.size(), matcher_.getNumMatches());

		// Verify the matches are in the expected order and have the expected raw symbols. (We have to
		// verify the raw symbols separately because raw symbols are not included in IMatch.equals()
		// or IMatch.hasCode() evaluation.
		final Iterator<Match<S>> expectedIterator = expectedMatches_.iterator();
		final Iterator<IMatch<S>> matchIterator = actualMatches.iterator();

		while (expectedIterator.hasNext())
		{
			final Match<S> expected = expectedIterator.next();
			final IMatch<S> actual = matchIterator.next();

			Assert.assertEquals("unexpected match ordering; ",
					expected, actual);

			Assert.assertTrue(String.format("unexpected raw bytes;\nexpect: %s\nactual: %s ", expected, actual),
					Arrays.equals(expected.getRawSymbols(), actual.getRawSymbols()));
		}

		Assert.assertFalse(matchIterator.hasNext());

		textSource_.close();
	}

	/**
	 * Formats the raw input symbols as a string for use in diagnostic messages.
	 *
	 * @param textSource_
	 *            the input text source
	 * @param start_
	 *            the starting position of desired symbols (inclusive)
	 * @param end_
	 *            the ending position of desired symbols (exclusive)
	 * @return Returns a string for use in diagnostic messages containing the specified raw symbol range.
	 */
	protected abstract String getRawText(ITextSource<S> textSource_, long start_, long end_);

	protected String format(final List<? extends IMatch<S>> list_)
	{
		final StringBuilder buf = new StringBuilder();

		if (list_ != null)
		{
			int i = 0;
			for (final IMatch<S> match : list_)
			{
				buf.append(String.format("%d) %s\n", ++i, match));
			}
		}

		return buf.toString();
	}
}
