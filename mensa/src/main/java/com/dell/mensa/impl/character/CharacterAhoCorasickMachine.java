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

import com.dell.mensa.IFactory;
import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.impl.generic.AhoCorasickMachine;

/**
 * {@link CharacterAhoCorasickMachine} specializes {@link AhoCorasickMachine} for matching {@link Character} symbols.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterAhoCorasickMachine extends AhoCorasickMachine<Character>
{
	/**
	 * Constructs a new instance with the specified factory and classifier instances.
	 *
	 * @param factory_
	 *            the factor instance used by this machine
	 * @param classifier_
	 *            the classifier instance used by this machine
	 */
	public CharacterAhoCorasickMachine(final IFactory<Character> factory_, final ISymbolClassifier<Character> classifier_)
	{
		super(factory_, classifier_);
	}

	/**
	 * Constructs a new instance using a {@link CharacterFactory} factory and a {@link CharacterSymbolClassifier} symbol
	 * classifier.
	 *
	 * @param bExtensionsEnabled_
	 *            specifies whether all extensions are enabled ({@code true}) or disabled {@code false}); see
	 *            {@link CharacterSymbolClassifier#CharacterSymbolClassifier(boolean)}.
	 */
	public CharacterAhoCorasickMachine(final boolean bExtensionsEnabled_)
	{
		this(new CharacterFactory(), new CharacterSymbolClassifier(bExtensionsEnabled_));
	}

	/**
	 * Constructs a new instance as if calling {@link #CharacterAhoCorasickMachine(boolean)} with {@code true} as the
	 * argument value.
	 */
	public CharacterAhoCorasickMachine()
	{
		this(true);
	}
}
