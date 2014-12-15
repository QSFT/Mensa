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

import com.dell.mensa.IKeyword;
import com.dell.mensa.util.Verify;

/**
 * {@link AbstractKeyword} simplifies creation of concrete {#link IKeyword} implementations.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public abstract class AbstractKeyword<S> implements IKeyword<S>
{
	/**
	 * A bit flag indicating the keyword is case-sensitive.
	 */
	public static final int CASE_SENSITIVE = 0x01;

	/**
	 * A bit flag indicating the keyword is punctuation-sensitive.
	 */
	public static final int PUNCTUATION_SENSITIVE = 0x02;

	private static final int ALL_FLAGS = CASE_SENSITIVE | PUNCTUATION_SENSITIVE;

	private static final String MSG_UNKNOWN_FLAGS = "unknown flags";

	// =========================================================================
	// Properties
	// =========================================================================
	private final Object userData;
	private final byte flags;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * @param userData_
	 *            specifies user an arbitrary user data object to associate with this keyword; may be {@code null}.
	 * @param flags_
	 *            specifies bit flags that control various keyword features. To specify multiple bit flags, OR together
	 *            individual bit values, e.g., {@link #CASE_SENSITIVE}|{@link #PUNCTUATION_SENSITIVE}.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified flags contain unknown values
	 */
	protected AbstractKeyword(final Object userData_, final int flags_)
	{
		Verify.condition((flags_ & ~ALL_FLAGS) == 0, MSG_UNKNOWN_FLAGS);

		this.userData = userData_;
		this.flags = (byte) flags_;
	}

	// =========================================================================
	// Abstract methods
	// =========================================================================
	/**
	 * Returns a human-friendly description of this keyword for diagnostic purposes.
	 *
	 * @return Returns a human-friendly description of this keyword for diagnostic purposes.
	 */
	protected abstract String asString();

	// =========================================================================
	// IKeyword methods
	// =========================================================================

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IKeyword#getUserData()
	 */
	@Override
	public Object getUserData()
	{
		return userData;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IKeyword#isCaseSensitive()
	 */
	@Override
	public boolean isCaseSensitive()
	{
		return (flags & CASE_SENSITIVE) != 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IKeyword#isPunctuationSensitive()
	 */
	@Override
	public boolean isPunctuationSensitive()
	{
		return (flags & PUNCTUATION_SENSITIVE) != 0;
	}

	// =========================================================================
	// hashCode() and equals()
	// =========================================================================

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
		result = prime * result + (isCaseSensitive() ? 1231 : 1237);
		result = prime * result + (isPunctuationSensitive() ? 1231 : 1237);

		final Object userData = getUserData();
		result = prime * result + (userData == null ? 0 : userData.hashCode());

		final int length = length();
		for (int i = 0; i < length; i++)
		{
			final Object symbol = symbolAt(i);
			result = prime * result + (symbol == null ? 0 : symbol.hashCode());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object object_)
	{
		if (this == object_)
		{
			return true;
		}

		if (object_ == null)
		{
			return false;
		}

		if (!(object_ instanceof IKeyword))
		{
			return false;
		}

		@SuppressWarnings("rawtypes")
		final IKeyword other = (IKeyword) object_;

		if (isCaseSensitive() != other.isCaseSensitive())
		{
			return false;
		}

		if (isPunctuationSensitive() != other.isPunctuationSensitive())
		{
			return false;
		}

		final Object userData = getUserData();
		final Object otherUserData = other.getUserData();

		if (userData == null)
		{
			if (otherUserData != null)
			{
				return false;
			}
		}
		else
		{
			if (!userData.equals(otherUserData))
			{
				return false;
			}
		}

		final int length = length();
		if (length != other.length())
		{
			return false;
		}

		for (int i = 0; i < length; i++)
		{
			final Object symbol = symbolAt(i);
			final Object otherSymbol = other.symbolAt(i);

			if (symbol == null ? otherSymbol != null : !symbol.equals(otherSymbol))
			{
				return false;
			}
		}

		return true;
	}

	// =========================================================================
	// toString
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%s [\"%s\"%s%s]",
				getClass().getSimpleName(),
				asString(),
				isCaseSensitive() ? ", case-sensitive" : "",
				isPunctuationSensitive() ? ", punctuation-sensitive" : ""
				);
	}
}
