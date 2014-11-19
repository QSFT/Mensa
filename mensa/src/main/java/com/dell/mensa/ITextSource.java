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
package com.dell.mensa;

import java.io.IOException;
import com.dell.mensa.impl.generic.AhoCorasickMachine;

/**
 * {@link ITextSource} is the interface used by an {@link AhoCorasickMachine} to read the input text.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface ITextSource<S>
{
	/**
	 * Closes the text source, if it is open. Otherwise does nothing. An application must ensure that any open text
	 * source is eventually closed.
	 *
	 * @throws IOException
	 *             if an error occurs while closing the text source.
	 */
	void close() throws IOException;

	/**
	 * Returns the current input position. The position is the zero-based index of the next symbol to be returned by
	 * {@link #peek()} or {@link #read()}. Note that this index may be beyond the last symbol if end-of-file has been
	 * reached.
	 *
	 * @return Returns the next input position.
	 *
	 * @throws IllegalStateException
	 *             if the text source is not open.
	 */
	long getPosition();

	/**
	 * Returns a read-only {@link ITailBuffer} view of the raw symbol input stream. The raw symbol stream is the exact
	 * sequence of symbols present in the underlying source. This sequence may differ from the symbols actually returned
	 * by the {@link #read()} method. See {@link #getTailBuffer()} for further discussion.
	 *
	 * @return Returns a read-only {@link ITailBuffer} view of the raw symbol input stream.
	 *
	 * @throws IllegalStateException
	 *             if this text source is not open.
	 */
	ITailBuffer<S> getRawTailBuffer();

	/**
	 * Returns a read-only {@link ITailBuffer} view of the effective symbol input stream. The effective symbol stream is
	 * the sequence of symbols seen actually returned by {@link #read()} method. This sequence may differ from the raw
	 * symbol stream if this {@link ITextSource} implementation performs any type of transformation of the raw symbols.
	 * For example, a character text source may perform white space normalization to ensure more reliable matching of
	 * multi-word keywords.
	 *
	 * <p>
	 * While the buffers returned by this method and the {@link #getRawTailBuffer()} method may show different symbols,
	 * the symbol positions reported are the same across both buffers and refer to the zero-based index into the raw
	 * input stream. In the case where this {@link ITextSource} transforms raw input symbols such that multiple raw
	 * symbols are transformed into a single effective symbol (e.g., several space character reduced to a single space),
	 * "extra" symbols in the raw buffer are represented as {@code null} symbols in the effective buffer.
	 * </p>
	 *
	 * @return Returns a read-only {@link ITailBuffer} view of the effective symbol input stream.
	 *
	 * @throws IllegalStateException
	 *             if this text source is not open.
	 */
	ITailBuffer<S> getTailBuffer();

	/**
	 * Tests whether end-of-file has been reached.
	 *
	 * @return Returns {@code true} if end-of-file has been reached; {@code false} otherwise.
	 *
	 * @throws IllegalStateException
	 *             if the text source is not open.
	 */
	boolean isEof();

	/**
	 * Tests whether or not the text source is open.
	 *
	 * @return Returns {@code true} if the text source is open, {@code false} otherwise.
	 */
	boolean isOpen();

	/**
	 * Opens the text source for reading. An application is must ensure that all open text sources are eventually
	 * closed.
	 *
	 * @throws IOException
	 *             if any error occurs opening the text source
	 *
	 * @throws IllegalStateException
	 *             if the text source is already open.
	 */
	void open() throws IOException;

	/**
	 * Reads, but does not consume, the next input symbol.
	 *
	 * @return Returns the next input symbol or {@code null} if end-of-file has been reached.
	 *
	 * @throws IllegalStateException
	 *             if the text source is not open.
	 */
	S peek();

	/**
	 * Reads and consumes the next input symbol, advancing the input position.
	 *
	 * @return Returns the next input symbol.
	 *
	 * @throws IOException
	 *             if any error occurs reading the input, including an attempt to read beyond end-of-file.
	 * @throws IllegalStateException
	 *             if the text source is not open.
	 */
	S read() throws IOException;

	/**
	 * Sets the current input position to an earlier position. This provides the ability to "unread" symbols previously
	 * returned by {@link #read()}, provided the position is still within the associated {@link ITailBuffer}.
	 *
	 * @param iPosition_
	 *            the current input position to set
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the position is not within the tail buffer.
	 * @throws IllegalArgumentException
	 *             if the position specifies the location of an "extra" symbol.
	 */
	void setPosition(final long iPosition_);
}
