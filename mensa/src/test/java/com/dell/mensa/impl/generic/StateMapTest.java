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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IStateMap;
import com.dell.mensa.impl.character.CharacterFactory;

/**
 * {@link StateMapTest} is a parameterized test framework capable of testing multiple {@link IStateMap} implementations
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
@RunWith(Parameterized.class)
public class StateMapTest
{

	// =========================================================================
	// Fixture
	// =========================================================================
	private final IFactory<Character> factory;

	private static final int NUM_SYMBOLS = 255; // Characters 0, ... NUM_SYMBOL-1, plus null.
	private static final int NUM_STATES = 16 * 1024; // 0, ... NUM_STATES-1
	private static final int EDGES_PER_STATE = 31; // On average, not guaranteed for every state.

	private Random rnd;

	private IStateMap<Character> testMap; // map being tested

	private Map<Integer, Map<Character, Integer>> stateMap1; // Mock maps
	private Map<Integer, Map<Character, Integer>> stateMap2;

	/**
	 * Creates an edge map for a state in a simulated map.
	 *
	 * @param state_
	 *            specifies the state for which the edge map is requested.
	 *
	 * @return Returns the edge map for the specified state.
	 */
	private Map<Character, Integer> mockEdgeMap(final int state_)
	{
		final Map<Character, Integer> map = new HashMap<>();

		map.put(null, randomState());
		for (int i = 0; i < NUM_SYMBOLS; i++)
		{
			if (rnd.nextInt(NUM_SYMBOLS) < EDGES_PER_STATE)
			{
				map.put(Character.valueOf((char) i), randomState());
			}
		}

		return map;
	}

	/**
	 * Create a simulated state map.
	 *
	 * @return Returns a new simulated state map.
	 */
	private Map<Integer, Map<Character, Integer>> mockStateMap()
	{
		final Map<Integer, Map<Character, Integer>> map = new HashMap<>();

		for (int state = 0; state < NUM_STATES; state++)
		{
			map.put(state, mockEdgeMap(state));

		}

		return map;
	}

	private int randomState()
	{
		return rnd.nextInt(NUM_STATES);
	}

	@Before
	public void setUp()
	{
		// Seed the random number generator so we get consistent random numbers
		// for every test.
		rnd = new Random(5);

		testMap = factory.createStateMap();

		stateMap1 = mockStateMap();
		stateMap2 = mockStateMap();

	}

	// =========================================================================
	// Parameters
	// =========================================================================
	public StateMapTest(final IFactory<Character> factory_)
	{
		super();
		this.factory = factory_;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> generateData()
	{
		final Collection<Object[]> data = new ArrayList<>();

		final Object[] factory =
		{ new Factory<Character>() };
		data.add(factory);

		final Object[] characterFactory =
		{ new CharacterFactory() };
		data.add(characterFactory);

		return data;
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link com.dell.mensa.impl.generic.StateMap#get(int)}.
	 */
	@Test
	public void testGet()
	{
		populate(testMap, stateMap1);
		verifyStateMap(stateMap1, testMap);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.StateMap#optimize()}.
	 */
	@Test
	public void testOptimize()
	{
		populate(testMap, stateMap1);
		verifyStateMap(stateMap1, testMap);
		testMap.optimize();
		verifyOptimized(testMap);

		optimizeStateMap(stateMap1);
		verifyStateMap(stateMap1, testMap);

		populate(testMap, stateMap2);
		verifyStateMap(stateMap2, testMap);
		testMap.optimize();
		verifyOptimized(testMap);

		optimizeStateMap(stateMap2);
		verifyStateMap(stateMap2, testMap);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.StateMap#put(int, com.dell.mensa.IEdgeMap)}.
	 */
	@Test
	public void testPut()
	{
		populate(testMap, stateMap1);
		verifyStateMap(stateMap1, testMap);
		populate(testMap, stateMap2);
		verifyStateMap(stateMap2, testMap);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.StateMap#states()}.
	 */
	@Test
	public void testStates()
	{
		populate(testMap, stateMap1);
		final int[] states = testMap.states();
		Assert.assertEquals(NUM_STATES, states.length);

		final Set<Integer> uniqueStates = new HashSet<>();
		for (final int state : states)
		{
			uniqueStates.add(state);
		}

		Assert.assertEquals(NUM_STATES, uniqueStates.size());

		for (int i = 0; i < NUM_STATES; i++)
		{
			Assert.assertTrue(uniqueStates.contains(Integer.valueOf(i)));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.StateMap#symbols()}.
	 */
	@Test
	public void testSymbols()
	{
		populate(testMap, stateMap1);
		final Collection<Character> symbols = testMap.symbols();

		Assert.assertEquals(NUM_SYMBOLS + 1, symbols.size());
		Assert.assertTrue(symbols.contains(null));
		for (int i = 0; i < NUM_SYMBOLS; i++)
		{
			Assert.assertTrue(symbols.contains(Character.valueOf((char) i)));
		}
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	/**
	 * @param edgeMap_
	 */
	private static void optimizeEdgeMap(final Map<Character, Integer> edgeMap_)
	{
		final int defaultState = edgeMap_.get(null);
		if (defaultState != IGotoFunction.NO_STATE)
		{
			final Iterator<Entry<Character, Integer>> iterator = edgeMap_.entrySet().iterator();

			while (iterator.hasNext())
			{
				final Entry<Character, Integer> entry = iterator.next();
				if (entry.getKey() != null && entry.getValue() == defaultState)
				{
					iterator.remove();
				}
			}
		}
	}

	/**
	 * @param stateMap_
	 */
	private static void optimizeStateMap(final Map<Integer, Map<Character, Integer>> stateMap_)
	{
		for (final Map<Character, Integer> edgeMap : stateMap_.values())
		{
			optimizeEdgeMap(edgeMap);
		}
	}

	/**
	 * Populate an actual state map from a mock state map.
	 *
	 * @param actual_
	 *            the actual {@link IStateMap} to populate.
	 * @param mockStateMap_
	 *            the mock state map to copy
	 */
	private void populate(
			final IStateMap<Character> actual_,
			final Map<Integer, Map<Character, Integer>> mockStateMap_)
	{
		for (final Entry<Integer, Map<Character, Integer>> stateMapEntry : mockStateMap_.entrySet())
		{
			final int state = stateMapEntry.getKey();
			final Map<Character, Integer> mockEdgeMap = stateMapEntry.getValue();

			final IEdgeMap<Character> edgeMap = factory.createEdgeMap();
			for (final Entry<Character, Integer> edgeMapEntry : mockEdgeMap.entrySet())
			{
				edgeMap.put(edgeMapEntry.getKey(), edgeMapEntry.getValue());
				final IEdgeMap<Character> expectedPrevious = actual_.get(state);
				final IEdgeMap<Character> previous = actual_.put(state, edgeMap);
				Assert.assertEquals(expectedPrevious, previous);
			}
		}
	}

	/**
	 * Verify the actual state map has the same values as a mock state map.
	 *
	 * @param expected_
	 *            a mock state map
	 * @param actual_
	 *            an actual state map
	 */
	private static void verifyStateMap(final Map<Integer, Map<Character, Integer>> expected_, final IStateMap<Character> actual_)
	{
		Assert.assertEquals(expected_.size(), actual_.states().length);

		final Set<Integer> uniqueStates = new HashSet<>();
		for (final int state : actual_.states())
		{
			uniqueStates.add(state);
		}

		Assert.assertEquals(expected_.size(), uniqueStates.size());
		Assert.assertTrue(expected_.keySet().containsAll(uniqueStates));

		for (final int state : uniqueStates)
		{
			verifyEdgeMap(expected_.get(state), actual_.get(state));
		}
	}

	/**
	 * @param expected_
	 * @param actual_
	 */
	private static void verifyEdgeMap(final Map<Character, Integer> expected_, final IEdgeMap<Character> actual_)
	{
		if (expected_ == null)
		{
			Assert.assertNull(actual_);
			return;
		}

		Assert.assertEquals(
				String.format("Unexpected edge map size:\n expected=%s,\n actual=%s\n", expected_.keySet(), actual_.getSymbols()),
				expected_.keySet().size(), actual_.getSymbols().size());
		Assert.assertTrue(expected_.keySet().containsAll(actual_.getSymbols()));
	}

	/**
	 * Verifies that the edges in a state map are optimized.
	 *
	 * @param stateMap_
	 *            the state map to verify
	 */
	private static void verifyOptimized(final IStateMap<Character> stateMap_)
	{
		for (final int state : stateMap_.states())
		{
			final IEdgeMap<Character> edgeMap = stateMap_.get(state);
			verifyOptimized(edgeMap);
		}
	}

	/**
	 * Verify that an edge map is optimized.
	 *
	 * @param edgeMap_
	 *            the edge map to verify
	 */
	private static void verifyOptimized(final IEdgeMap<Character> edgeMap_)
	{
		final int defaultState = edgeMap_.get(null);
		if (defaultState != IGotoFunction.NO_STATE)
		{
			for (final Character symbol : edgeMap_.getSymbols())
			{
				if (symbol != null && defaultState == edgeMap_.get(symbol))
				{
					Assert.fail(String.format("redundant default transition in edge map: %s -> %d", symbol, defaultState));
				}
			}
		}
	}
}
