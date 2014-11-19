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

import java.util.Collection;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IStateMap;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public class GotoFunction<S> implements IGotoFunction<S>
{
	private static final String PARM_factory = "factory_";
	private final IFactory<S> factory;
	private final IStateMap<S> map;

	private int calls;

	// =========================================================================
	// Constructors
	// =========================================================================
	public GotoFunction(final IFactory<S> factory_)
	{
		Verify.notNull(factory_, PARM_factory);

		this.factory = factory_;
		this.map = factory_.createStateMap();
	}

	// =========================================================================
	// IGotoFunction methods
	// =========================================================================
	@Override
	public void clearCalls()
	{
		calls = 0;
	}

	@Override
	public int eval(final int state_, final S a_)
	{
		++calls;
		final IEdgeMap<S> edgeMap = getEdgeMap(state_);
		if (edgeMap == null)
		{
			return IGotoFunction.NO_STATE;
		}

		final int state = edgeMap.get(a_);
		return state == IGotoFunction.NO_STATE ? edgeMap.get(null) : state;
	}

	@Override
	public int getCalls()
	{
		return calls;
	}

	@Override
	public IEdgeMap<S> getEdgeMap(final int state_)
	{
		return map.get(state_);
	}

	@Override
	public void optimize()
	{
		map.optimize();
	}

	@Override
	public void put(final int state_, final S a_, final int nextState_)
	{
		IEdgeMap<S> edgeMap = getEdgeMap(state_);
		if (edgeMap == null)
		{
			edgeMap = factory.createEdgeMap();
			map.put(state_, edgeMap);
		}

		edgeMap.put(a_, nextState_);
	}

	@Override
	public Collection<S> symbols()
	{
		return map.symbols();
	}
}
