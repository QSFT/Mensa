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
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.util.Verify;

/**
 * {@link AbstractTextSource} is an abstract base class that simplifies the implementation of a new concrete
 * {@link ITextSource}. To implement a new concrete text source, a derived class must implement three abstract methods:
 * {@link #openImpl()}, {@link #closeImpl()}, and {@link #readImpl(ITailBuffer)}. The bulk of the state management is
 * performed by this class, keeping the requirements for these abstract methods to a minimum.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public abstract class AbstractTextSource<S> implements ITextSource<S>
{
	private static final String PARM_iPosition = "iPosition_";

	private static final String MSG_ALREADY_OPEN = "text source is already open";
	private static final String MSG_AT_EOF = "read beyond end-of-file";
	private static final String MSG_NOT_OPEN = "text source is not open";

	/**
	 * Maximum size of tail buffers used to hold matched symbols. This effectively limits the maximum size of a keyword
	 * that can be matched.
	 */
	private static final int BUFFER_CAPACITY = TailBuffer.DEFAULT_CAPACITY;

	/**
	 * Maximum number of raw symbols that can be reduced to a single effective symbol.
	 */
	private static final int MAX_NEXT_SYMBOL_SIZE = 300;
	// =========================================================================
	// Properties
	// =========================================================================
	/**
	 * A boolean property indicating if the text source is open.
	 *
	 * @see #isOpen()
	 */
	private boolean bOpen;

	/**
	 * The position used when re-reading previously read symbols. Normally, this property is set to -1, indicating that
	 * symbols are not being re-read. In this case, the actual input position corresponds to the "end" of the tail
	 * buffers. However, calling {@link #setPosition(long)} sets this property to a previously read position (that still
	 * available in the tail buffers).
	 */
	private long position;

	/**
	 * The next symbol that will be returned by {@link #peek()} or {@link #read()}, or {@code null} if end-of-file has
	 * been reached. This property has no meaning when the text stream is not open.
	 *
	 * @see #peek()
	 * @see #read()
	 * @see #isEof()
	 */
	private S nextSymbol;

	/**
	 * A {@link ITailBuffer} used to hold the raw symbols consumed by the all to {@link #readImpl(ITailBuffer)} that
	 * produced the {@link #nextSymbol} value.
	 */
	private ITailBuffer<S> nextSymbolBuffer;

	/**
	 * A {@link ITailBuffer} view of the effective symbol stream. Defined only when this {@link ITextSource} is open.
	 */
	private ITailBuffer<S> buffer;

	/**
	 * A {@link ITailBuffer} view of the raw symbol input stream. Defined only when this {@link ITextSource} is open.
	 */
	private ITailBuffer<S> rawBuffer;

	/**
	 * Read-only view of {@link #rawBuffer}.
	 */
	private ITailBuffer<S> readOnlyRawBuffer;

	/**
	 * Read-only view of {@link #buffer}
	 */
	private ITailBuffer<S> readOnlyBuffer;

	// =========================================================================
	// Abstract methods
	// =========================================================================
	/**
	 * This method must be implemented by a derived class to close the text source. This method should release all
	 * resources used by the open text source.
	 *
	 * @throws IOException
	 *             if anything goes wrong closing the text source
	 */
	protected abstract void closeImpl() throws IOException;

	/**
	 * This method must be implemented by a derived class to open a text source.
	 *
	 * @throws IOException
	 *             if anything goes wrong opening the text source
	 */
	protected abstract void openImpl() throws IOException;

	/**
	 * This method must be implemented by a derived class to perform the raw symbol reading operation. Note that this
	 * method always returns exactly one <i>effective</i> symbol, but it may read any number of <i>raw</i> symbols from
	 * the input stream.
	 *
	 * @param buffer_
	 *            a {@link ITailBuffer} that will receive the raw symbols consumed by this call.
	 *
	 * @return Returns the next effective symbol from the text source or {@code null} if end-of-file has been reached.
	 *
	 * @throws IOException
	 *             if anything goes wrong reading the next symbol
	 */
	protected abstract S readImpl(final ITailBuffer<S> buffer_) throws IOException;

	// =========================================================================
	// ITextSource methods
	// =========================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#close()
	 */
	@Override
	public void close() throws IOException
	{
		if (isOpen())
		{
			closeImpl();
			bOpen = false;
			buffer = null;
			rawBuffer = null;
			readOnlyBuffer = null;
			readOnlyRawBuffer = null;
			nextSymbolBuffer = null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#getPosition()
	 */
	@Override
	public long getPosition()
	{
		verifyOpen();

		if (position != -1)
		{
			return position;
		}

		// We could return the end position from either of our buffers, since they are both tracking
		// progress through the same underlying symbol stream.
		assert rawBuffer.end() == buffer.end();

		return buffer.end();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#getRawTailBuffer()
	 */
	@Override
	public ITailBuffer<S> getRawTailBuffer()
	{
		verifyOpen();
		return readOnlyRawBuffer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#getTailBuffer()
	 */
	@Override
	public ITailBuffer<S> getTailBuffer()
	{
		verifyOpen();
		return readOnlyBuffer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#isEof()
	 */
	@Override
	public boolean isEof()
	{
		verifyOpen();
		return position == -1 && nextSymbol == null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#isOpen()
	 */
	@Override
	public boolean isOpen()
	{
		return bOpen;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#open()
	 */
	@Override
	public void open() throws IOException
	{
		if (isOpen())
		{
			throw new IllegalStateException(MSG_ALREADY_OPEN);
		}

		openImpl();

		bOpen = true;

		position = -1;

		buffer = new TailBuffer<>(BUFFER_CAPACITY);
		rawBuffer = new TailBuffer<>(BUFFER_CAPACITY);

		nextSymbolBuffer = new TailBuffer<>(MAX_NEXT_SYMBOL_SIZE);

		readOnlyBuffer = new ReadOnlyTailBuffer<>(buffer);
		readOnlyRawBuffer = new ReadOnlyTailBuffer<>(rawBuffer);

		// Prime the pump!
		loadNextSymbol();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#peek()
	 */
	@Override
	public S peek()
	{
		verifyOpen();

		if (position != -1)
		{
			return buffer.symbolAt(position);
		}

		return nextSymbol;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#read()
	 */
	@Override
	public S read() throws IOException
	{
		verifyOpen();

		if (position != -1)
		{
			// Re-read a symbol from the buffer.
			final S symbol = buffer.symbolAt(position++);
			assert symbol != null;

			// Consume any "extra" symbols associated with symbol we're about to return.
			while (position < buffer.end() && buffer.symbolAt(position) == null)
			{
				++position;
			}

			// If we've read to the end of the buffer, go back to normal positioning.
			if (position == buffer.end())
			{
				position = -1;
			}

			// Return the re-read symbol.
			return symbol;
		}

		if (isEof())
		{
			throw new IOException(MSG_AT_EOF);
		}

		final S symbol = nextSymbol;
		assert symbol != null;

		// Add next symbol to the effective symbol buffer.
		buffer.add(symbol);

		// Move raw symbol(s) from nextSymbolBuffer to rawBuffer.
		while (!nextSymbolBuffer.isEmpty())
		{
			rawBuffer.add(nextSymbolBuffer.remove());
		}

		// Add additional null symbols to the effective symbol buffer for each additional
		// raw symbol that was consumed, if any.
		while (buffer.end() < rawBuffer.end())
		{
			buffer.add(null);
		}

		loadNextSymbol();
		return symbol;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.ITextSource#setPosition(long)
	 */
	@Override
	public void setPosition(final long iPosition_)
	{
		verifyOpen();

		if (iPosition_ == buffer.end())
		{
			position = -1;
		}
		else
		{
			Verify.inRange(iPosition_, buffer.start(), buffer.end(), PARM_iPosition);

			if (buffer.symbolAt(iPosition_) == null)
			{
				throw new IllegalArgumentException();
			}
			position = iPosition_;
		}
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	/**
	 * Load the next symbol into {@link #nextSymbol} and {@link #nextSymbolBuffer}.
	 *
	 * @throws IOException
	 *             if {@link #readImpl(ITailBuffer)} throws an exception.
	 */
	private void loadNextSymbol() throws IOException
	{
		nextSymbol = readImpl(nextSymbolBuffer);
	}

	/**
	 * Verifies that the text source is open.
	 *
	 * @throws IllegalStateException
	 *             if the text source is not open.
	 */
	private void verifyOpen()
	{
		if (!bOpen)
		{
			throw new IllegalStateException(MSG_NOT_OPEN);
		}
	}
}
