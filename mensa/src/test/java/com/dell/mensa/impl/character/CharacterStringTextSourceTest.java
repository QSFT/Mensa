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

import org.junit.Test;
import com.dell.mensa.ITextSource;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterStringTextSourceTest extends AbstractCharacterTextSourceTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	/**
	 * Creates a new {@link CharacterStringTextSource} instance for testing.
	 *
	 * @param text_
	 *            the input text string.
	 *
	 * @return Returns a new {@link CharacterStringTextSource} instance.
	 */
	@Override
	protected ITextSource<Character> createTextSource(final String text_)
	{
		return new CharacterStringTextSource(text_);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link CharacterStringTextSource#CharacterStringTextSource(String)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testCtor_IllegalArgument()
	{
		createTextSource(null);
	}
}
