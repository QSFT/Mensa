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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IStateMap;

/**
 * {@link CharacterCompactStateMap} is a generic, concrete {@link IStateMap} implementation backed by an
 * {@link CharacterCompactEdgeMap}[] array rather than a #link HashMap}. This implementation makes use of the knowledge
 * that states are created sequentially, beginning with zero, creating a densely packed state space. So, the state
 * number is used as a direct index into the array, resulting in savings in both runtime performance and resource usage.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterCompactStateMap implements IStateMap<Character>
{
	private CharacterCompactEdgeMap[] map;
	private int maxIndex;

	// =========================================================================
	// Constructors
	// =========================================================================

	public CharacterCompactStateMap(final int initialCapacity_)
	{
		super();
		this.map = new CharacterCompactEdgeMap[initialCapacity_];
		this.maxIndex = -1;
	}

	// =========================================================================
	// IStateMap methods
	// =========================================================================
	@Override
	public CharacterCompactEdgeMap get(final int state_)
	{
		return 0 <= state_ && state_ < map.length ? map[state_] : null;
	}

	@Override
	public void optimize()
	{
		if (map.length > maxIndex + 1)
		{
			final CharacterCompactEdgeMap[] newMap = new CharacterCompactEdgeMap[maxIndex + 1];
			System.arraycopy(map, 0, newMap, 0, maxIndex + 1);
			map = newMap;
		}

		for (int i = 0; i <= maxIndex; i++)
		{
			final CharacterCompactEdgeMap edgeMap = map[i];
			if (edgeMap != null)
			{
				edgeMap.optimize();
			}
		}
	}

	@Override
	public CharacterCompactEdgeMap put(final int state_, final IEdgeMap<Character> edgeMap_)
	{
		assert state_ >= IGotoFunction.START_STATE;

		while (state_ >= map.length)
		{
			expand();
		}

		if (state_ > maxIndex)
		{
			maxIndex = state_;
		}

		final CharacterCompactEdgeMap previous = map[state_];
		map[state_] = (CharacterCompactEdgeMap) edgeMap_;
		return previous;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IStateMap#states()
	 */
	@Override
	public int[] states()
	{
		final int[] aStates = new int[maxIndex + 1];
		for (int i = 0; i <= maxIndex; i++)
		{
			aStates[i] = i;
		}
		return aStates;
	}

	@Override
	public Set<Character> symbols()
	{
		final Set<Character> set = new HashSet<>();

		for (int i = 0; i <= maxIndex; i++)
		{
			final CharacterCompactEdgeMap edgeMap = map[i];
			if (edgeMap != null)
			{
				set.addAll(edgeMap.getSymbols());
			}
		}

		return Collections.unmodifiableSet(set);
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private void expand()
	{
		setCapacity(map.length * 2);
	}

	private void setCapacity(final int capacity_)
	{
		assert capacity_ > 0;

		if (capacity_ != map.length)
		{
			final CharacterCompactEdgeMap[] newMap = new CharacterCompactEdgeMap[capacity_];
			System.arraycopy(map, 0, newMap, 0, maxIndex + 1);
			map = newMap;
		}
	}
}
