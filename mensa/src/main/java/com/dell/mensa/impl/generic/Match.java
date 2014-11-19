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

import java.util.Arrays;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.util.Verify;

/**
 * {@link Match} is a simple bean that implements {@link IMatch}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class Match<S extends Comparable<S>> implements IMatch<S>
{
	private static final String PARM_precision = "precision_";

	// =========================================================================
	// Properties
	// =========================================================================
	private AhoCorasickMachine<S> machine;
	private IKeyword<S> keyword;
	private long start;
	private long end;
	private double precision;
	private S[] rawSymbols;

	/**
	 * @return Returns the end position.
	 */
	@Override
	public long getEnd()
	{
		return end;
	}

	/**
	 * @return Returns the matched keyword.
	 */
	@Override
	public IKeyword<S> getKeyword()
	{
		return keyword;
	}

	/**
	 * @return Returns the machine that performed the match.
	 */
	@Override
	public AhoCorasickMachine<S> getMachine()
	{
		return machine;
	}

	/**
	 * @return Returns the match precision.
	 */
	@Override
	public double getPrecision()
	{
		return precision;
	}

	/**
	 * @return Returns the raw symbols from the input text used for this match, or {@code null} if the raw symbols are
	 *         not available.
	 */
	@Override
	public S[] getRawSymbols()
	{
		if (rawSymbols == null)
		{
			return null;
		}

		return rawSymbols.clone();
	}

	/**
	 * @return Returns the start position.
	 */
	@Override
	public long getStart()
	{
		return start;
	}

	/**
	 * @param end_
	 *            the end position to set
	 */
	public void setEnd(final int end_)
	{
		this.end = end_;
	}

	/**
	 * @param keyword_
	 *            the keyword to set
	 */
	public void setKeyword(final IKeyword<S> keyword_)
	{
		this.keyword = keyword_;
	}

	/**
	 * @param machine_
	 *            the machine to set
	 */
	public void setMachine(final AhoCorasickMachine<S> machine_)
	{
		this.machine = machine_;
	}

	/**
	 * @param precision_
	 *            the precision to set
	 */
	public void setPrecision(final double precision_)
	{
		Verify.inClosedRange(precision_,
				IMatchPrecisionFunction.MIN_PRECISION, IMatchPrecisionFunction.MAX_PRECISION, PARM_precision);
		this.precision = precision_;
	}

	/**
	 * @param rawSymbols_
	 *            the rawSymbols to set
	 */
	public void setRawSymbols(final S[] rawSymbols_)
	{
		this.rawSymbols = rawSymbols_ == null ? null : rawSymbols_.clone();
	}

	/**
	 * @param start_
	 *            the start position to set
	 */
	public void setStart(final int start_)
	{
		this.start = start_;
	}

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance with the specified properties. By default, the precision is set
	 * {@link IMatchPrecisionFunction#MAX_PRECISION}, but may be changed using {@link #setPrecision(double)}.
	 *
	 * @param machine_
	 *            the machine
	 * @param keyword_
	 *            the keyword
	 * @param start_
	 *            the start position
	 * @param end_
	 *            the end position
	 */
	public Match(final AhoCorasickMachine<S> machine_, final IKeyword<S> keyword_, final long start_, final long end_)
	{
		super();
		this.machine = machine_;
		this.keyword = keyword_;
		this.start = start_;
		this.end = end_;
		this.precision = IMatchPrecisionFunction.MAX_PRECISION;
	}

	/**
	 * Constructs a new instance with {@code null} machine and keyword properties and {@code -1} start and end
	 * properties. By default, the precision is set {@link IMatchPrecisionFunction#MAX_PRECISION}, but may be changed
	 * using {@link #setPrecision(double)}.
	 */
	public Match()
	{
		this(null, null, -1, -1);
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
		return String.format("Match [%s, start=%s, end=%s, extra=%d, precision=%f, rawSymbols=%s, machine=%s]",
				keyword, start, end, end - start - keyword.length(), precision, Arrays.toString(rawSymbols), machine);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + end);
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + ((machine == null) ? 0 : machine.hashCode());
		result = (int) (prime * result + start);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Match))
		{
			return false;
		}
		final Match<?> other = (Match<?>) obj;
		if (end != other.end)
		{
			return false;
		}
		if (keyword == null)
		{
			if (other.keyword != null)
			{
				return false;
			}
		}
		else if (!keyword.equals(other.keyword))
		{
			return false;
		}
		if (machine == null)
		{
			if (other.machine != null)
			{
				return false;
			}
		}
		else if (!machine.equals(other.machine))
		{
			return false;
		}
		if (start != other.start)
		{
			return false;
		}
		return true;
	}
}
