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

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
@Ignore("Diagnostic tool; not really a test")
public class CharacterClassificationTest
{
	/**
	 * Prints a simple quick and dirty table of character classifications.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void test()
	{
		for (int i = 0; i < 256; i++)
		{
			final Character c = Character.valueOf((char) i);

			final String msg = String.format("%d) %s isLetterOrDigit=%s",
					i,
					i > 31 && i < 127 || Character.isLetterOrDigit(c) ? "\'" + c + "\'" : "   ",
					Character.isLetterOrDigit(c));
			System.out.println(msg); // NOPMD by <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy
										// Seidl</a> on 9/20/14 9:28 AM
		}
	}
}
