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

import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IStateMap;

/**
 * {@link Factory} is a concrete, generic implementation of {@link IFactory}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class Factory<S> implements IFactory<S>
{
	/**
	 * @return Returns a new {@link EdgeMap} instance.
	 *
	 * @see com.dell.mensa.IFactory#createEdgeMap()
	 */
	@Override
	public IEdgeMap<S> createEdgeMap()
	{
		return new EdgeMap<>();
	}

	/**
	 * @return Returns a new {@link FailureFunction} instance.
	 *
	 * @see com.dell.mensa.IFactory#createFailureFunction()
	 */
	@Override
	public CompactFailureFunction createFailureFunction()
	{
		// return new FailureFunction();
		return new CompactFailureFunction();
	}

	/**
	 * @return Returns a new {@link GotoFunction} instance.
	 *
	 * @see com.dell.mensa.IFactory#createGotoFunction()
	 */
	@Override
	public GotoFunction<S> createGotoFunction()
	{
		return new GotoFunction<>(this);
	}

	/**
	 * @return Returns a new {@link Keywords} instance.
	 *
	 * @see com.dell.mensa.IFactory#createKeywords()
	 */
	@Override
	public IKeywords<S> createKeywords()
	{
		return new Keywords<>();
	}

	/**
	 * @return Returns a new {@link NextMoveFunction} instance.
	 *
	 * @see com.dell.mensa.IFactory#createNextMoveFunction()
	 */
	@Override
	public NextMoveFunction<S> createNextMoveFunction()
	{
		return new NextMoveFunction<>(this);
	}

	/**
	 * @return Returns a new {@link OutputFunction} instance.
	 *
	 * @see com.dell.mensa.IFactory#createOutputFunction()
	 */
	@Override
	public OutputFunction<S> createOutputFunction()
	{
		return new OutputFunction<>(this);
	}

	/**
	 * @return Returns a new {@link StateMap} instance.
	 *
	 * @see com.dell.mensa.IFactory#createStateMap()
	 */
	@Override
	public IStateMap<S> createStateMap()
	{
		return new StateMap<>();
	}
}
