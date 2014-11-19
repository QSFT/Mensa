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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import com.dell.mensa.util.Verify;

/**
 * {@link CharacterInputStreamTextSource} is a concrete {@link AbstractCharacterTextSource} for reading
 * {@link Character} symbols from an {@link InputStream}
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class CharacterInputStreamTextSource extends AbstractCharacterTextSource
{
	private static final String PARM_inputStream = "inputStream_";
	private static final String PARM_charset = "charset_";

	// =========================================================================
	// Properties
	// =========================================================================
	private final InputStream inputStream;
	private final Charset charset;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance that reads {@link Character} symbols from the specified input stream using the
	 * specified character set.
	 *
	 * @param inputStream_
	 *            the input stream containing the {@link Character} symbols.
	 *
	 * @param charset_
	 *            the character set used to interpret the stream content.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified input stream is {@code null}
	 */
	public CharacterInputStreamTextSource(final InputStream inputStream_, final Charset charset_)
	{
		super();
		Verify.notNull(inputStream_, PARM_inputStream);
		Verify.notNull(charset_, PARM_charset);
		this.inputStream = inputStream_;
		this.charset = charset_;
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
		return new InputStreamReader(new BufferedInputStream(inputStream), charset);
	}
}
