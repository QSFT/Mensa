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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class LoremIpsumTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private static final String RAW_TEXT = "Hello, world!  How are you?\n\nI am\t fine.  Thanks for asking.\n";
	private static final String NORMALIZED_TEXT = "Hello, world! How are you?\nI am fine. Thanks for asking.";

	private static final String[] EXPECTED_PARAGRAPHS =
	{
			"Hello, world! How are you?",
			"I am fine. Thanks for asking."
	};

	private static final String[] EXPECTED_SENTENCES =
	{
			"Hello, world!",
			"How are you?",
			"I am fine.",
			"Thanks for asking."
	};

	private static final String[] EXPECTED_WORDS =
	{
			"Hello,",
			"world!",
			"How",
			"are",
			"you?",
			"I",
			"am",
			"fine.",
			"Thanks",
			"for",
			"asking."
	};

	private LoremIpsum loremIpsum;

	@Before
	public void setUp() throws Exception
	{
		loremIpsum = new LoremIpsum(RAW_TEXT);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#LoremIpsum()}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCtor() throws IOException
	{
		sanityCheckDefaultText(new LoremIpsum());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#LoremIpsum(java.lang.String)}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@Test
	public void testLoremCtorString() throws IOException
	{
		final String text = "hi";
		loremIpsum = new LoremIpsum(text);
		Assert.assertEquals(text, loremIpsum.getText());
		Assert.assertEquals(1, loremIpsum.getNumParagraphs());
		Assert.assertEquals(1, loremIpsum.getNumSentences());
		Assert.assertEquals(1, loremIpsum.getNumWords());
	}

	@Test
	public void testLoremCtorString_Null() throws IOException
	{
		loremIpsum = new LoremIpsum(null);
		Assert.assertNull(loremIpsum.getText());
		Assert.assertEquals(0, loremIpsum.getNumParagraphs());
		Assert.assertEquals(0, loremIpsum.getNumSentences());
		Assert.assertEquals(0, loremIpsum.getNumWords());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getNumParagraphs()}.
	 */
	@Test
	public void testGetNumParagraphs()
	{
		Assert.assertEquals(EXPECTED_PARAGRAPHS.length, loremIpsum.getNumParagraphs());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getNumSentences()}.
	 */
	@Test
	public void testGetNumSentences()
	{
		Assert.assertEquals(EXPECTED_SENTENCES.length, loremIpsum.getNumSentences());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getNumWords()}.
	 */
	@Test
	public void testGetNumWords()
	{
		Assert.assertEquals(EXPECTED_WORDS.length, loremIpsum.getNumWords());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getText()}.
	 */
	@Test
	public void testGetText()
	{
		Assert.assertEquals(NORMALIZED_TEXT, loremIpsum.getText());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getParagraph(int)}.
	 */
	@Test
	public void testGetParagraph()
	{
		for (int i = 0; i < EXPECTED_PARAGRAPHS.length; ++i)
		{
			Assert.assertEquals(
					String.format("unexpected paragraph[%d]; ", i),
					EXPECTED_PARAGRAPHS[i], loremIpsum.getParagraph(i));
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetParagraph_Underflow()
	{
		loremIpsum.getParagraph(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetParagraph_Overflow()
	{
		loremIpsum.getParagraph(loremIpsum.getNumParagraphs());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getSentence(int)}.
	 */
	@Test
	public void testGetSentence()
	{
		for (int i = 0; i < EXPECTED_SENTENCES.length; ++i)
		{
			Assert.assertEquals(
					String.format("unexpected sentence[%d]; ", i),
					EXPECTED_SENTENCES[i], loremIpsum.getSentence(i));
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetSentence_Underflow()
	{
		loremIpsum.getSentence(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetSentence_Overflow()
	{
		loremIpsum.getSentence(loremIpsum.getNumSentences());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#getWord(int)}.
	 */
	@Test
	public void testGetWord()
	{
		for (int i = 0; i < EXPECTED_WORDS.length; ++i)
		{
			Assert.assertEquals(
					String.format("unexpected word[%d]; ", i),
					EXPECTED_WORDS[i], loremIpsum.getWord(i));
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetWord_Underflow()
	{
		loremIpsum.getWord(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetWord_Overflow()
	{
		loremIpsum.getWord(loremIpsum.getNumWords());
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#loadTextResource()}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@Test
	public void testLoadTextResource() throws IOException
	{
		loremIpsum.loadTextResource();
		sanityCheckDefaultText(loremIpsum);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#loadTextResource(java.lang.String)}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@Test
	public void testLoadTextResourceString() throws IOException
	{
		loremIpsum.loadTextResource(LoremIpsum.LOREM_IPSUM_RESOURCE);
		sanityCheckDefaultText(loremIpsum);
	}

	@Test(expected = FileNotFoundException.class)
	public void testLoadTextResourceString_BadResource() throws IOException
	{
		loremIpsum.loadTextResource("bad-resource");
	}

	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS",
			justification = "Testing null argument")
	public void testLoadTextResourceString_IllegalArgument() throws IOException
	{
		loremIpsum.loadTextResource(null);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#normalize(java.lang.String)}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testNormalize() throws IOException
	{
		Assert.assertEquals(NORMALIZED_TEXT, LoremIpsum.normalize(RAW_TEXT));
		Assert.assertEquals(NORMALIZED_TEXT, LoremIpsum.normalize(NORMALIZED_TEXT));
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testNormalize_IllegalArgument() throws IOException
	{
		LoremIpsum.normalize(null);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.LoremIpsum#setText(java.lang.String)}.
	 *
	 * @throws IOException
	 *             if thrown by code under test
	 */
	@Test
	public void testSetText() throws IOException
	{
		final String text = "hi";
		loremIpsum.setText(text);
		Assert.assertEquals(text, loremIpsum.getText());
		Assert.assertEquals(1, loremIpsum.getNumParagraphs());
		Assert.assertEquals(1, loremIpsum.getNumSentences());
		Assert.assertEquals(1, loremIpsum.getNumWords());
	}

	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS",
			justification = "Testing null argument")
	public void testSetText_Null() throws IOException
	{
		loremIpsum.setText(null);
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private static void sanityCheckDefaultText(final LoremIpsum loremIpsum_)
	{
		// Sanity check using hard-coded knowledge of default resource...
		Assert.assertEquals("Lorem", loremIpsum_.getWord(0));
		Assert.assertEquals("Ipsum", loremIpsum_.getWord(1));
		Assert.assertEquals("laborum.", loremIpsum_.getWord(loremIpsum_.getNumWords() - 1));
		Assert.assertEquals(6, loremIpsum_.getNumParagraphs());
	}
}
