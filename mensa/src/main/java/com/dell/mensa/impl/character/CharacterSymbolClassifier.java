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

import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.impl.generic.DefaultSymbolClassifier;

/**
 * {@link CharacterSymbolClassifier} implements {@link ISymbolClassifier} for {@link Character} symbols.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterSymbolClassifier extends DefaultSymbolClassifier<Character>
{
	/**
	 * Canonical space symbol returned by {@link #getSpace()}.
	 */
	public static final Character SPACE = Character.valueOf(' ');

	// =========================================================================
	// Properties
	// =========================================================================
	private final boolean bCaseExtensionEnabled;
	private final boolean bPunctuationExtensionEnabled;
	private final boolean bWordBreakExtensionEnabled;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance with case-sensitivity extensions enabled or disabled.
	 *
	 * @param bCaseExtensionEnabled_
	 *            specifies whether the case-sensitivity extension is enabled ({@code true}) or disabled {@code false}).
	 * @param bPunctuationExtensionEnabled_
	 *            specifies whether the case-sensitivity extension is enabled ({@code true}) or disabled {@code false}).
	 * @param bWordBreakExtensionEnabled_
	 *            specifies whether the word-break extension is enabled ({@code true}) or disabled {@code false}).
	 */
	public CharacterSymbolClassifier(
			final boolean bCaseExtensionEnabled_,
			final boolean bPunctuationExtensionEnabled_,
			final boolean bWordBreakExtensionEnabled_)
	{
		super(null);

		this.bCaseExtensionEnabled = bCaseExtensionEnabled_;
		this.bPunctuationExtensionEnabled = bPunctuationExtensionEnabled_;
		this.bWordBreakExtensionEnabled = bWordBreakExtensionEnabled_;

		// IMPORTANT: initialize our match function last so that this classifier is
		// fully constructed by the time it is passed to the match function constructor.
		this.matchPrecisionFunction = new CharacterMatchPrecisionFunction(this);
	}

	/**
	 * Constructs a new instance with all extensions enabled or disabled.
	 *
	 * @param bExtensionsEnabled_
	 *            specifies whether all extensions are enabled ({@code true}) or disabled {@code false}).
	 */
	public CharacterSymbolClassifier(final boolean bExtensionsEnabled_)
	{
		this(bExtensionsEnabled_, bExtensionsEnabled_, bExtensionsEnabled_);
	}

	/**
	 * Constructs a new instance with all extensions enabled.
	 */
	public CharacterSymbolClassifier()
	{
		this(true);
	}

	// =========================================================================
	// ISymbolClassifier methods
	// =========================================================================
	/**
	 * Indicates that special white space processing is disabled by returning {@code null}.
	 *
	 * @return Returns {@code null} always.
	 */
	@Override
	public Character getSpace()
	{
		return SPACE;
	}

	/**
	 * This method reports whether or not the case-sensitivity extension is enabled.
	 *
	 * @return Returns {@code true} if case-sensitivity extensions are enabled; {@code false} otherwise.
	 *
	 * @see #CharacterSymbolClassifier(boolean, boolean, boolean)
	 * @see #CharacterSymbolClassifier(boolean)
	 * @see #CharacterSymbolClassifier()
	 */
	@Override
	public boolean isCaseExtensionEnabled()
	{
		return bCaseExtensionEnabled;
	}

	/**
	 * Determines if a symbol is a punctuation symbol. This method delegates actual classification to
	 * {@link AbstractCharacterTextSource#isPunctuation(int)}.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a punctuation symbol; {@code false} otherwise.
	 *
	 * @throws IllegalStateException
	 *             if the punctuation extension is disabled.
	 */
	@Override
	public boolean isPunctuation(final Character a_)
	{
		return a_ != null && AbstractCharacterTextSource.isPunctuation(a_);
	}

	/**
	 * This method reports whether or not the case-sensitivity extension is enabled.
	 *
	 * @return Returns {@code true} if case-sensitivity extensions are enabled; {@code false} otherwise.
	 *
	 * @see #CharacterSymbolClassifier(boolean, boolean, boolean)
	 * @see #CharacterSymbolClassifier(boolean)
	 * @see #CharacterSymbolClassifier()
	 */
	@Override
	public boolean isPunctuationExtensionEnabled()
	{
		return bPunctuationExtensionEnabled;
	}

	/**
	 * Determines if a symbol is a white space symbol. This method delegates actual classification to
	 * {@link AbstractCharacterTextSource#isWhitespace(int)}.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a white space symbol; {@code false} otherwise.
	 */
	@Override
	public boolean isWhitespace(final Character a_)
	{
		return a_ != null && AbstractCharacterTextSource.isWhitespace(a_);
	}

	/**
	 * This method reports whether or not the word-break extension is enabled.
	 *
	 * @return Returns {@code true} if case-sensitivity extensions are enabled; {@code false} otherwise.
	 *
	 * @see #CharacterSymbolClassifier(boolean, boolean, boolean)
	 * @see #CharacterSymbolClassifier(boolean)
	 * @see #CharacterSymbolClassifier()
	 */
	@Override
	public boolean isWordBreakExtensionEnabled()
	{
		return bWordBreakExtensionEnabled;
	}

	/**
	 * Classifies non-letter-or-digit characters as word break symbols.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify end-of-file.
	 *
	 * @return Returns {@code true} if the character is: {@code null}; a surrogate (as defined by
	 *         {@link Character#isSurrogate(char)}); or a letter or a digit (as defined by
	 *         {@link Character#isLetterOrDigit(char)}).
	 *
	 * @throws IllegalArgumentException
	 *             if the specified symbol is {@code null}.
	 */
	@Override
	public boolean isWordBreak(final Character a_)
	{
		if (!bWordBreakExtensionEnabled)
		{
			return super.isWordBreak(a_); // throws IllegalStateException
		}

		return a_ == null || Character.isSurrogate(a_) || !Character.isLetterOrDigit(a_);
	}

	/**
	 * This method transforms a symbol to lower case ASCII. Diacritical characters are mapped to ASCII by
	 * {@link AbstractCharacterTextSource#mapDiacriticalToASCII(int)} before case conversion.
	 *
	 * @param a_
	 *            specifies the symbol to transform, which may be {@code null};
	 *
	 * @return Returns the lower case version of the specified symbol, or the original symbol if no lower case version
	 *         is applicable.
	 *
	 * @throws IllegalStateException
	 *             if called when case-sensitivity extensions are disabled.
	 */
	@Override
	public Character toLowerCase(final Character a_)
	{
		if (!bCaseExtensionEnabled)
		{
			return super.toLowerCase(a_); // throws IllegalStateException
		}

		if (a_ == null)
		{
			return null;
		}

		return Character.isSurrogate(a_)
				? a_
				: Character.toLowerCase((char) AbstractCharacterTextSource.mapDiacriticalToASCII(a_));
	}
}
