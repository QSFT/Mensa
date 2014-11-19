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

import java.util.ArrayList;
import java.util.List;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchListener;
import com.dell.mensa.util.Verify;

/**
 * {@link MatchCollector} is a {@link IMatchListener} implementation that collects match notifications. Specifically,
 * {@link #notifyMatch(IMatch)} adds adds the match to a list of matches. The list may be accessed via the
 * {@link #getMatches()} method.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class MatchCollector<S extends Comparable<S>> implements IMatchListener<S>
{
	private static final String PARM_offset = "offset_";
	private static final String PARM_limit = "limit_";

	/**
	 * The default offset; see {@link #setOffset(int)}.
	 */
	public static final int DEFAULT_OFFSET = 0;

	/**
	 * The default limit; see {@link #setLimit(int)}
	 */
	public static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

	// =========================================================================
	// Properties
	// =========================================================================
	private final List<IMatch<S>> matches;
	private boolean autoReset;
	private int offset;
	private int limit;
	private int count;

	/**
	 * Tests the autoReset property.
	 *
	 * @return Return the value of the autoReset property.
	 */
	public boolean isAutoReset()
	{
		return this.autoReset;
	}

	/**
	 * Sets the autoReset property. When this property is set to {@code true}, this collector will automatically clear
	 * the list of matches in {@link #notifyBeginMatching(AhoCorasickMachine)}.
	 *
	 * @param autoReset_
	 *            the autoReset property value to set.
	 */
	public void setAutoReset(final boolean autoReset_)
	{
		this.autoReset = autoReset_;
	}

	/**
	 * Returns the list of matches maintained by this instance. The returned list is modifiable, so the application can
	 * manipulate the list in any way it wished. However, this class is not thread-safe; the application must
	 * synchronize access to the this class and the maintained list when using multiple threads.
	 *
	 * @return the list of collected matches.
	 */
	public List<IMatch<S>> getMatches()
	{
		return this.matches;
	}

	/**
	 * Returns the current offset. See {@link #setOffset(int)} for further details.
	 *
	 * @return Returns the current offset.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Sets number of matches to ignore before beginning to collect matches.
	 *
	 * @param offset_
	 *            the offset to set
	 *
	 * @see #DEFAULT_OFFSET
	 *
	 * @throws IllegalArgumentException
	 *             if the offset is negative.
	 */
	public void setOffset(final int offset_)
	{
		Verify.notNegative(offset_, PARM_offset);
		this.offset = offset_;
	}

	/**
	 * Returns the current limit. See {@link #setLimit(int)} for further details.
	 *
	 * @return Returns the current limit.
	 */
	public int getLimit()
	{
		return limit;
	}

	/**
	 * Set the limit on the number of matches collected. Once the collector has collected this many matches,
	 * {@link #notifyMatch(IMatch)} will return {@code false}, terminating the match processing.
	 *
	 * @param limit_
	 *            the limit to set
	 *
	 * @see #DEFAULT_LIMIT
	 *
	 * @throws IllegalArgumentException
	 *             if the limit is negative.
	 */
	public void setLimit(final int limit_)
	{
		Verify.notNegative(limit_, PARM_limit);
		this.limit = limit_;
	}

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs an empty collector instance with the autoReset property to set to {@code false}.
	 *
	 * @see #setAutoReset(boolean)
	 */
	public MatchCollector()
	{
		this(false);
	}

	/**
	 * Constructs an empty collector instance with the specified autoReset property setting.
	 *
	 * @param autoReset_
	 *            specifies the autoReset property setting.
	 *
	 * @see #setAutoReset(boolean)
	 */
	public MatchCollector(final boolean autoReset_)
	{
		this.matches = new ArrayList<>();
		this.autoReset = autoReset_;
		this.offset = DEFAULT_OFFSET;
		this.limit = DEFAULT_LIMIT;
	}

	// =========================================================================
	// IMatchListener methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IMatchListener#notifyBeginMatching(com.dell.mensa.impl.generic.AhoCorasickMachine )
	 */
	@Override
	public boolean notifyBeginMatching(final AhoCorasickMachine<S> machine_)
	{
		if (isAutoReset())
		{
			matches.clear();
			count = 0;
		}
		return matches.size() < limit;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IMatchListener#notifyEndMatching(com.dell.mensa.impl.generic.AhoCorasickMachine)
	 */
	@Override
	public void notifyEndMatching(final AhoCorasickMachine<S> machine_)
	{
		// no-op
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IMatchListener#notifyMatch(com.dell.mensa.impl.generic.AhoCorasickMachine,
	 * com.dell.mensa.IKeyword, int, int)
	 */
	@Override
	public boolean notifyMatch(final IMatch<S> match_)
	{
		++count;

		if (count > offset && matches.size() < limit)
		{
			matches.add(match_);
		}

		return matches.size() < limit;
	}

	// =========================================================================
	// toString()
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();

		buf.append(String.format("MatchCollector [isAutoReset=%b, size=%d]\n", isAutoReset(), matches.size()));
		AhoCorasickMachine<S> machine = null;
		for (final IMatch<S> match : matches)
		{
			if (machine == null && match.getMachine() != null || machine != null && !machine.equals(match.getMachine()))
			{
				machine = match.getMachine();
				buf.append(String.format("  machine: %s\n", machine));
			}

			buf.append(String.format("  %5d, %5d, %s\n", match.getStart(), match.getEnd(), match.getKeyword()));
		}

		return buf.toString();
	}
}
