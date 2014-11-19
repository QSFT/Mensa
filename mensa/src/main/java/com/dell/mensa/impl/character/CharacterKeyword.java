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
package com.dell.mensa.impl.character;

import com.dell.mensa.impl.generic.AbstractKeyword;
import com.dell.mensa.util.Verify;

/**
 * {@link CharacterKeyword} extends {@link AbstractKeyword} to create a concrete {@link Character}-valued {#link
 * IKeyword} implementation.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterKeyword extends AbstractKeyword<Character>
{
	private static final String PARM_keyword = "keyword_";

	// =========================================================================
	// Properties
	// =========================================================================
	private final char[] symbols;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a keyword instance based on the specified symbols, user data, and bit flags.
	 *
	 * @param keyword_
	 *            the string value of the keyword.
	 * @param userData_
	 *            specifies user an arbitrary user data object to associate with this keyword; may be {@code null}.
	 * @param flags_
	 *            specifies bit flags that control various keyword features. To specify multiple bit flags, OR together
	 *            individual bit values, e.g., {@link #CASE_SENSITIVE}|{@link #PUNCTUATION_SENSITIVE}.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified keyword string is null or empty, or if the specified flags contain unknown values
	 */
	public CharacterKeyword(final String keyword_, final Object userData_, final int flags_)
	{
		super(userData_, flags_);

		Verify.notEmpty(keyword_, PARM_keyword);
		this.symbols = keyword_.toCharArray();
	}

	/**
	 * Constructs a keyword instance based on the specified symbols and user data and no flags.
	 *
	 * @param keyword_
	 *            the string value of the keyword.
	 * @param userData_
	 *            specifies user an arbitrary user data object to associate with this keyword; may be {@code null}.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified keyword string is null or empty
	 */
	public CharacterKeyword(final String keyword_, final Object userData_)
	{
		this(keyword_, userData_, 0);
	}

	/**
	 * Constructs a keyword instance based on the specified symbols and no user data or flags.
	 *
	 * @param keyword_
	 *            the string value of the keyword.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified keyword string is null or empty
	 */
	public CharacterKeyword(final String keyword_)
	{
		this(keyword_, null, 0);
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
		return new String(symbols);
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
	public Character symbolAt(final int index_)
	{
		return symbols[index_];
	}
}
