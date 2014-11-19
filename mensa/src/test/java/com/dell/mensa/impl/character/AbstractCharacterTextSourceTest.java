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
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public abstract class AbstractCharacterTextSourceTest
{
	// =========================================================================
	// Fixture
	// =========================================================================

	private String expectedText;
	private String actualText;
	private ITextSource<Character> textSource;

	/**
	 * Create the text source to be tested.
	 *
	 * @param text_
	 *            The raw text content of the text source.
	 *
	 * @return Returns a concrete {@link ITextSource} instance for testing.
	 *
	 * @throws IOException
	 *             if an error occurs creating the text source.
	 */
	protected abstract ITextSource<Character> createTextSource(final String text_) throws IOException;

	@SuppressWarnings("static-method")
	protected String getActualText()
	{
		// Test string containing surrogate pair character for the Unicode character U+2070E
		// as well as several "extra" whitespace symbols.
		return "abc-xyz \uD841\uDF0E xyz\nHow  about  that!  \n\n  ";
	}

	@SuppressWarnings("static-method")
	protected String getExpectedText()
	{
		return "abc xyz \uD841\uDF0E xyz How about that\n";
	}

	@Before
	public void setUp() throws IOException
	{
		expectedText = getExpectedText();
		actualText = getActualText();
		textSource = createTextSource(actualText);
	}

	@After
	public void tearDown() throws IOException
	{
		textSource.close();
	}

	// =========================================================================
	// Test methods
	// =========================================================================

	@SuppressWarnings("static-method")
	@Test
	public void testCharacterConstants()
	{
		Assert.assertEquals(0x0009, AbstractCharacterTextSource.TAB);
		Assert.assertEquals(0x000A, AbstractCharacterTextSource.LF);
		Assert.assertEquals(0x000B, AbstractCharacterTextSource.VTAB);
		Assert.assertEquals(0x000C, AbstractCharacterTextSource.FF);
		Assert.assertEquals(0x000D, AbstractCharacterTextSource.CR);
		Assert.assertEquals(0x001C, AbstractCharacterTextSource.FILESEP);
		Assert.assertEquals(0x001D, AbstractCharacterTextSource.GROUPSEP);
		Assert.assertEquals(0x001E, AbstractCharacterTextSource.RECORDSEP);
		Assert.assertEquals(0x001F, AbstractCharacterTextSource.UNITSEP);
		Assert.assertEquals(0x0020, AbstractCharacterTextSource.SPACE);
		Assert.assertEquals(0x00A0, AbstractCharacterTextSource.NBSP);
		Assert.assertEquals(0x2007, AbstractCharacterTextSource.FIGURESPACE);
		Assert.assertEquals(0x202F, AbstractCharacterTextSource.NNBSP);
	}

	@Test
	public void testTheBasics() throws IOException
	{
		textSource = new CharacterStringTextSource("  a \n \nb\n\f\n");
		textSource.open();

		final ITailBuffer<Character> buffer = textSource.getTailBuffer();
		final ITailBuffer<Character> rawBuffer = textSource.getRawTailBuffer();

		Assert.assertNotNull(buffer);
		Assert.assertNotNull(rawBuffer);

		// Before first read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(0, textSource.getPosition());
		Assert.assertEquals(0, buffer.end());
		Assert.assertEquals(0, rawBuffer.end());
		Assert.assertEquals(Character.valueOf(' '), textSource.peek());

		// First read
		Assert.assertEquals(Character.valueOf(' '), textSource.read());

		// After first read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(2, textSource.getPosition());
		Assert.assertEquals(2, buffer.end());
		Assert.assertEquals(2, rawBuffer.end());
		Assert.assertEquals(Character.valueOf('a'), textSource.peek());

		// Second read
		Assert.assertEquals(Character.valueOf('a'), textSource.read());

		// After second read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(3, textSource.getPosition());
		Assert.assertEquals(3, buffer.end());
		Assert.assertEquals(3, rawBuffer.end());
		Assert.assertEquals(Character.valueOf('\n'), textSource.peek());

		// Third read
		Assert.assertEquals(Character.valueOf('\n'), textSource.read());

		// After third read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(7, textSource.getPosition());
		Assert.assertEquals(7, buffer.end());
		Assert.assertEquals(7, rawBuffer.end());
		Assert.assertEquals(Character.valueOf('b'), textSource.peek());

		Assert.assertEquals(Character.valueOf(' '), buffer.symbolAt(0));
		Assert.assertEquals(null, buffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), buffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf('\n'), buffer.symbolAt(3));
		Assert.assertEquals(null, buffer.symbolAt(4));
		Assert.assertEquals(null, buffer.symbolAt(5));
		Assert.assertEquals(null, buffer.symbolAt(6));

		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(0));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), rawBuffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(3));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(4));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(5));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(6));

		// Fourth read
		Assert.assertEquals(Character.valueOf('b'), textSource.read());

		// After fourth read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(8, textSource.getPosition());
		Assert.assertEquals(8, buffer.end());
		Assert.assertEquals(8, rawBuffer.end());
		Assert.assertEquals(Character.valueOf('\f'), textSource.peek());

		Assert.assertEquals(Character.valueOf(' '), buffer.symbolAt(0));
		Assert.assertEquals(null, buffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), buffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf('\n'), buffer.symbolAt(3));
		Assert.assertEquals(null, buffer.symbolAt(4));
		Assert.assertEquals(null, buffer.symbolAt(5));
		Assert.assertEquals(null, buffer.symbolAt(6));

		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(0));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), rawBuffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(3));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(4));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(5));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(6));

		Assert.assertEquals(Character.valueOf('b'), rawBuffer.symbolAt(7));

		// fifth read
		Assert.assertEquals(Character.valueOf('\f'), textSource.read());

		// After fifth read
		Assert.assertTrue(textSource.isEof());
		Assert.assertEquals(11, textSource.getPosition());
		Assert.assertEquals(11, buffer.end());
		Assert.assertEquals(11, rawBuffer.end());
		Assert.assertEquals(null, textSource.peek());

		Assert.assertEquals(Character.valueOf(' '), buffer.symbolAt(0));
		Assert.assertEquals(null, buffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), buffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf('\n'), buffer.symbolAt(3));
		Assert.assertEquals(null, buffer.symbolAt(4));
		Assert.assertEquals(null, buffer.symbolAt(5));
		Assert.assertEquals(null, buffer.symbolAt(6));

		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(0));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(1));
		Assert.assertEquals(Character.valueOf('a'), rawBuffer.symbolAt(2));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(3));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(4));
		Assert.assertEquals(Character.valueOf(' '), rawBuffer.symbolAt(5));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(6));

		Assert.assertEquals(Character.valueOf('b'), rawBuffer.symbolAt(7));

		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(8));
		Assert.assertEquals(Character.valueOf('\f'), rawBuffer.symbolAt(9));
		Assert.assertEquals(Character.valueOf('\n'), rawBuffer.symbolAt(10));

		textSource.close();
	}

	@Test
	public void testTabs() throws IOException
	{
		do_testWhitespace("\t", ' ');
		do_testWhitespace("\t\t", ' ');
		do_testWhitespace("\t\t\t", ' ');

		do_testWhitespace(" \t ", ' ');
		do_testWhitespace(" \t\t ", ' ');
		do_testWhitespace(" \t\t\t ", ' ');
	}

	@Test
	public void testLineFeeds() throws IOException
	{
		do_testWhitespace("\n", ' ');
		do_testWhitespace("\n\n", '\n');
		do_testWhitespace("\n\n\n", '\n');

		do_testWhitespace(" \n ", ' ');
		do_testWhitespace(" \n\n ", '\n');
		do_testWhitespace(" \n\n\n ", '\n');
	}

	@Test
	public void testVertialTabs() throws IOException
	{
		do_testWhitespace("\u000B", ' ');
		do_testWhitespace("\u000B\u000B", ' ');
		do_testWhitespace("\u000B\u000B\u000B", ' ');

		do_testWhitespace(" \u000B ", ' ');
		do_testWhitespace(" \u000B\u000B ", ' ');
		do_testWhitespace(" \u000B\u000B\u000B ", ' ');
	}

	@Test
	public void testFormfeeds() throws IOException
	{
		do_testWhitespace("\f", '\f');
		do_testWhitespace("\f\f", '\f');
		do_testWhitespace("\f\f\f", '\f');
		do_testWhitespace("\n\f\n", '\f'); // precedence over newline

		do_testWhitespace(" \f ", '\f');
		do_testWhitespace(" \f\f ", '\f');
		do_testWhitespace(" \f\f\f ", '\f');
		do_testWhitespace(" \n\f\n ", '\f'); // precedence over newline
	}

	@Test
	public void testCarriageReturns() throws IOException
	{
		do_testWhitespace("\r", ' ');
		do_testWhitespace("\r\r", ' ');
		do_testWhitespace("\r\r\r", ' ');

		do_testWhitespace(" \r ", ' ');
		do_testWhitespace(" \r\r ", ' ');
		do_testWhitespace(" \r\r\r ", ' ');
	}

	@Test
	public void testFileSeps() throws IOException
	{
		do_testWhitespace("\u001C", ' ');
		do_testWhitespace("\u001C\u001C", ' ');
		do_testWhitespace("\u001C\u001C\u001C", ' ');

		do_testWhitespace(" \u001C ", ' ');
		do_testWhitespace(" \u001C\u001C ", ' ');
		do_testWhitespace(" \u001C\u001C\u001C ", ' ');
	}

	@Test
	public void testGroupSeps() throws IOException
	{
		do_testWhitespace("\u001D", ' ');
		do_testWhitespace("\u001D\u001D", ' ');
		do_testWhitespace("\u001D\u001D\u001D", ' ');

		do_testWhitespace(" \u001D ", ' ');
		do_testWhitespace(" \u001D\u001D ", ' ');
		do_testWhitespace(" \u001D\u001D\u001D ", ' ');
	}

	@Test
	public void testRecordSeps() throws IOException
	{
		do_testWhitespace("\u001E", ' ');
		do_testWhitespace("\u001E\u001E", ' ');
		do_testWhitespace("\u001E\u001E\u001E", ' ');

		do_testWhitespace(" \u001E ", ' ');
		do_testWhitespace(" \u001E\u001E ", ' ');
		do_testWhitespace(" \u001E\u001E\u001E ", ' ');
	}

	@Test
	public void testUnitSeps() throws IOException
	{
		do_testWhitespace("\u001F", ' ');
		do_testWhitespace("\u001F\u001F", ' ');
		do_testWhitespace("\u001F\u001F\u001F", ' ');

		do_testWhitespace(" \u001F ", ' ');
		do_testWhitespace(" \u001F\u001F ", ' ');
		do_testWhitespace(" \u001F\u001F\u001F ", ' ');
	}

	@Test
	public void testSpace() throws IOException
	{
		do_testWhitespace(" ", ' ');
		do_testWhitespace("  ", ' ');
		do_testWhitespace("   ", ' ');
	}

	@Test
	public void testNBSP() throws IOException
	{
		do_testWhitespace("\u00A0", ' ');
		do_testWhitespace("\u00A0\u00A0", ' ');
		do_testWhitespace("\u00A0\u00A0\u00A0", ' ');

		do_testWhitespace(" \u00A0 ", ' ');
		do_testWhitespace(" \u00A0\u00A0 ", ' ');
		do_testWhitespace(" \u00A0\u00A0\u00A0 ", ' ');
	}

	@Test
	public void testFigureSpace() throws IOException
	{
		do_testWhitespace("\u2007", ' ');
		do_testWhitespace("\u2007\u2007", ' ');
		do_testWhitespace("\u2007\u2007\u2007", ' ');

		do_testWhitespace(" \u2007 ", ' ');
		do_testWhitespace(" \u2007\u2007 ", ' ');
		do_testWhitespace(" \u2007\u2007\u2007 ", ' ');
	}

	@Test
	public void testNNBSP() throws IOException
	{
		do_testWhitespace("\u202F", ' ');
		do_testWhitespace("\u202F\u202F", ' ');
		do_testWhitespace("\u202F\u202F\u202F", ' ');

		do_testWhitespace(" \u202F ", ' ');
		do_testWhitespace(" \u202F\u202F ", ' ');
		do_testWhitespace(" \u202F\u202F\u202F ", ' ');
	}

	@Test
	public void testCRLF() throws IOException
	{
		do_testWhitespace("\r\n", ' ');
		do_testWhitespace("\r\n\r\n", '\n');
		do_testWhitespace("\r\n\r\n\r\n", '\n');

		do_testWhitespace(" \r\n ", ' ');
		do_testWhitespace(" \r\n\r\n ", '\n');
		do_testWhitespace(" \r\n\r\n\r\n ", '\n');
	}

	@Test
	public void testLFCR() throws IOException
	{
		do_testWhitespace("\n\r", ' ');
		do_testWhitespace("\n\r\n\r", '\n');
		do_testWhitespace("\n\r\n\r\n\r", '\n');

		do_testWhitespace(" \n\r ", ' ');
		do_testWhitespace(" \n\r\n\r ", '\n');
		do_testWhitespace(" \n\r\n\r\n\r ", '\n');
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#close()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testClose() throws IOException
	{
		Assert.assertFalse(textSource.isOpen());
		textSource.open();
		Assert.assertTrue(textSource.isOpen());
		textSource.close();
		Assert.assertFalse(textSource.isOpen());

		// Can call multiple times
		textSource.close();
		Assert.assertFalse(textSource.isOpen());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#isEof()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testIsEof() throws IOException
	{
		textSource.open();
		for (int i = 0; i < expectedText.length(); i++)
		{
			Assert.assertFalse(textSource.isEof());
			// Can call multiple times
			Assert.assertFalse(textSource.isEof());

			textSource.read();
		}

		Assert.assertTrue(textSource.isEof());
		// Can call multiple times
		Assert.assertTrue(textSource.isEof());
	}

	@Test(expected = IllegalStateException.class)
	public void testIsEof_NotOpen()
	{
		textSource.isEof();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#isOpen()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testIsOpen() throws IOException
	{
		Assert.assertFalse(textSource.isOpen());
		textSource.open();
		Assert.assertTrue(textSource.isOpen());
		textSource.close();
		Assert.assertFalse(textSource.isOpen());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#open()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testOpen() throws IOException
	{
		Assert.assertFalse(textSource.isOpen());
		textSource.open();
		Assert.assertTrue(textSource.isOpen());
		textSource.close();
		Assert.assertFalse(textSource.isOpen());

		// Can reopen? ITextSource does not not that it must be possible to reopen a source. While
		// some implementation may support reopening the source, this is not a requirement.

		// textSource.open();
		// Assert.assertTrue(textSource.isOpen());
		// textSource.close();
		// Assert.assertFalse(textSource.isOpen());
	}

	@Test(expected = IllegalStateException.class)
	public void testOpen_AlreadyOpen() throws IOException
	{
		try
		{
			textSource.open();
		}
		catch (final IllegalStateException e)
		{
			Assert.fail("unexpected exception");
		}

		textSource.open();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#peek()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testPeek() throws IOException
	{
		textSource.open();

		for (int i = 0; i < expectedText.length(); i++)
		{
			final Character expected = Character.valueOf(expectedText.charAt(i));

			final String msg = String.format("unexpected character at postion %d; ", i);
			Assert.assertEquals(msg,
					expected, textSource.peek());

			// Can call multiple times
			Assert.assertEquals(msg,
					expected, textSource.peek());

			textSource.read();
		}

		Assert.assertNull(textSource.peek());

		// Can call multiple times
		Assert.assertNull(textSource.peek());
	}

	@Test(expected = IllegalStateException.class)
	public void testPeek_NotOpen()
	{
		textSource.peek();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#getPosition()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testGetPosition() throws IOException
	{
		textSource.open();

		final ITailBuffer<Character> buffer = textSource.getTailBuffer();
		final ITailBuffer<Character> rawBuffer = textSource.getRawTailBuffer();

		Assert.assertNotNull(buffer);
		Assert.assertNotNull(rawBuffer);

		for (int i = 0; i < expectedText.length(); i++)
		{
			final long expectedPosition = buffer.end();
			Assert.assertEquals(expectedPosition, textSource.getPosition());
			Assert.assertEquals(expectedPosition, rawBuffer.end());

			// Can call multiple times
			final long position = textSource.getPosition();
			Assert.assertEquals(expectedPosition, position);

			textSource.read();
		}

		Assert.assertTrue(textSource.isEof());
		Assert.assertEquals(actualText.length(), textSource.getPosition());

		// Can call multiple times
		Assert.assertEquals(actualText.length(), textSource.getPosition());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetPosition_NotOpen()
	{
		textSource.getPosition();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#getTailBuffer()}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testGetTailBuffer_NotOpen()
	{
		textSource.getTailBuffer();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#getRawTailBuffer()}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testGetRawTailBuffer_NotOpen()
	{
		textSource.getRawTailBuffer();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AbstractTextSource#read()}.
	 *
	 * @throws IOException
	 *             if thrown by the code being tested
	 */
	@Test
	public void testRead() throws IOException
	{
		textSource.open();

		for (int i = 0; i < expectedText.length(); i++)
		{
			final Character expected = Character.valueOf(expectedText.charAt(i));

			final String msg = String.format("unexpected character at postion %d; ", i);
			Assert.assertEquals(msg,
					expected, textSource.read());
		}

		Assert.assertTrue(textSource.isEof());
	}

	@Test(expected = IOException.class)
	public void testRead_BeyondEof() throws IOException
	{
		textSource.open();

		try
		{
			while (!textSource.isEof())
			{
				textSource.read();
			}
		}
		catch (final IOException e)
		{
			Assert.fail("unexpected exception");
		}

		textSource.read();
	}

	@Test(expected = IllegalStateException.class)
	public void testRead_NotOpen() throws IOException
	{
		textSource.read();
	}

	@Test
	public void testSetPosition() throws IOException
	{
		textSource.open();

		final Map<Long, Character> symbols = new HashMap<>();

		// Read half of the input into the text buffers.
		for (int i = 0; i < expectedText.length() / 2; ++i)
		{
			final long position = textSource.getPosition();
			final Character a = textSource.read();
			symbols.put(position, a);
		}

		// Verify that we can re-read beginning at any position already in the
		// text buffers.
		final ITailBuffer<Character> buffer = textSource.getTailBuffer();
		for (long i = buffer.start(); i < buffer.end(); ++i)
		{
			textSource.setPosition(i);

			for (long j = i; j < buffer.end(); ++j)
			{
				Assert.assertEquals(j, textSource.getPosition());
				Assert.assertEquals(symbols.get(j), textSource.read());
			}
		}

		// Verify that we can read continue reading all the way to EOF.
		textSource.setPosition(0);
		Assert.assertEquals(expectedText, readToEof(textSource));

		// Verify that we can move back before EOF and read again.
		textSource.setPosition(0);
		Assert.assertEquals(expectedText, readToEof(textSource));

		textSource.close();
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetPosition_Underflow() throws IOException
	{
		textSource.open();
		textSource.setPosition(textSource.getTailBuffer().start() - 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetPosition_Overflow() throws IOException
	{
		textSource.open();
		textSource.setPosition(textSource.getTailBuffer().end() + 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPosition_BadPosition() throws IOException
	{
		textSource.open();

		// Fill tail buffer.
		while (!textSource.isEof())
		{
			textSource.read();
		}

		// Find an illegal position to set; i.e., one where the tail buffer contains a null symbol.
		final ITailBuffer<Character> buffer = textSource.getTailBuffer();
		for (long i = 0; i < buffer.end(); ++i)
		{
			if (buffer.symbolAt(i) == null)
			{
				textSource.setPosition(i);
			}
		}

		Assert.fail("can't find an illegal position to test!");
	}

	@Test(expected = IllegalStateException.class)
	public void testSetPosition_NotOpen()
	{
		textSource.setPosition(0);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMinDiacritical()
	{
		Assert.assertEquals(192, AbstractCharacterTextSource.MIN_DIACRITICAL);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaxDiacritical()
	{
		Assert.assertEquals(383, AbstractCharacterTextSource.MAX_DIACRITICAL);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaxDiacriticalMapSize()
	{
		Assert.assertEquals(
				2 * (AbstractCharacterTextSource.MAX_DIACRITICAL - AbstractCharacterTextSource.MIN_DIACRITICAL + 1),
				AbstractCharacterTextSource.DIACRITICAL_TO_ASCII_MAP.length);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMaxDiacriticalMapStructure()
	{
		for (int i = AbstractCharacterTextSource.MIN_DIACRITICAL; i <= AbstractCharacterTextSource.MAX_DIACRITICAL; ++i)
		{
			Assert.assertEquals(
					String.format("unexpected entry (i=%d); ", i),
					i, AbstractCharacterTextSource.DIACRITICAL_TO_ASCII_MAP[2 * (i - AbstractCharacterTextSource.MIN_DIACRITICAL)]);
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsDiacritical()
	{
		for (int c = Character.MIN_VALUE; c < Character.MAX_VALUE; ++c)
		{
			final boolean expected = AbstractCharacterTextSource.MIN_DIACRITICAL <= c
					&& c <= AbstractCharacterTextSource.MAX_DIACRITICAL
					&& c != 215
					&& c != 247;
			final boolean actual = AbstractCharacterTextSource.isDiacritical(c);
			Assert.assertEquals(
					String.format("unexpected entry (c=%d); ", c),
					expected,
					actual);
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testMapDiacriticalToASCII()
	{
		// ISO Latin1
		// ----------
		verifyDiacriticalMapping(192, 'a'); // Capital A, grave accent
		verifyDiacriticalMapping(193, 'a'); // Capital A, acute accent
		verifyDiacriticalMapping(194, 'a'); // Capital A, circumflex accent
		verifyDiacriticalMapping(195, 'a'); // Capital A, tilde
		verifyDiacriticalMapping(196, 'a'); // Capital A, dieresis or umlaut mark
		verifyDiacriticalMapping(197, 'a'); // Capital A, ring
		verifyDiacriticalMapping(198, 'a'); // Capital AE diphthong
		verifyDiacriticalMapping(199, 'c'); // Capital C, cedilla
		verifyDiacriticalMapping(200, 'e'); // Capital E, grave accent
		verifyDiacriticalMapping(201, 'e'); // Capital E, acute accent
		verifyDiacriticalMapping(202, 'e'); // Capital E, circumflex accent
		verifyDiacriticalMapping(203, 'e'); // Capital E, dieresis or umlaut mark
		verifyDiacriticalMapping(204, 'i'); // Capital I, grave accent
		verifyDiacriticalMapping(205, 'i'); // Capital I, acute accent
		verifyDiacriticalMapping(206, 'i'); // Capital I, circumflex accent
		verifyDiacriticalMapping(207, 'i'); // Capital I, dieresis or umlaut mark
		verifyDiacriticalMapping(208, 'e'); // Capital Eth, Icelandic
		verifyDiacriticalMapping(209, 'n'); // Capital N, tilde
		verifyDiacriticalMapping(210, 'o'); // Capital O, grave accent
		verifyDiacriticalMapping(211, 'o'); // Capital O, acute accent
		verifyDiacriticalMapping(212, 'o'); // Capital O, circumflex accent
		verifyDiacriticalMapping(213, 'o'); // Capital O, tilde
		verifyDiacriticalMapping(214, 'o'); // Capital O, dieresis or umlaut mark
		verifyDiacriticalMapping(216, 'o'); // Capital O, slash
		verifyDiacriticalMapping(217, 'u'); // Capital U, grave accent
		verifyDiacriticalMapping(218, 'u'); // Capital U, acute accent
		verifyDiacriticalMapping(219, 'u'); // Capital U, circumflex accent
		verifyDiacriticalMapping(220, 'u'); // Capital U, dieresis or umlaut mark
		verifyDiacriticalMapping(221, 'y'); // Capital Y, acute accent
		verifyDiacriticalMapping(222, 'p'); // Capital thorn, Icelandic
		verifyDiacriticalMapping(223, 's'); // Small sharp s, German
		verifyDiacriticalMapping(224, 'a'); // Small a, grave accent
		verifyDiacriticalMapping(225, 'a'); // Small a, acute accent
		verifyDiacriticalMapping(226, 'a'); // Small a, circumflex accent
		verifyDiacriticalMapping(227, 'a'); // Small a, tilde
		verifyDiacriticalMapping(228, 'a'); // Small a, dieresis or umlaut mark
		verifyDiacriticalMapping(229, 'a'); // Small a, ring
		verifyDiacriticalMapping(230, 'a'); // Small ae diphthong
		verifyDiacriticalMapping(231, 'c'); // Small c, cedilla
		verifyDiacriticalMapping(232, 'e'); // Small e, grave accent
		verifyDiacriticalMapping(233, 'e'); // Small e, acute accent
		verifyDiacriticalMapping(234, 'e'); // Small e, circumflex accent
		verifyDiacriticalMapping(235, 'e'); // Small e, dieresis or umlaut mark
		verifyDiacriticalMapping(236, 'i'); // Small i, grave accent
		verifyDiacriticalMapping(237, 'i'); // Small i, acute accent
		verifyDiacriticalMapping(238, 'i'); // Small i, circumflex accent
		verifyDiacriticalMapping(239, 'i'); // Small i, dieresis or umlaut mark
		verifyDiacriticalMapping(240, 'e'); // Small eth, Icelandic
		verifyDiacriticalMapping(241, 'n'); // Small n, tilde
		verifyDiacriticalMapping(242, 'o'); // Small o, grave accent
		verifyDiacriticalMapping(243, 'o'); // Small o, acute accent
		verifyDiacriticalMapping(244, 'o'); // Small o, circumflex accent
		verifyDiacriticalMapping(245, 'o'); // Small o, tilde
		verifyDiacriticalMapping(246, 'o'); // Small o, dieresis or umlaut mark
		verifyDiacriticalMapping(248, 'o'); // Small o, slash
		verifyDiacriticalMapping(249, 'u'); // Small u, grave accent
		verifyDiacriticalMapping(250, 'u'); // Small u, acute accent
		verifyDiacriticalMapping(251, 'u'); // Small u, circumflex accent
		verifyDiacriticalMapping(252, 'u'); // Small u, dieresis or umlaut mark
		verifyDiacriticalMapping(253, 'y'); // Small y, acute accent
		verifyDiacriticalMapping(254, 'p'); // Small thorn, Icelandic
		verifyDiacriticalMapping(255, 'y'); // Small y, dieresis or umlaut mark

		// ISO Latin1 Extended A
		// ---------------------
		verifyDiacriticalMapping(256, 'a'); // Capital A, macron accent
		verifyDiacriticalMapping(257, 'a'); // Small a, macron accent
		verifyDiacriticalMapping(258, 'a'); // Capital A, breve accent
		verifyDiacriticalMapping(259, 'a'); // Small a, breve accent
		verifyDiacriticalMapping(260, 'a'); // Capital A, ogonek accent
		verifyDiacriticalMapping(261, 'a'); // Small a, ogonek accent
		verifyDiacriticalMapping(262, 'c'); // Capital C, acute accent
		verifyDiacriticalMapping(263, 'c'); // Small c, acute accent
		verifyDiacriticalMapping(264, 'c'); // Capital C, circumflex accent
		verifyDiacriticalMapping(265, 'c'); // Small c, circumflex accent
		verifyDiacriticalMapping(266, 'c'); // Capital C, dot accent
		verifyDiacriticalMapping(267, 'c'); // Small c, dot accent
		verifyDiacriticalMapping(268, 'c'); // Capital C, caron accent
		verifyDiacriticalMapping(269, 'c'); // Small c, caron accent
		verifyDiacriticalMapping(270, 'd'); // Capital D, caron accent
		verifyDiacriticalMapping(271, 'd'); // Small d, caron accent
		verifyDiacriticalMapping(272, 'd'); // Capital D, with stroke accent
		verifyDiacriticalMapping(273, 'd'); // Small d, with stroke accent
		verifyDiacriticalMapping(274, 'e'); // Capital E, macron accent
		verifyDiacriticalMapping(275, 'e'); // Small e, macron accent
		verifyDiacriticalMapping(276, 'e'); // Capital E, breve accent
		verifyDiacriticalMapping(277, 'e'); // Small e, breve accent
		verifyDiacriticalMapping(278, 'e'); // Capital E, dot accent
		verifyDiacriticalMapping(279, 'e'); // Small e, dot accent
		verifyDiacriticalMapping(280, 'e'); // Capital E, ogonek accent
		verifyDiacriticalMapping(281, 'e'); // Small e, ogonek accent
		verifyDiacriticalMapping(282, 'e'); // Capital E, caron accent
		verifyDiacriticalMapping(283, 'e'); // Small e, caron accent
		verifyDiacriticalMapping(284, 'g'); // Capital G, circumflex accent
		verifyDiacriticalMapping(285, 'g'); // Small g, circumflex accent
		verifyDiacriticalMapping(286, 'g'); // Capital G, breve accent
		verifyDiacriticalMapping(287, 'g'); // Small g, breve accent
		verifyDiacriticalMapping(288, 'g'); // Capital G, dot accent
		verifyDiacriticalMapping(289, 'g'); // Small g, dot accent
		verifyDiacriticalMapping(290, 'g'); // Capital G, cedilla accent
		verifyDiacriticalMapping(291, 'g'); // Small g, cedilla accent
		verifyDiacriticalMapping(292, 'h'); // Capital H, circumflex accent
		verifyDiacriticalMapping(293, 'h'); // Small h, circumflex accent
		verifyDiacriticalMapping(294, 'h'); // Capital H, with stroke accent
		verifyDiacriticalMapping(295, 'h'); // Small h, with stroke accent
		verifyDiacriticalMapping(296, 'i'); // Capital I, tilde accent
		verifyDiacriticalMapping(297, 'i'); // Small I, tilde accent
		verifyDiacriticalMapping(298, 'i'); // Capital I, macron accent
		verifyDiacriticalMapping(299, 'i'); // Small i, macron accent
		verifyDiacriticalMapping(300, 'i'); // Capital I, breve accent
		verifyDiacriticalMapping(301, 'i'); // Small i, breve accent
		verifyDiacriticalMapping(302, 'i'); // Capital I, ogonek accent
		verifyDiacriticalMapping(303, 'i'); // Small i, ogonek accent
		verifyDiacriticalMapping(304, 'i'); // Capital I, dot accent
		verifyDiacriticalMapping(305, 'i'); // Small dotless i
		verifyDiacriticalMapping(306, 'i'); // Capital ligature IJ
		verifyDiacriticalMapping(307, 'i'); // Small ligature IJ
		verifyDiacriticalMapping(308, 'j'); // Capital J, circumflex accent
		verifyDiacriticalMapping(309, 'j'); // Small j, circumflex accent
		verifyDiacriticalMapping(310, 'k'); // Capital K, cedilla accent
		verifyDiacriticalMapping(311, 'k'); // Small k, cedilla accent
		verifyDiacriticalMapping(312, 'k'); // Small Kra
		verifyDiacriticalMapping(313, 'l'); // Capital L, acute accent
		verifyDiacriticalMapping(314, 'l'); // Small l, acute accent
		verifyDiacriticalMapping(315, 'l'); // Capital L, cedilla accent
		verifyDiacriticalMapping(316, 'l'); // Small l, cedilla accent
		verifyDiacriticalMapping(317, 'l'); // Capital L, caron accent
		verifyDiacriticalMapping(318, 'l'); // Small L, caron accent
		verifyDiacriticalMapping(319, 'l'); // Capital L, middle dot accent
		verifyDiacriticalMapping(320, 'l'); // Small l, middle dot accent
		verifyDiacriticalMapping(321, 'l'); // Capital L, with stroke accent
		verifyDiacriticalMapping(322, 'l'); // Small l, with stroke accent
		verifyDiacriticalMapping(323, 'n'); // Capital N, acute accent
		verifyDiacriticalMapping(324, 'n'); // Small n, acute accent
		verifyDiacriticalMapping(325, 'n'); // Capital N, cedilla accent
		verifyDiacriticalMapping(326, 'n'); // Small n, cedilla accent
		verifyDiacriticalMapping(327, 'n'); // Capital N, caron accent
		verifyDiacriticalMapping(328, 'n'); // Small n, caron accent
		verifyDiacriticalMapping(329, 'n'); // Small N, preceded by apostrophe
		verifyDiacriticalMapping(330, 'n'); // Capital Eng
		verifyDiacriticalMapping(331, 'n'); // Small Eng
		verifyDiacriticalMapping(332, 'o'); // Capital O, macron accent
		verifyDiacriticalMapping(333, 'o'); // Small o, macron accent
		verifyDiacriticalMapping(334, 'o'); // Capital O, breve accent
		verifyDiacriticalMapping(335, 'o'); // Small o, breve accent
		verifyDiacriticalMapping(336, 'o'); // Capital O, with double acute accent
		verifyDiacriticalMapping(337, 'o'); // Small O, with double acute accent
		verifyDiacriticalMapping(338, 'o'); // Capital Ligature OE
		verifyDiacriticalMapping(339, 'o'); // Small Ligature OE
		verifyDiacriticalMapping(340, 'r'); // Capital R, acute accent
		verifyDiacriticalMapping(341, 'r'); // Small R, acute accent
		verifyDiacriticalMapping(342, 'r'); // Capital R, cedilla accent
		verifyDiacriticalMapping(343, 'r'); // Small r, cedilla accent
		verifyDiacriticalMapping(344, 'r'); // Capital R, caron accent
		verifyDiacriticalMapping(345, 'r'); // Small r, caron accent
		verifyDiacriticalMapping(346, 's'); // Capital S, acute accent
		verifyDiacriticalMapping(347, 's'); // Small s, acute accent
		verifyDiacriticalMapping(348, 's'); // Capital S, circumflex accent
		verifyDiacriticalMapping(349, 's'); // Small s, circumflex accent
		verifyDiacriticalMapping(350, 's'); // Capital S, cedilla accent
		verifyDiacriticalMapping(351, 's'); // Small s, cedilla accent
		verifyDiacriticalMapping(352, 's'); // Capital S, caron accent
		verifyDiacriticalMapping(353, 's'); // Small s, caron accent
		verifyDiacriticalMapping(354, 't'); // Capital T, cedilla accent
		verifyDiacriticalMapping(355, 't'); // Small t, cedilla accent
		verifyDiacriticalMapping(356, 't'); // Capital T, caron accent
		verifyDiacriticalMapping(357, 't'); // Small t, caron accent
		verifyDiacriticalMapping(358, 't'); // Capital T, with stroke accent
		verifyDiacriticalMapping(359, 't'); // Small t, with stroke accent
		verifyDiacriticalMapping(360, 'u'); // Capital U, tilde accent
		verifyDiacriticalMapping(361, 'u'); // Small u, tilde accent
		verifyDiacriticalMapping(362, 'u'); // Capital U, macron accent
		verifyDiacriticalMapping(363, 'u'); // Small u, macron accent
		verifyDiacriticalMapping(364, 'u'); // Capital U, breve accent
		verifyDiacriticalMapping(365, 'u'); // Small u, breve accent
		verifyDiacriticalMapping(366, 'u'); // Capital U with ring above
		verifyDiacriticalMapping(367, 'u'); // Small u with ring above
		verifyDiacriticalMapping(368, 'u'); // Capital U, double acute accent
		verifyDiacriticalMapping(369, 'u'); // Small u, double acute accent
		verifyDiacriticalMapping(370, 'u'); // Capital U, ogonek accent
		verifyDiacriticalMapping(371, 'u'); // Small u, ogonek accent
		verifyDiacriticalMapping(372, 'w'); // Capital W, circumflex accent
		verifyDiacriticalMapping(373, 'w'); // Small w, circumflex accent
		verifyDiacriticalMapping(374, 'y'); // Capital Y, circumflex accent
		verifyDiacriticalMapping(375, 'y'); // Small y, circumflex accent
		verifyDiacriticalMapping(376, 'y'); // Capital Y, diaeresis accent
		verifyDiacriticalMapping(377, 'z'); // Capital Z, acute accent
		verifyDiacriticalMapping(378, 'z'); // Small z, acute accent
		verifyDiacriticalMapping(379, 'z'); // Capital Z, dot accent
		verifyDiacriticalMapping(380, 'z'); // Small Z, dot accent
		verifyDiacriticalMapping(381, 'z'); // Capital Z, caron accent
		verifyDiacriticalMapping(382, 'z'); // Small z, caron accent
		verifyDiacriticalMapping(383, 's'); // Small long s

		// Everything else maps to itself
		// ==============================
		for (int c = Character.MIN_VALUE; c < Character.MAX_VALUE; ++c)
		{
			if (c < 192 || c > 383)
			{
				Assert.assertEquals(
						String.format("unexpected entry (c=%d); ", c),
						c,
						AbstractCharacterTextSource.mapDiacriticalToASCII(c));
			}
		}
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private void do_testWhitespace(final String text_, final Character expectedSymbol_) throws IOException
	{
		textSource = new CharacterStringTextSource(text_);
		textSource.open();

		final ITailBuffer<Character> buffer = textSource.getTailBuffer();
		final ITailBuffer<Character> rawBuffer = textSource.getRawTailBuffer();

		Assert.assertNotNull(buffer);
		Assert.assertNotNull(rawBuffer);

		// Before read
		Assert.assertFalse(textSource.isEof());
		Assert.assertEquals(0, textSource.getPosition());
		Assert.assertEquals(0, buffer.end());
		Assert.assertEquals(0, rawBuffer.end());
		Assert.assertEquals(expectedSymbol_, textSource.peek());

		// Read
		Assert.assertEquals(expectedSymbol_, textSource.read());

		// After read
		Assert.assertTrue(textSource.isEof());
		Assert.assertEquals(text_.length(), textSource.getPosition());
		Assert.assertEquals(text_.length(), buffer.end());
		Assert.assertEquals(text_.length(), rawBuffer.end());
		Assert.assertEquals(null, textSource.peek());

		textSource.close();
	}

	private static String readToEof(final ITextSource<Character> textSource_) throws IOException
	{
		final StringBuilder buf = new StringBuilder();

		while (!textSource_.isEof())
		{
			buf.append(textSource_.read());
		}

		return buf.toString();
	}

	private static void verifyDiacriticalMapping(final int c_, final int expectedAscii_)
	{
		Assert.assertEquals(
				String.format("unexpected entry (c=%d); ", c_),
				expectedAscii_, AbstractCharacterTextSource.mapDiacriticalToASCII(c_));
	}
}
