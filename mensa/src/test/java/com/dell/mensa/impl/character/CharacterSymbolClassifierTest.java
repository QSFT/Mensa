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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.ISymbolClassifier;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterSymbolClassifierTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private final static boolean[] booleans =
	{ false, true };

	private CharacterSymbolClassifier classifierDefault;
	private CharacterSymbolClassifier classifierWithExtensions;
	private CharacterSymbolClassifier classifierWithoutExtensions;

	@Before
	public void setUp()
	{
		classifierDefault = new CharacterSymbolClassifier();
		classifierWithExtensions = new CharacterSymbolClassifier(true);
		classifierWithoutExtensions = new CharacterSymbolClassifier(false);
	}

	// =========================================================================
	// Test methods
	// =========================================================================

	@SuppressWarnings("static-method")
	@Test
	public void testGranularCtor()
	{
		for (final boolean bCaseExtensionEnabled : booleans)
		{
			for (final boolean bPunctuationExtensionEnabled : booleans)
			{
				for (final boolean bWordBreakExtensionEnabled : booleans)
				{
					final ISymbolClassifier<Character> classifier =
							new CharacterSymbolClassifier( // NOPMD by <a href="http://www.linkedin.com/in/faseidl/"
															// target="_blank">F. Andy Seidl</a> on 9/23/14 2:12 PM
									bCaseExtensionEnabled,
									bPunctuationExtensionEnabled,
									bWordBreakExtensionEnabled);

					Assert.assertEquals(bCaseExtensionEnabled, classifier.isCaseExtensionEnabled());
					Assert.assertEquals(bPunctuationExtensionEnabled, classifier.isPunctuationExtensionEnabled());
					Assert.assertEquals(bWordBreakExtensionEnabled, classifier.isWordBreakExtensionEnabled());
				}
			}
		}
	}

	/**
	 * Test method for {@link CharacterSymbolClassifier#getSpace()}
	 */
	@Test
	public void testGetSpace()
	{
		final Character space = CharacterSymbolClassifier.SPACE;

		Assert.assertEquals(' ', space.charValue());

		Assert.assertEquals(space, classifierDefault.getSpace());
		Assert.assertEquals(space, classifierWithExtensions.getSpace());
		Assert.assertEquals(space, classifierWithoutExtensions.getSpace());
	}

	/**
	 * Test method for {@link CharacterSymbolClassifier#getMatchPrecisionFunction()}.
	 */
	@Test
	public void testGetMatchPrecisionFunction()
	{
		do_getMatchPrecisionFunctionTest(classifierDefault);
		do_getMatchPrecisionFunctionTest(classifierWithExtensions);
		do_getMatchPrecisionFunctionTest(classifierWithoutExtensions);
	}

	private static void do_getMatchPrecisionFunctionTest(final CharacterSymbolClassifier classifier_)
	{
		final IMatchPrecisionFunction<Character> fn = classifier_.getMatchPrecisionFunction();

		Assert.assertNotNull(fn);
		Assert.assertTrue(fn instanceof CharacterMatchPrecisionFunction);
		Assert.assertSame(fn, classifier_.getMatchPrecisionFunction());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.character.CharacterSymbolClassifier#isCaseExtensionEnabled()} .
	 */
	@Test
	public void testIsCaseExtensionEnabled()
	{
		Assert.assertTrue(classifierDefault.isCaseExtensionEnabled());
		Assert.assertTrue(classifierWithExtensions.isCaseExtensionEnabled());
		Assert.assertFalse(classifierWithoutExtensions.isCaseExtensionEnabled());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.character.CharacterSymbolClassifier#isPunctuationExtensionEnabled()} .
	 */
	@Test
	public void testIsPunctuationExtensionEnabled()
	{
		Assert.assertTrue(classifierDefault.isPunctuationExtensionEnabled());
		Assert.assertTrue(classifierWithExtensions.isPunctuationExtensionEnabled());
		Assert.assertFalse(classifierWithoutExtensions.isPunctuationExtensionEnabled());
	}

	/**
	 * Test method for {@link CharacterSymbolClassifier#isPunctuation(Character)}
	 */
	@Test
	public void testIsPunctuation_Default()
	{
		do_testIsPunctuation(classifierDefault);
	}

	@Test
	public void testIsPunctuation_WithExtensions()
	{
		do_testIsPunctuation(classifierWithExtensions);
	}

	@Test
	public void testIsPunctuation_WithoutExtensions()
	{
		do_testIsPunctuation(classifierWithoutExtensions);
	}

	private static void do_testIsPunctuation(final CharacterSymbolClassifier classifier_)
	{
		Assert.assertFalse(classifier_.isPunctuation(null));

		for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; ++c)
		{
			boolean expected = false;
			for (final int punctuation : AbstractCharacterTextSource.PUNCTUATION)
			{
				if (punctuation == c)
				{
					expected = true;
					break;
				}
			}

			Assert.assertEquals(
					String.format("unexpected result for isPunctuation(0x%04d); ", (int) c),
					expected, classifier_.isPunctuation(c));
		}
	}

	/**
	 * Test method for {@link CharacterSymbolClassifier#isWhitespace(Character)}
	 */
	@Test
	public void testIsWhitespace_Default()
	{
		do_testIsWhitespace(classifierDefault);
	}

	@Test
	public void testIsWhitespace_WithExtensions()
	{
		do_testIsWhitespace(classifierWithExtensions);
	}

	@Test
	public void testIsWhitespace_WithoutExtensions()
	{
		do_testIsWhitespace(classifierWithoutExtensions);
	}

	private static void do_testIsWhitespace(final CharacterSymbolClassifier classifier_)
	{
		Assert.assertFalse(classifier_.isWhitespace(null));

		for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; ++c)
		{
			final boolean expected = c == AbstractCharacterTextSource.TAB ||
					c == AbstractCharacterTextSource.LF ||
					c == AbstractCharacterTextSource.VTAB ||
					c == AbstractCharacterTextSource.FF ||
					c == AbstractCharacterTextSource.CR ||
					c == AbstractCharacterTextSource.FILESEP ||
					c == AbstractCharacterTextSource.GROUPSEP ||
					c == AbstractCharacterTextSource.RECORDSEP ||
					c == AbstractCharacterTextSource.UNITSEP ||
					c == AbstractCharacterTextSource.SPACE ||
					c == AbstractCharacterTextSource.NBSP ||
					c == AbstractCharacterTextSource.FIGURESPACE ||
					c == AbstractCharacterTextSource.NNBSP;

			Assert.assertEquals(
					String.format("unexpected result for isWhitespace(0x%04d); ", (int) c),
					expected, classifier_.isWhitespace(c));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.character.CharacterSymbolClassifier#isWordBreak(java.lang.Character)}.
	 */
	@Test
	public void testIsWordBreak_Default()
	{
		do_testIsWordBreak(classifierDefault);
	}

	@Test
	public void testIsWordBreak_WithExtensions()
	{
		do_testIsWordBreak(classifierWithExtensions);
	}

	@Test(expected = IllegalStateException.class)
	public void testIsWordBreak_WithoutExtensions()
	{
		do_testIsWordBreak(classifierWithoutExtensions);
	}

	private static void do_testIsWordBreak(final CharacterSymbolClassifier classifier_)
	{
		Assert.assertTrue(classifier_.isWordBreak(null));

		for (char c = 0; c < Character.MAX_VALUE; c++)
		{
			Assert.assertEquals(
					Character.isSurrogate(c) || !Character.isLetterOrDigit(c),
					classifier_.isWordBreak(c));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.character.CharacterSymbolClassifier#toLowerCase(java.lang.Character)}.
	 */
	@Test
	public void testToLowerCase_Default()
	{
		do_testToLowerCase(classifierDefault);
	}

	@Test
	public void testToLowerCase_WithExtensions()
	{
		do_testToLowerCase(classifierWithExtensions);
	}

	@Test(expected = IllegalStateException.class)
	public void testToLowerCase_WithoutExtensions()
	{
		do_testToLowerCase(classifierWithoutExtensions);
	}

	private static void do_testToLowerCase(final CharacterSymbolClassifier classifier_)
	{
		Assert.assertNull(classifier_.toLowerCase(null));

		for (char c = 0; c < Character.MAX_VALUE; c++)
		{
			final Character expected = Character.isSurrogate(c)
					? c
					: Character.toLowerCase((char) AbstractCharacterTextSource.mapDiacriticalToASCII(c));
			Assert.assertEquals(
					expected,
					classifier_.toLowerCase(c));
		}
	}
}
