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
import com.dell.mensa.util.Verify;

/**
 * {@link Keyword} extends {@link AbstractKeyword} to create a concrete, generic {#link IKeyword} implementation.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class Keyword<S> extends AbstractKeyword<S>
{
	private static final String PARM_symbols = "symbols_";

	// =========================================================================
	// Properties
	// =========================================================================
	protected final S[] symbols;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a keyword instance based on the specified symbols, user data, and bit flags.
	 *
	 * @param symbols_
	 *            the symbols that comprise this keyword.
	 * @param userData_
	 *            specifies user an arbitrary user data object to associate with this keyword; may be {@code null}.
	 * @param flags_
	 *            specifies bit flags that control various keyword features. To specify multiple bit flags, OR together
	 *            individual bit values, e.g., {@link #CASE_SENSITIVE}|{@link #PUNCTUATION_SENSITIVE}.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified symbols are null or empty, or if the specified flags contain unknown values
	 */
	public Keyword(final S[] symbols_, final Object userData_, final int flags_)
	{
		super(userData_, flags_);

		Verify.notEmpty(symbols_, PARM_symbols);
		this.symbols = symbols_.clone();
	}

	/**
	 * Constructs a keyword instance based on the specified symbols and user data and no flags.
	 *
	 * @param symbols_
	 *            the symbols that comprise this keyword.
	 * @param userData_
	 *            specifies user an arbitrary user data object to associate with this keyword; may be {@code null}.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified symbols are null or empty
	 */
	public Keyword(final S[] symbols_, final Object userData_)
	{
		this(symbols_, userData_, 0);
	}

	/**
	 * Constructs a keyword instance based on the specified symbols and no user data or flags.
	 *
	 * @param symbols_
	 *            the symbols that comprise this keyword.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified symbols are null or empty
	 */
	public Keyword(final S[] symbols_)
	{
		this(symbols_, null, 0);
	}

	// =========================================================================
	// AbstractKeyword methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.Keyword#asString()
	 */
	@Override
	protected String asString()
	{
		return Arrays.toString(symbols);
	}

	// =========================================================================
	// IKeyword methods
	// =========================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IKeyword#length()
	 */
	@Override
	public int length()
	{
		return symbols.length;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IKeyword#symbolAt(int)
	 */
	@Override
	public S symbolAt(final int index_)
	{
		return symbols[index_];
	}
}
