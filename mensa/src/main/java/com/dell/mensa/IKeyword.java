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
 * {@link IKeyword} is a keyword consisting of a non-empty, finite sequence of symbols.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IKeyword<S>
{
	/**
	 * An implementation must provide an implementation of {@link #hashCode()} and {@link #equals(Object)} to ensure
	 * instances have value semantics. Specifically, two keyword instances must be considered equal if, and only if, all
	 * of the following are true:
	 *
	 * <ul>
	 *
	 * <li>they consist of an identical sequence of symbols;</li>
	 *
	 * <li>they have the same case-sensitivity setting, indicated by {@link #isCaseSensitive()}; and</li>
	 *
	 * <li>they have the same punctuation-sensitivity setting, indicated by {@link #isPunctuationSensitive()}; and</li>
	 *
	 * <li>they have the same user data. (User data objects are compared using {@code equals()}.)</li>
	 *
	 * </ul>
	 *
	 * @param object_
	 *            the object to compare with this keyword.
	 *
	 * @return Returns {@code true} if the specified object is a keyword consisting of an identical sequence of values
	 *         as this keyword; {@code false} otherwise.
	 */
	@Override
	boolean equals(final Object object_);

	/**
	 * Returns the user data object for this keyword. A user data object is an application-defined object associated
	 * with with the keyword. For example, an application might use this mechanism might include information about the
	 * taxonomy to which each keyword belongs.
	 *
	 * @return Returns the user data object for this keyword, which may be {@code null}.
	 */
	Object getUserData();

	/**
	 * An implementation must provide an implementation of {@link #hashCode()} and {@link #equals(Object)} to ensure
	 * instances have value semantics. See {@link #equals(Object)} for further discussion.
	 *
	 * @return Returns a hash code for this instances.
	 */
	@Override
	int hashCode();

	/**
	 * Indicates whether or not this keyword is case-sensitive. This method is only meaningful when the case-sensitivity
	 * extension is enabled.
	 *
	 * @return Returns {@code true} if this keyword is case-sensitive; {@code false} otherwise.
	 *
	 * @see ISymbolClassifier#isCaseExtensionEnabled()
	 */
	boolean isCaseSensitive();

	/**
	 * Indicates whether or not this keyword is punctuation-sensitive. This method is only meaningful when the
	 * case-sensitivity extension is enabled.
	 *
	 * @return Returns {@code true} if this keyword is punctuation-sensitive; {@code false} otherwise.
	 *
	 * @see ISymbolClassifier#isPunctuationExtensionEnabled()
	 */
	boolean isPunctuationSensitive();

	/**
	 * Returns the number of symbols in the keyword, i.e., the keyword length.
	 *
	 * @return Returns the number of symbols in the keyword.
	 */
	int length();

	/**
	 * Returns the symbol at the specified index. An index ranges from 0 to {@link #length()} - 1. The first symbol of
	 * the keyword is at index 0, the next at index 1, and so on, as for array indexing.
	 *
	 * @param index_
	 *            the index of the symbol to return
	 *
	 * @return Returns the symbol at the specified index.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the index argument is negative or not less than the length of this keyword.
	 */
	S symbolAt(int index_);

	/**
	 * Returns a human-readable string representation of this keyword. This method is used for diagnostic and reporting
	 * purposes only.
	 *
	 * @return Returns a human-readable string representation of this keyword.
	 */
	@Override
	String toString();
}
