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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.dell.mensa.IEdge;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class EdgeMap<S> implements IEdgeMap<S>
{
	private static final String PARM_state = "state_";

	// =========================================================================
	// Properties
	// =========================================================================
	private final Map<S, Integer> map;

	// =========================================================================
	// Constructors
	// =========================================================================
	public EdgeMap()
	{
		this.map = new HashMap<>();
	}

	/**
	 * @param initialCapacity_
	 *            the initial capacity of the backing {@link HashMap}.
	 */
	public EdgeMap(final int initialCapacity_)
	{
		this.map = new HashMap<>(initialCapacity_);
	}

	/**
	 * @param initialCapacity_
	 *            the initial capacity of the backing {@link HashMap}.
	 * @param loadFactor_
	 *            the load factor of the backing {@link HashMap}.
	 */
	public EdgeMap(final int initialCapacity_, final float loadFactor_)
	{
		this.map = new HashMap<>(initialCapacity_, loadFactor_);
	}

	// =========================================================================
	// IEdgeMap methods
	// =========================================================================
	@Override
	public Collection<IEdge<S>> getEdges()
	{
		final List<IEdge<S>> set = new ArrayList<>(map.size());
		for (final Entry<S, Integer> entry : map.entrySet())
		{
			set.add(createEdge(entry.getKey(), entry.getValue()));
		}

		return Collections.unmodifiableCollection(set);
	}

	@Override
	public int get(final S a_)
	{
		return xformNull(map.get(a_));
	}

	@Override
	public void optimize()
	{
		final int failState = map.get(null);
		if (failState != IGotoFunction.NO_STATE)
		{
			final Iterator<Entry<S, Integer>> iterator = map.entrySet().iterator();
			while (iterator.hasNext())
			{
				final Entry<S, Integer> entry = iterator.next();
				if (entry.getKey() != null && failState == entry.getValue())
				{
					iterator.remove();
				}
			}
		}
	}

	@Override
	public int put(final S a_, final int state_)
	{
		Verify.notNegative(state_, PARM_state);

		return xformNull(map.put(a_, state_));
	}

	@Override
	public int size()
	{
		return map.size();
	}

	@Override
	public Set<Integer> getStates()
	{
		return Collections.unmodifiableSet(new HashSet<>(map.values()));
	}

	@Override
	public Set<S> getSymbols()
	{
		return Collections.unmodifiableSet(map.keySet());
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private IEdge<S> createEdge(final S symbol_, final Integer state_)
	{
		return new Edge<>(symbol_, state_);
	}

	private static final int xformNull(final Integer state_)
	{
		return state_ == null ? IGotoFunction.NO_STATE : state_;
	}
}
