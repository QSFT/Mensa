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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import com.dell.mensa.ITextSource;
import com.dell.mensa.util.Verify;

/**
 * {@link CharacterStringTextSource} is a concrete {@link ITextSource} for reading {@link Character} symbols from a
 * {@link String}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class CharacterStringTextSource extends AbstractCharacterTextSource
{
	private static final String PARM_text = "text_";

	// =========================================================================
	// Properties
	// =========================================================================
	private final String text;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance that reads {@link Character} symbols from the specified string.
	 *
	 * @param text_
	 *            the string containing the {@link Character} symbols.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified string is {@code null}
	 */
	public CharacterStringTextSource(final String text_)
	{
		super();
		Verify.notNull(text_, PARM_text);
		this.text = text_;
	}

	// =========================================================================
	// AbstractCharacterTextSource abstract methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.character.AbstractCharacterTextSource#getReader()
	 */
	@Override
	protected Reader getReader() throws IOException
	{
		return new StringReader(text);
	}
}
