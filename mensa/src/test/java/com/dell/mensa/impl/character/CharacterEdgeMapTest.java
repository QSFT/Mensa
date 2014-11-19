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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import com.dell.mensa.IEdge;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.impl.generic.Edge;
import com.dell.mensa.impl.generic.Factory;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
@RunWith(Parameterized.class)
public class CharacterEdgeMapTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	/**
	 * Number of edges used in tests.
	 */
	private static final int NUM_MOCK_EDGES = 100;

	/**
	 * Factory used to create {@link IEdgeMap} implementation being tested.
	 */
	private final transient IFactory<Character> factory;

	/**
	 * {@link IEdgeMap} implementation being tested.
	 */
	private transient IEdgeMap<Character> edgeMap;

	/**
	 * Create a new {@link IEdge} instance.
	 *
	 * @param i_
	 *            specifies the mock edge number, [0, {@link #NUM_MOCK_EDGES}).
	 *
	 * @return Returns a new {@link IEdge} instance.
	 */
	private static IEdge<Character> mockEdge(final int i_)
	{
		assert 0 <= i_ && i_ < NUM_MOCK_EDGES;

		final Character symbol = mockSymbol(i_);
		final int state = mockState(symbol);

		return new Edge<>(symbol, state);
	}

	/**
	 * Create a mock symbol for a mock edge.
	 *
	 * @param i_
	 *            specifies the mock edge number, [0, {@link #NUM_MOCK_EDGES}).
	 *
	 * @return Returns a mock symbol.
	 */
	private static Character mockSymbol(final int i_)
	{
		return i_ == 0 ? null : Character.valueOf((char) (31 + i_));
	}

	/**
	 * Creates a mock state value for a given symbol.
	 *
	 * @param symbol_
	 *            the given symbol; may be {@code null}.
	 *
	 * @return Returns a mock state value.
	 */
	private static int mockState(final Character symbol_)
	{
		return symbol_ == null ? 0 : 17 * (int) symbol_;
	}

	@Before
	public void setUp()
	{
		edgeMap = factory.createEdgeMap();
	}

	// =========================================================================
	// Parameters
	// =========================================================================
	public CharacterEdgeMapTest(final IFactory<Character> factory_)
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
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#getEdges()}.
	 */
	@Test
	public void testGetEdges()
	{
		// Initially, we have no edges.
		Collection<IEdge<Character>> edges = edgeMap.getEdges();
		Assert.assertNotNull(edges);
		Assert.assertTrue(edges.isEmpty());

		// Add the mock edges.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
		}

		// Verify we have the correct number of edges.
		edges = edgeMap.getEdges();
		Assert.assertNotNull(edges);
		Assert.assertEquals(NUM_MOCK_EDGES, edges.size());

		// Verify we have the correct edges.
		final Set<Character> symbols = new HashSet<>();
		for (final IEdge<Character> edge : edges)
		{
			final Character symbol = edge.getSymbol();
			symbols.add(symbol);
			Assert.assertEquals(mockState(symbol), edge.getState());
		}

		// Verify there were no symbols.
		Assert.assertEquals(NUM_MOCK_EDGES, symbols.size());

		// Verify we have the correct symbols.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			Assert.assertTrue(symbols.contains(mockSymbol(i)));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#get(java.lang.Object)}.
	 */
	@Test
	public void testGet()
	{
		// Verify initially there are no results.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			Assert.assertEquals(IGotoFunction.NO_STATE, edgeMap.get(mockSymbol(i)));
		}

		// Add the mock edges.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
		}

		// Verify we can now retrieve each edge.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final Character symbol = mockSymbol(i);
			Assert.assertEquals(mockState(symbol), edgeMap.get(symbol));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#optimize()}.
	 */
	@Test
	public void testOptimize()
	{
		// Add mock edges, but make half of them redundant with the default edge.
		final int defaultState = mockState(mockSymbol(0));
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final Character symbol = mockSymbol(i);
			final int state = i < NUM_MOCK_EDGES / 2 ? defaultState : mockState(symbol);

			edgeMap.put(symbol, state);
		}

		// Verify we have all the edges we added.
		Collection<IEdge<Character>> edges = edgeMap.getEdges();
		Assert.assertNotNull(edges);
		Assert.assertEquals(NUM_MOCK_EDGES, edges.size());

		// Now, optimize to remove redundant edges.
		edgeMap.optimize();

		// Verify we have a reduced number of edges.
		edges = edgeMap.getEdges();
		Assert.assertNotNull(edges);
		Assert.assertEquals(NUM_MOCK_EDGES - NUM_MOCK_EDGES / 2 + 1, edges.size());

		// Verify we have the correct edges.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final Character symbol = mockSymbol(i);
			final int state = i < NUM_MOCK_EDGES / 2 ? defaultState : mockState(symbol);

			Assert.assertEquals(
					i == 0 || i >= NUM_MOCK_EDGES / 2,
					edges.contains(new Edge<>(symbol, state)));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#put(java.lang.Object, int)}.
	 */
	@Test
	public void testPut()
	{
		// The effects of put() are tested in the various other test methods. Here we focus only
		// on verifying the expected return value.

		Assert.assertEquals(0, edgeMap.size());
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			Assert.assertEquals(IGotoFunction.NO_STATE, edgeMap.put(edge.getSymbol(), edge.getState()));
		}

		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			Assert.assertEquals(edge.getState(), edgeMap.put(edge.getSymbol(), edge.getState()));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#size()}.
	 */
	@Test
	public void testSize()
	{
		Assert.assertEquals(0, edgeMap.size());
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
			Assert.assertEquals(i + 1, edgeMap.size());
		}

		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
			Assert.assertEquals(NUM_MOCK_EDGES, edgeMap.size());
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#getStates()}.
	 */
	@Test
	public void testGetStates()
	{
		// Verify initially there are no states.
		Collection<Integer> states = edgeMap.getStates();
		Assert.assertNotNull(states);
		Assert.assertTrue(states.isEmpty());

		// Add the mock edges.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
		}

		// Verify we have the expected number of symbols.
		states = edgeMap.getStates();
		Assert.assertNotNull(states);
		Assert.assertEquals(NUM_MOCK_EDGES, states.size());

		// Verify we have all the correct symbols.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			Assert.assertTrue(states.contains(mockState(mockSymbol(i))));
		}

		// Verify that only unique states are returned.
		final int overloadedState = Integer.MAX_VALUE;
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			if (i < NUM_MOCK_EDGES / 2)
			{
				final IEdge<Character> edge = new Edge<>(mockSymbol(i), overloadedState);
				edgeMap.put(edge.getSymbol(), edge.getState());
			}
		}

		// Verify we have the expected number of symbols.
		states = edgeMap.getStates();
		Assert.assertNotNull(states);
		Assert.assertEquals(NUM_MOCK_EDGES - NUM_MOCK_EDGES / 2 + 1, states.size());

		// Verify that only unique states are returned.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			if (i < NUM_MOCK_EDGES / 2)
			{
				Assert.assertTrue(states.contains(overloadedState));
			}
			else
			{
				Assert.assertTrue(states.contains(mockState(mockSymbol(i))));
			}
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.EdgeMap#getSymbols()}.
	 */
	@Test
	public void testGetSymbols()
	{
		// Verify initially there are no symbols.
		Collection<Character> symbols = edgeMap.getSymbols();
		Assert.assertNotNull(symbols);
		Assert.assertTrue(symbols.isEmpty());

		// Add the mock edges.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			final IEdge<Character> edge = mockEdge(i);
			edgeMap.put(edge.getSymbol(), edge.getState());
		}

		// Verify we have the expected number of symbols.
		symbols = edgeMap.getSymbols();
		Assert.assertNotNull(symbols);
		Assert.assertEquals(NUM_MOCK_EDGES, symbols.size());

		// Verify we have all the correct symbols.
		for (int i = 0; i < NUM_MOCK_EDGES; i++)
		{
			Assert.assertTrue(symbols.contains(mockSymbol(i)));
		}
	}
}
