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

import java.util.HashMap;
import java.util.Map;
import com.dell.mensa.IFactory;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IOutputFunction;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class OutputFunction<S> implements IOutputFunction<S>
{
	private static final String PARM_keyword = "keyword_";
	private static final String PARM_state = "state_";

	private final IFactory<S> factory;
	private final Map<Integer, IKeywords<S>> map;

	// =========================================================================
	// Constructors
	// =========================================================================
	public OutputFunction(final IFactory<S> factory_)
	{
		this.factory = factory_;
		this.map = new HashMap<>();
	}

	// =========================================================================
	// IOutputFunction methods
	// =========================================================================
	@Override
	public IKeywords<S> output(final int state_)
	{
		return map.get(state_);
	}

	@Override
	public void put(final int state_, final IKeyword<S> keyword_)
	{
		Verify.notNegative(state_, PARM_state);
		Verify.notNull(keyword_, PARM_keyword);

		IKeywords<S> keywords = map.get(state_);
		if (keywords == null)
		{
			keywords = factory.createKeywords();
			map.put(state_, keywords);
		}

		keywords.add(keyword_);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IOutputFunction#add(com.dell.mensa.int, com.dell.mensa.IKeywords)
	 */
	@Override
	public void put(final int state_, final IKeywords<S> keywords_)
	{
		if (keywords_ != null)
		{
			for (final IKeyword<S> keyword : keywords_)
			{
				put(state_, keyword);
			}
		}
	}
}
