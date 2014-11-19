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

import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ISymbolClassifier;

/**
 * {@link DefaultSymbolClassifier} is a default {@link ISymbolClassifier} implementation that performs the simplest
 * possible classification.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class DefaultSymbolClassifier<S> implements ISymbolClassifier<S>
{
	private static final String MSG_IS_PUNCTUATION_UNAVALABLE = "isPuntuation(S a_) called with the punctuation extension disabled";
	private static final String MSG_IS_WORDBREAK_UNAVALABLE = "isWordBreak(S a_) called with the word-break extension disabled";
	private static final String MSG_TO_LOWERCASE_UNAVALABLE = "toLowerCase(S a_) called with the case-sensitivity extension disabled";

	// =========================================================================
	// Properties
	// =========================================================================
	/**
	 * The {@link IMatchPrecisionFunction} instance returned by {@link #getMatchPrecisionFunction()}.
	 */
	protected IMatchPrecisionFunction<S> matchPrecisionFunction;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * @param matchPrecisionFunction_
	 *            {@link IMatchPrecisionFunction} instance to be returned by {@link #getMatchPrecisionFunction()}. A
	 *            derived class may specify {@code null} and instead set the {@link #matchPrecisionFunction} value
	 *            explicitly.
	 */
	protected DefaultSymbolClassifier(final IMatchPrecisionFunction<S> matchPrecisionFunction_)
	{
		super();
		this.matchPrecisionFunction = matchPrecisionFunction_;
	}

	public DefaultSymbolClassifier()
	{
		this(new DefaultMatchPrecisionFunction<S>());
	}

	// =========================================================================
	// ISymbolClassifier methods
	// =========================================================================

	@Override
	public IMatchPrecisionFunction<S> getMatchPrecisionFunction()
	{
		return matchPrecisionFunction;
	}

	/**
	 * Indicates that special white space processing is disabled by returning {@code null}.
	 *
	 * @return Returns {@code null} always.
	 */
	@Override
	public S getSpace()
	{
		return null;
	}

	/**
	 * Indicates that the case-sensitivity extension is disabled.
	 *
	 * @return Returns {@code false} always.
	 */
	@Override
	public boolean isCaseExtensionEnabled()
	{
		return false;
	}

	/**
	 * Disabled for this classifier.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify end-of-file.
	 *
	 * @throws IllegalStateException
	 *             always, because the punctuation extension is disabled.
	 */
	@Override
	public boolean isPunctuation(final S a_)
	{
		throw new IllegalStateException(MSG_IS_PUNCTUATION_UNAVALABLE);
	}

	/**
	 * Indicates that the punctuation-sensitivity extension is disabled.
	 *
	 * @return Returns {@code false} always.
	 */
	@Override
	public boolean isPunctuationExtensionEnabled()
	{
		return false;
	}

	/**
	 * Determines if a symbol is a white space symbol.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Returns {@code false} always.
	 */
	@Override
	public boolean isWhitespace(final S a_)
	{
		return false;
	}

	/**
	 * Disabled for this classifier.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify end-of-file.
	 *
	 * @throws IllegalStateException
	 *             always, because the word-break extension is disabled.
	 */
	@Override
	public boolean isWordBreak(final S a_)
	{
		throw new IllegalStateException(MSG_IS_WORDBREAK_UNAVALABLE);
	}

	/**
	 * Indicates that the word-break extension is disabled.
	 *
	 * @return Returns {@code false} always.
	 */
	@Override
	public boolean isWordBreakExtensionEnabled()
	{
		return false;
	}

	/**
	 * Unsupported operation.
	 *
	 * @throws IllegalStateException
	 *             always, because case-sensitivity extensions are disabled.
	 */
	@Override
	public S toLowerCase(final S a_)
	{
		throw new IllegalStateException(MSG_TO_LOWERCASE_UNAVALABLE);
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
		return String.format("%s [extensions: %s%s%s]",
				getClass().getSimpleName(),
				isCaseExtensionEnabled() ? " case" : "",
				isPunctuationExtensionEnabled() ? " punctuation" : "",
				isWordBreakExtensionEnabled() ? " wordBreak" : "");
	}
}
