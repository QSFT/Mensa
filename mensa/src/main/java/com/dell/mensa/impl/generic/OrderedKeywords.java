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

import java.util.LinkedHashSet;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class OrderedKeywords<S> extends LinkedHashSet<IKeyword<S>> implements IKeywords<S>
{
	private static final long serialVersionUID = 1L;

	// =========================================================================
	// Constructors
	// =========================================================================

	public OrderedKeywords()
	{
		super();
	}

	/**
	 * @param initialCapacity_
	 *            the initial capacity of the backing {@link LinkedHashSet}.
	 */
	public OrderedKeywords(final int initialCapacity_)
	{
		super(initialCapacity_);
	}

	/**
	 * @param initialCapacity_
	 *            the initial capacity of the backing {@link LinkedHashSet}.
	 * @param loadFactor_
	 *            the load factor of the backing {@link LinkedHashSet}.
	 */
	public OrderedKeywords(final int initialCapacity_, final float loadFactor_)
	{
		super(initialCapacity_, loadFactor_);
	}

	/**
	 * @param keywords_
	 *            the keywords to place in this set
	 */
	public OrderedKeywords(final IKeywords<S> keywords_)
	{
		super(keywords_);
	}
}
