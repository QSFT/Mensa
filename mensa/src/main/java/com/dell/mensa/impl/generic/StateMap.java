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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IStateMap;
import com.dell.mensa.util.Verify;

/**
 * {@link StateMap} is a generic, concrete {@link IStateMap} implementation backed by a {#link HashMap}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class StateMap<S> implements IStateMap<S>
{
	private static final String PARM_edgeMap = "edgeMap_";

	private final Map<Integer, IEdgeMap<S>> map = new HashMap<>();

	// =========================================================================
	// IStateMap methods
	// =========================================================================
	@Override
	public IEdgeMap<S> get(final int state_)
	{
		return map.get(state_);
	}

	@Override
	public void optimize()
	{
		for (final IEdgeMap<S> edgeMap : map.values())
		{
			edgeMap.optimize();
		}
	}

	@Override
	public IEdgeMap<S> put(final int state_, final IEdgeMap<S> edgeMap_)
	{
		Verify.notNull(edgeMap_, PARM_edgeMap);
		return map.put(state_, edgeMap_);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IStateMap#states()
	 */
	@Override
	public int[] states()
	{
		final int[] aStates = new int[map.size()];
		int i = 0;
		for (final Integer state : map.keySet())
		{
			aStates[i++] = state;
		}
		return aStates;
	}

	@Override
	public Collection<S> symbols()
	{
		final Set<S> set = new HashSet<>();

		for (final IEdgeMap<S> edgeMap : map.values())
		{
			set.addAll(edgeMap.getSymbols());
		}

		return Collections.unmodifiableCollection(set);
	}
}
