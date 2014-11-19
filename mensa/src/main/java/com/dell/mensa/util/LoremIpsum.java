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
package com.dell.mensa.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * {@link LoremIpsum} is a simple <a href="http://en.wikipedia.org/wiki/Lorem_ipsum">Lorem ipsum</a> text generator.
 * Methods are provided for retrieving words, sentences, paragraphs, or the entire text. Normally, the raw text is read
 * from the {@link #LOREM_IPSUM_RESOURCE} resource, but an application may use {@link #loadTextResource(String)} or
 * {@link #setText(String)} to specify different text.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class LoremIpsum
{
	private static final String PARAM_i = "i_";
	private static final String PARM_text = "text_";
	private static final String PARM_resource = "resource_";

	public static final String LOREM_IPSUM_RESOURCE = "lorem-ipsum.txt";

	private static final String REGEX_PARAGRAPHS = "\\n";
	private static final String REGEX_SENTENCES = "(?<=[\\.\\?!])[ \\n]";
	private static final String REGEX_WORDS = "[ \\n]";

	/**
	 * Raw Lorem Ipsum text. Words are separated by a single space character; paragraphs are delimited by a single
	 * newline character.
	 */
	private String text;

	/**
	 * The Lorem Ipsum text separated into paragraphs at newline boundaries.
	 */
	private String[] paragraphs;

	/**
	 * The Lorem Ipsum text separated into sentences at period boundaries.
	 */
	private String[] sentences;

	/**
	 * The Lorem Ipsum text separated into words at whitespace boundaries.
	 */
	private String[] words;

	/**
	 * Constructs an instance and loads the Lorem Ipsum text from the {@link #LOREM_IPSUM_RESOURCE} resource.
	 *
	 * @throws IOException
	 *             if an error occurs reading the resource
	 */
	public LoremIpsum() throws IOException
	{
		loadTextResource();
	}

	/**
	 * Constructs an instance using the specified Lorem Ipsum text. The specified text may be {@code null}, in which
	 * case the application is responsible for using {@link #setText(String)}, {@link #loadTextResource()}, or
	 * {@link #loadTextResource(String)} to set the text before calling any of the text accessor methods.
	 *
	 * @param text_
	 *            specfies the Lorem Ipsum text, which may be {@code null} to indicate that the text will be loaded
	 *            later. If the text is not {@code null}, it is normalized using {@link #normalize(String)}.
	 *
	 * @throws IOException
	 *             if an error occurs when normalizing the string
	 */
	public LoremIpsum(final String text_) throws IOException
	{
		if (text_ != null)
		{
			setText(text_);
		}
	}

	/**
	 * @return Returns the number of paragraphs.
	 */
	public int getNumParagraphs()
	{
		return paragraphs == null ? 0 : paragraphs.length;
	}

	/**
	 * @return Returns the number of sentences.
	 */
	public int getNumSentences()
	{
		return sentences == null ? 0 : sentences.length;
	}

	/**
	 * @return Returns the number of words.
	 */
	public int getNumWords()
	{
		return words == null ? 0 : words.length;
	}

	/**
	 * Returns the entire (normalized) Lorem Ipsum text.
	 *
	 * @return Returns the entire Lorem Ipsum text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Returns the i-th paragraph.
	 *
	 * @param i_
	 *            specifies the index of the paragraph to return. Must be in the range [0, {@link #getNumParagraphs()}).
	 *
	 * @return Returns the i-th paragraph.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range.
	 */
	public String getParagraph(final int i_)
	{
		return get(paragraphs, i_);
	}

	/**
	 * Returns the i-th sentence.
	 *
	 * @param i_
	 *            specifies the index of the sentence to return. Must be in the range [0, {@link #getNumSentences()}).
	 *
	 * @return Returns the i-th sentence.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range.
	 */
	public String getSentence(final int i_)
	{
		return get(sentences, i_);
	}

	/**
	 * Returns the i-th word.
	 *
	 * @param i_
	 *            specifies the index of the word to return. Must be in the range [0, {@link #getNumWords()}).
	 *
	 * @return Returns the i-th word.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range.
	 */
	public String getWord(final int i_)
	{
		return get(words, i_);
	}

	/**
	 * Loads the Lorem Ipsum text from the {@link #LOREM_IPSUM_RESOURCE} resource.
	 *
	 * @throws IOException
	 *             if an error occurs reading the resource
	 */
	public final void loadTextResource() throws IOException
	{
		loadTextResource(LOREM_IPSUM_RESOURCE);
	}

	/**
	 * Loads the Lorem Ipsum text from the named resource.
	 *
	 * @param resource_
	 *            name of the resource containing Lorem Ipsum text.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified resource name is {@code null} or empty
	 * @throws FileNotFoundException
	 *             if the specified resource could not be found
	 * @throws IOException
	 *             if an error occurs reading the resource
	 */
	public final void loadTextResource(final String resource_) throws IOException
	{
		Verify.notEmpty(resource_, PARM_resource);

		final InputStream is = getClass().getResourceAsStream(resource_);
		if (is == null)
		{
			throw new FileNotFoundException(resource_);
		}

		try (final Reader fileReader = new InputStreamReader(is);
				final BufferedReader reader = new BufferedReader(fileReader))
		{
			final StringBuilder buf = new StringBuilder();

			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				buf.append(line);
				buf.append('\n');
			}

			setText(buf.toString());
		}
	}

	/**
	 * Normalizes the specified text as follows:
	 *
	 * <p>
	 * Newline characters are interpreted as paragraph separators. Blank lines are ignored. All other whitespace is
	 * normalized by replacing any consecutive white space characters with a single space.
	 * </p>
	 *
	 * @param text_
	 *            the text to normalize
	 *
	 * @return Returns the normalized text.
	 *
	 * @throws IOException
	 *             if an error occurs scanning the text
	 */
	public static String normalize(final String text_) throws IOException
	{
		Verify.notNull(text_, PARM_text);

		try (final Reader fileReader = new StringReader(text_);
				final BufferedReader reader = new BufferedReader(fileReader))
		{
			final StringBuilder buf = new StringBuilder();

			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				line = line.trim();
				if (line.length() > 0)
				{
					line = line.replaceAll("\\s\\s+", " ");
					buf.append(line);
					buf.append('\n');
				}
			}

			return buf.toString().trim();
		}
	}

	/**
	 * Sets the Lorem Ipsum text.
	 *
	 * @param text_
	 *            the Lorem Ipsum text to set, which is normalized by {@link #normalize(String)}.
	 * @throws IllegalArgumentException
	 *             if the specified text is {@code null}
	 * @throws IOException
	 *             if an error occurs when normalizing the string
	 */
	public void setText(final String text_) throws IOException
	{
		Verify.notNull(text_, PARM_text);
		text = normalize(text_);
		paragraphs = text.split(REGEX_PARAGRAPHS);
		sentences = text.split(REGEX_SENTENCES);
		words = text.split(REGEX_WORDS);
	}

	private static String get(final String[] array_, final int i_)
	{
		Verify.inRange(i_, 0, array_.length, PARAM_i);
		return array_[i_];
	}
}
