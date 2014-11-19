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
 * {@link ReadOnlyTailBuffer} is a decorator used create a read-only view of an existing {@link ITailBuffer} instance.
 * This class delegates all required (non-mutating) methods to the existing buffer and throw
 * {@link UnsupportedOperationException} for all optional (mutating) methods.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class ReadOnlyTailBuffer<S> implements ITailBuffer<S>
{
	private static final String PARM_buffer = "buffer_";

	// =========================================================================
	// Properties
	// =========================================================================
	/**
	 * The wrapped buffer.
	 */
	private final ITailBuffer<S> buffer;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs an instance that wraps an existing {@link ITailBuffer}.
	 *
	 * @param buffer_
	 *            specifies the existing {@link ITailBuffer} to wrap.
	 *
	 * @throws IllegalArgumentException
	 *             if the existing buffer it {@code null}
	 */
	public ReadOnlyTailBuffer(final ITailBuffer<S> buffer_)
	{
		super();
		Verify.notNull(buffer_, PARM_buffer);
		this.buffer = buffer_;
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
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#capacity()
	 */
	@Override
	public int capacity()
	{
		return buffer.capacity();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#clear()
	 */
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#end()
	 */
	@Override
	public long end()
	{
		return buffer.end();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return buffer.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#isFull()
	 */
	@Override
	public boolean isFull()
	{
		return buffer.isFull();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#remove()
	 */
	@Override
	public S remove()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#size()
	 */
	@Override
	public int size()
	{
		return buffer.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#start()
	 */
	@Override
	public long start()
	{
		return buffer.start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITailBuffer#symbolAt(int)
	 */
	@Override
	public S symbolAt(final long iPosition_)
	{
		return buffer.symbolAt(iPosition_);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("ReadOnly: %s", buffer);
	}
}
