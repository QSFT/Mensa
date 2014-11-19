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
package com.dell.mensa.impl.generic;

import org.junit.Assert;
import org.junit.Test;
import com.dell.mensa.IKeyword;
import com.dell.mensa.impl.character.CharacterKeyword;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class KeywordAndCharacterKeywordTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private static final boolean[] booleans =
	{ false, true };

	private static final Object[] userDatas =
	{ null, "my data" };

	private static final Character[] HE =
	{
			'h', 'e'
	};

	private static final Character[] SHE =
	{
			's', 'h', 'e'
	};

	private static final String STR_HE = "he";
	private static final String STR_SHE = "she";

	private static Keyword<Character>
			he = new Keyword<>(HE),
			she = new Keyword<>(SHE),

			he_c = new Keyword<>(HE, null, AbstractKeyword.CASE_SENSITIVE),
			she_c = new Keyword<>(SHE, null, AbstractKeyword.CASE_SENSITIVE),

			he_p = new Keyword<>(HE, null, AbstractKeyword.PUNCTUATION_SENSITIVE),
			she_p = new Keyword<>(SHE, null, AbstractKeyword.PUNCTUATION_SENSITIVE),

			he_pc = new Keyword<>(HE, null, AbstractKeyword.CASE_SENSITIVE | AbstractKeyword.PUNCTUATION_SENSITIVE),
			she_pc = new Keyword<>(SHE, null, AbstractKeyword.CASE_SENSITIVE | AbstractKeyword.PUNCTUATION_SENSITIVE)
			;

	private static CharacterKeyword
			cHe = new CharacterKeyword(STR_HE),
			cShe = new CharacterKeyword(STR_SHE),

			cHe_c = new CharacterKeyword(STR_HE, null, AbstractKeyword.CASE_SENSITIVE),
			cShe_c = new CharacterKeyword(STR_SHE, null, AbstractKeyword.CASE_SENSITIVE),

			cHe_p = new CharacterKeyword(STR_HE, null, AbstractKeyword.PUNCTUATION_SENSITIVE),
			cShe_p = new CharacterKeyword(STR_SHE, null, AbstractKeyword.PUNCTUATION_SENSITIVE),

			cHe_pc = new CharacterKeyword(STR_HE, null, AbstractKeyword.CASE_SENSITIVE | AbstractKeyword.PUNCTUATION_SENSITIVE),
			cShe_pc = new CharacterKeyword(STR_SHE, null, AbstractKeyword.CASE_SENSITIVE | AbstractKeyword.PUNCTUATION_SENSITIVE)
			;

	private static Object[] keywords =
	{
			// i&1 => same symbols
			// i&2 => same case-sensitivity
			// i&4 => same punctuation-sensitivity

			he, she, he_c, she_c, he_p, she_p, he_pc, she_pc,
			cHe, cShe, cHe_c, cShe_c, cHe_p, cShe_p, cHe_pc, cShe_pc,
	};

	// =========================================================================
	// Test methods
	// =========================================================================

	@SuppressWarnings("static-method")
	@Test
	public void testCtor_3Arg()
	{
		for (final Object userData : userDatas)
		{
			for (final boolean bCaseSensitive : booleans)
			{
				for (final boolean bPunctuationSensitive : booleans)
				{
					int flags = 0;
					if (bCaseSensitive)
					{
						flags |= AbstractKeyword.CASE_SENSITIVE;
					}
					if (bPunctuationSensitive)
					{
						flags |= AbstractKeyword.PUNCTUATION_SENSITIVE;
					}

					final IKeyword<Character> k = new Keyword<>(HE, userData, flags);
					final IKeyword<Character> ck = new CharacterKeyword(STR_HE, userData, flags);

					Assert.assertEquals(userData, k.getUserData());
					Assert.assertEquals(userData, ck.getUserData());

					Assert.assertEquals(bCaseSensitive, k.isCaseSensitive());
					Assert.assertEquals(bCaseSensitive, ck.isCaseSensitive());

					Assert.assertEquals(bPunctuationSensitive, k.isPunctuationSensitive());
					Assert.assertEquals(bPunctuationSensitive, ck.isPunctuationSensitive());

					Assert.assertEquals(2, k.length());
					Assert.assertEquals(2, ck.length());
				}
			}
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testCtor_2Arg()
	{
		for (final Object userData : userDatas)
		{
			final IKeyword<Character> k = new Keyword<>(HE, userData);
			final IKeyword<Character> ck = new CharacterKeyword(STR_HE, userData);

			Assert.assertEquals(userData, k.getUserData());
			Assert.assertEquals(userData, ck.getUserData());

			Assert.assertFalse(k.isCaseSensitive());
			Assert.assertFalse(ck.isCaseSensitive());

			Assert.assertFalse(k.isPunctuationSensitive());
			Assert.assertFalse(ck.isPunctuationSensitive());

			Assert.assertEquals(2, k.length());
			Assert.assertEquals(2, ck.length());
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testCtor_1Arg()
	{
		final IKeyword<Character> k = new Keyword<>(HE);
		final IKeyword<Character> ck = new CharacterKeyword(STR_HE);

		Assert.assertNull(k.getUserData());
		Assert.assertNull(ck.getUserData());

		Assert.assertFalse(k.isCaseSensitive());
		Assert.assertFalse(ck.isCaseSensitive());

		Assert.assertFalse(k.isPunctuationSensitive());
		Assert.assertFalse(ck.isPunctuationSensitive());

		Assert.assertEquals(2, k.length());
		Assert.assertEquals(2, ck.length());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Keyword#Keyword(Object[])}.
	 */
	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testCtor_NullSymbols()
	{
		new Keyword<>((Character[]) null);
	}

	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_EmptySymbols()
	{
		final Integer symbols[] = {};
		new Keyword<>(symbols);
	}

	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testCCtor_NullSymbols()
	{
		new CharacterKeyword(null);
	}

	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	public void testCCtor_EmptySymbols()
	{
		new CharacterKeyword("");
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Keyword#length()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testLength()
	{
		Assert.assertEquals(2, he.length());
		Assert.assertEquals(3, she.length());

		Assert.assertEquals(2, cHe.length());
		Assert.assertEquals(3, cShe.length());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Keyword#symbolAt(int)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testSymbolAt()
	{
		Assert.assertEquals(Character.valueOf('s'), she.symbolAt(0));
		Assert.assertEquals(Character.valueOf('h'), she.symbolAt(1));
		Assert.assertEquals(Character.valueOf('e'), she.symbolAt(2));

		Assert.assertEquals(Character.valueOf('s'), cShe.symbolAt(0));
		Assert.assertEquals(Character.valueOf('h'), cShe.symbolAt(1));
		Assert.assertEquals(Character.valueOf('e'), cShe.symbolAt(2));
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSymbolAt_Underflow()
	{
		she.symbolAt(-1);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSymbolAt_Overflow()
	{
		she.symbolAt(3);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testCSymbolAt_Underflow()
	{
		cShe.symbolAt(-1);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testCSymbolAt_Overflow()
	{
		cShe.symbolAt(3);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Keyword#hashCode()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testHashCode()
	{
		for (int i = 0; i < keywords.length; ++i)
		{
			for (int j = 0; j < keywords.length; ++j)
			{
				// If keywords have the same symbols, the same case-sensitivity, and same-punctuation sensitivity,
				// then they must have the same hash code.
				if ((i & 7) == (j & 7))
				{
					Assert.assertEquals(
							String.format("unexpected result for %s.hashCode() equals %s.hashCode(); ", keywords[i], keywords[j]),
							keywords[i].hashCode(),
							keywords[j].hashCode());
				}
			}
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Keyword#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject()
	{
		for (int i = 0; i < keywords.length; ++i)
		{
			for (int j = 0; j < keywords.length; ++j)
			{
				// Keywords with the same symbols, case sensitivity, and punctuation-sensitivity must be
				// equal. Otherwise, they must be not equal.
				Assert.assertEquals(
						String.format("unexpected result for %s equals %s; ", keywords[i], keywords[j]),
						(i & 7) == (j & 7),
						keywords[i].equals(keywords[j]));
			}
		}
	}
}
