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

import com.dell.mensa.ITailBuffer;
import com.dell.mensa.util.Verify;

/**
 * {@link TailBuffer} is a concrete implementation of {@link ITailBuffer}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class TailBuffer<S> implements ITailBuffer<S>
{
	private static final String PARM_capacity = "capacity_";
	private static final String PARM_iPosition_ = "iPosition_";

	/**
	 * The default buffer size used by {@link #TailBuffer()}.
	 */
	public static final int DEFAULT_CAPACITY = 1024;

	// =========================================================================
	// Properties
	// =========================================================================
	private final Object[] buffer;
	private long iEnd;
	private long iStart;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs an instance with the specified capacity.
	 *
	 * @param capacity_
	 *            specifies the maximum size of the buffer.
	 */
	public TailBuffer(final int capacity_)
	{
		Verify.isPositive(capacity_, PARM_capacity);
		this.buffer = new Object[capacity_];
		this.iStart = 0;
		this.iEnd = 0;
	}

	/**
	 * Constructs an instance using the {@link #DEFAULT_CAPACITY}.
	 */
	public TailBuffer()
	{
		this(DEFAULT_CAPACITY);
	}

	// =========================================================================
	// ITailBuffer methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#add(java.lang.Object)
	 */
	@Override
	public void add(final S symbol_)
	{
		if (isFull())
		{
			++iStart;
		}

		buffer[(int) (iEnd++ % buffer.length)] = symbol_;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#capacity()
	 */
	@Override
	public int capacity()
	{
		return buffer.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#clear()
	 */
	@Override
	public void clear()
	{
		iStart = 0;
		iEnd = 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#end()
	 */
	@Override
	public long end()
	{
		return iEnd;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return iStart == iEnd;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#isFull()
	 */
	@Override
	public boolean isFull()
	{
		return iEnd - iStart == buffer.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#remove()
	 */
	@Override
	public S remove()
	{
		if (isEmpty())
		{
			throw new IllegalStateException();
		}

		final S removed = symbolAt(iStart);
		++iStart;
		return removed;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#size()
	 */
	@Override
	public int size()
	{
		return (int) (iEnd - iStart);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#start()
	 */
	@Override
	public long start()
	{
		return iStart;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#symbolAt(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public S symbolAt(final long iPosition_)
	{
		Verify.inRange(iPosition_, iStart, iEnd, PARM_iPosition_);

		return (S) buffer[(int) (iPosition_ % buffer.length)];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final int maxLen = 30;
		final StringBuilder buf = new StringBuilder();

		for (long i = Math.max(iStart, iEnd - maxLen); i < iEnd; ++i)
		{
			if (buf.length() > 0)
			{
				buf.append(", ");
			}
			buf.append(symbolAt(i));
		}

		return String.format("TailBuffer [%d, %d) ... %s",
				iStart, iEnd, buf);
	}
}
