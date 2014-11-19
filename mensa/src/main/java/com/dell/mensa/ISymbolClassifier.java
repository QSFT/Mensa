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
 * {@link ISymbolClassifier} specifies the interface to various symbol classification and other methods whose
 * implementation details may vary based on symbol type.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface ISymbolClassifier<S>
{
	/**
	 * Returns a concrete {@link IMatchPrecisionFunction} implementation that may be used to evaluate match precision in
	 * conjunction with this classifier.
	 *
	 * @return Returns a thread-safe {@link IMatchPrecisionFunction} instance.
	 */
	IMatchPrecisionFunction<S> getMatchPrecisionFunction();

	/**
	 * Returns the canonical "space" symbol.
	 *
	 * @return Returns the canonical "space" symbol or {@code null} to effectively special white space <i>and</i>
	 *         punctuation processing.
	 */
	S getSpace();

	/**
	 * This method reports whether or not the case-sensitivity extension is enabled. When this method returns
	 * {@code false}, the machine operates (in a case-sensitive manner) without any assumptions about symbol case. When
	 * this method returns {@code true}, the machine operates in a case-insensitive manner, but may apply post-match
	 * processing to enforce case-sensitivity on a per-keyword basis.
	 *
	 * @return Returns {@code true} if case-sensitivity extensions are enabled; {@code false} otherwise.
	 */
	boolean isCaseExtensionEnabled();

	/**
	 * This method reports whether or not the punctuation-sensitivity extension is enabled. When this method returns
	 * {@code false}, the machine operates (in a punctuation-sensitive manner) without any assumptions about punctuation
	 * symbols. When this method returns {@code true}, the machine operates in a punctuation-insensitive manner, but may
	 * apply post-match processing to enforce punctuation-sensitivity on a per-keyword basis.
	 *
	 * <p>
	 * Note: The punctuation extension depends on white space processing. Thus, {@link #getSpace()} must also return
	 * non-null to enable special punctuation processing.
	 * </p>
	 *
	 * @return Returns {@code true} if the punctuation-sensitivity extension is enabled; {@code false} otherwise.
	 */
	boolean isPunctuationExtensionEnabled();

	/**
	 * Determines if a symbol is a punctuation symbol.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a punctuation symbol; {@code false} otherwise.
	 *
	 * @throws IllegalStateException
	 *             if called when punctuation extension is disabled.
	 */
	boolean isPunctuation(final S a_);

	/**
	 * This method reports whether or not the word-break extension is enabled. When this method returns false, the
	 * machine operates without any assumptions word breaks. When this method returns true, the machine will only
	 * recognize "whole word" keywords; that is, keywords that are delimited by word break symbols.
	 *
	 * @return Returns {@code true} if the word-break extension is enabled; {@code false} otherwise.
	 */
	boolean isWordBreakExtensionEnabled();

	/**
	 * Determines if a symbol is a word break symbol. Keywords <i>may</i> contain word break symbols, but if work-break
	 * extension is enabled, a matching operation only matches keywords delimited by word-break symbols.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a word break symbol; {@code false} otherwise.
	 *
	 * @throws IllegalStateException
	 *             if called when word-break extension is disabled.
	 */
	boolean isWordBreak(final S a_);

	/**
	 * Determines if a symbol is a white space symbol. This method must return {@code true} for the symbol, if any,
	 * returned by {@link #getSpace()}. In addition, this method may return {@code true} for other symbols that should
	 * also be considered as white space.
	 *
	 * <p>
	 * Note that there is no {@code isWhitespaceEntensionEnabled()} property. The "white space" extension is always
	 * "enabled", but implementations can effectively disable special white space handling by having this
	 * {@link #getSpace()} always return {@code null}.
	 * </p>
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a white space symbol; {@code false} otherwise.
	 */
	boolean isWhitespace(final S a_);

	/**
	 * This method transforms a symbol to lower case using whatever transformation rules are appropriate for the symbol
	 * type S.
	 *
	 * @param a_
	 *            specifies the symbol to transform, which may be {@code null};
	 *
	 * @return Returns the lower case version of the specified symbol, or the original symbol if no lower case version
	 *         is applicable.
	 *
	 * @throws IllegalStateException
	 *             if called when case-sensitivity extension is disabled.
	 */
	S toLowerCase(final S a_);
}
