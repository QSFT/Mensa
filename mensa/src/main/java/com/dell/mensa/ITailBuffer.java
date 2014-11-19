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

/**
 * {@link ITailBuffer} specifies the interface to a data structure that maintains a copy of the tail end of an
 * arbitrarily long sequence of symbols, such as the symbols read from an {@link ITextSource}.
 *
 * <p>
 * The zero-based index of each symbol in sequence is known as the <i>position</i> of the symbol. {@link ITailBuffer}
 * provides methods for retrieving a symbol by its position, provided that its position falls in the range covered by
 * the buffer, which is defined by [{@link #start()}, {@link #end()}).
 * </p>
 *
 * <p>
 * The {@link #size()} of the buffer is the number of symbols in the buffer. The {@link #capacity()} is the maximum size
 * of the buffer. Adding a symbol when the buffer is full causes the earliest symbol to be dropped from the buffer.
 * </p>
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface ITailBuffer<S>
{
	/**
	 * Adds a symbol to the end of the buffer and advances the {@link #end()} position (optional operation). Adding a
	 * symbol when the buffer is full causes the earliest symbol to be dropped from the buffer.
	 *
	 * @param symbol_
	 *            specifies the symbol to add; may be {@code null}.
	 *
	 * @throws UnsupportedOperationException
	 *             if {@link #add(Object)} is not supported by this buffer
	 */
	void add(final S symbol_);

	/**
	 * Returns the capacity (i.e., the maximum size) of the buffer.
	 *
	 * @return Returns the capacity of the buffer.
	 */
	int capacity();

	/**
	 * Clears the content of the buffer (optional operation). Resets both the start and end positions to zero.
	 *
	 * @throws UnsupportedOperationException
	 *             if {@link #clear()} is not supported by this buffer
	 */
	void clear();

	/**
	 * Returns the position immediately after the last accessible symbol in the buffer. Note that there may not actually
	 * be a symbol available at the prior position if the buffer is empty.
	 *
	 * @return Returns the position immediately after the last accessible symbol in the buffer.
	 */
	long end();

	/**
	 * Tests if the buffer is empty.
	 *
	 * @return Returns {@code true} if the buffer is empty; {@code false} otherwise.
	 */
	boolean isEmpty();

	/**
	 * Tests if the buffer is full.
	 *
	 * @return Returns {@code true} if the buffer is empty; {@code false} otherwise.
	 */
	boolean isFull();

	/**
	 * Removes the symbol at the start of the buffer and advances the {@link #start()} position (optional operation).
	 *
	 * @return Returns the symbol that was at the start of the buffer.
	 *
	 * @throws IllegalStateException
	 *             if the buffer is empty.
	 * @throws UnsupportedOperationException
	 *             if {@link #remove()} is not supported by this buffer
	 */
	S remove();

	/**
	 * Returns the number of symbols available in the buffer.
	 *
	 * @return Returns the number of symbols available in the buffer.
	 */
	int size();

	/**
	 * Returns the position of the first accessible symbol in the buffer. Note that there may not actually be a symbol
	 * available at this position if the buffer is empty.
	 *
	 * @return Returns the position of the first accessible symbol in the buffer.
	 */
	long start();

	/**
	 * Returns the symbol at the specified position.
	 *
	 * @param iPosition_
	 *            the position of the symbol to retrieve.
	 * @return Returns the symbol at the specified position, which may be {@code null}.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the position is not within the range covered by the buffer, which is defined by [{@link #start()},
	 *             {@link #end()}).
	 */
	S symbolAt(final long iPosition_);
}
