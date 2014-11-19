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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.ITextSource;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterInputStreamTextSourceTest extends AbstractCharacterTextSourceTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private File file;
	private Charset charset;

	/**
	 * Creates a new {@link CharacterInputStreamTextSource} instance for testing.
	 *
	 * @param inputStream_
	 *            the input stream containing the {@link Character} symbols.
	 *
	 * @param charset_
	 *            the character set used to interpret the stream content.
	 *
	 * @return Returns a new {@link CharacterInputStreamTextSource} instance.
	 */
	private static ITextSource<Character> createTextSource(final InputStream inputStream_, final Charset charset_)
	{
		return new CharacterInputStreamTextSource(inputStream_, charset_);
	}

	@SuppressWarnings("resource")
	@Override
	protected ITextSource<Character> createTextSource(final String text_) throws IOException
	{
		try (final FileOutputStream fos = new FileOutputStream(file);
				final OutputStreamWriter writer = new OutputStreamWriter(fos, charset))
		{
			writer.write(text_);
		}

		final InputStream inputStream = new FileInputStream(file);
		return createTextSource(inputStream, charset);
	}

	@Override
	@Before
	public void setUp() throws IOException
	{
		charset = Charset.forName("UTF8");
		file = File.createTempFile("junit.textSource.", ".txt");

		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws IOException
	{
		super.tearDown();

		file.delete();
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link CharacterInputStreamTextSource#CharacterInputStreamTextSource(InputStream, Charset)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_NullFile()
	{
		createTextSource(null, charset);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_NullCharset()
	{
		final byte[] bytes = {};
		createTextSource(new ByteArrayInputStream(bytes), null);
	}
}
