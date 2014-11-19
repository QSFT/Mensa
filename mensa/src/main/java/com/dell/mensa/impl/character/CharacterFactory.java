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

import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IStateMap;
import com.dell.mensa.impl.generic.Factory;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterFactory extends Factory<Character>
{
	private static final int INITIAL_EDGE_MAP_CAPACITY = 4;
	private static final int INITIAL_STATE_MAP_CAPACITY = 128;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.Factory#createEdgeMap()
	 */
	@Override
	public IEdgeMap<Character> createEdgeMap()
	{
		return new CharacterCompactEdgeMap(INITIAL_EDGE_MAP_CAPACITY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.Factory#createStateMap()
	 */
	@Override
	public IStateMap<Character> createStateMap()
	{
		return new CharacterCompactStateMap(INITIAL_STATE_MAP_CAPACITY);
	}
}
