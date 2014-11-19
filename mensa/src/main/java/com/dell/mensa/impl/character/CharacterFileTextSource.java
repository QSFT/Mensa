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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import com.dell.mensa.util.Verify;

/**
 * {@link CharacterFileTextSource} is a concrete {@link AbstractCharacterTextSource} for reading {@link Character}
 * symbols from a {@link File}
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class CharacterFileTextSource extends AbstractCharacterTextSource
{
	private static final String PARM_file = "file_";
	private static final String PARM_charset = "charset_";

	// =========================================================================
	// Properties
	// =========================================================================
	private final File file;
	private final Charset charset;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance that reads {@link Character} symbols from the specified file using the specified
	 * character set.
	 *
	 * @param file_
	 *            the file containing the {@link Character} symbols.
	 *
	 * @param charset_
	 *            the character set used to interpret the file content.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified file is {@code null}
	 */
	public CharacterFileTextSource(final File file_, final Charset charset_)
	{
		super();
		Verify.notNull(file_, PARM_file);
		Verify.notNull(charset_, PARM_charset);
		this.file = file_;
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
		final InputStream is = new FileInputStream(file);
		return new InputStreamReader(is, charset);
	}
}
