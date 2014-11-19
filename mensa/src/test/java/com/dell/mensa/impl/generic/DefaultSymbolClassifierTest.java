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
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.IMatchPrecisionFunction;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class DefaultSymbolClassifierTest
{
	private DefaultSymbolClassifier<Byte> classifier;

	@Before
	public void setUp()
	{
		classifier = new DefaultSymbolClassifier<>();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#getSpace()}.
	 */
	@Test
	public void testGetSpace()
	{
		Assert.assertNull(classifier.getSpace());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#getMatchPrecisionFunction()}.
	 */
	@Test
	public void testGetMatchPrecisionFunction()
	{
		final IMatchPrecisionFunction<Byte> fn = classifier.getMatchPrecisionFunction();

		Assert.assertNotNull(fn);
		Assert.assertTrue(fn instanceof DefaultMatchPrecisionFunction);
		Assert.assertSame(fn, classifier.getMatchPrecisionFunction());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isCaseExtensionEnabled()}.
	 */
	@Test
	public void testIsCaseExtensionEnabled()
	{
		Assert.assertFalse(classifier.isCaseExtensionEnabled());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isPunctuationExtensionEnabled()}.
	 */
	@Test
	public void testIsPunctuationExtensionEnabled()
	{
		Assert.assertFalse(classifier.isPunctuationExtensionEnabled());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isPunctuation(Object)}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testIsPunctuation()
	{
		Assert.assertFalse(classifier.isPunctuation((byte) 0));
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isWhitespace(Object)}.
	 */
	@Test
	public void testIsWhitespace()
	{
		for (int a = 0; a < 256; ++a)
		{
			Assert.assertFalse(classifier.isWhitespace((byte) a));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isWordBreakExtensionEnabled()}.
	 */
	@Test
	public void testIsWordBreakExtensionEnabled()
	{
		Assert.assertFalse(classifier.isWordBreakExtensionEnabled());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#isWordBreak(java.lang.Object)}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testIsWordBreak()
	{
		classifier.isWordBreak((byte) 0);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.DefaultSymbolClassifier#toLowerCase(java.lang.Object)} .
	 */
	@Test(expected = IllegalStateException.class)
	public void testToLowerCase()
	{
		classifier.toLowerCase((byte) 0);
	}
}
