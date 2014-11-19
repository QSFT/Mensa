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
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class EdgeTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	private final static Character[] symbols =
	{ null, 'a', 'b', 'c' };

	private final static int[] states =
	{ 0, 1, 2 };

	private static Edge<Character> createEdge(final Character symbol_, final int state_)
	{
		return new Edge<>(symbol_, state_);
	}

	private static List<Edge<Character>> allEdges()
	{
		final List<Edge<Character>> list = new ArrayList<>(symbols.length * states.length);

		for (final Character symbol : symbols)
		{
			for (final int state : states)
			{
				list.add(createEdge(symbol, state));
			}
		}

		return Collections.unmodifiableList(list);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Edge#Edge(java.lang.Object, int)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_IllegalArgument()
	{
		createEdge('a', -1);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Edge#hashCode()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testHashCode()
	{
		final List<Edge<Character>> edges1 = allEdges();
		final List<Edge<Character>> edges2 = allEdges();

		for (int i = 0; i < edges1.size(); i++)
		{
			final Edge<Character> edge1 = edges1.get(i);
			for (int j = 0; j < edges2.size(); j++)
			{
				final Edge<Character> edge2 = edges2.get(j);

				if (i == j)
				{
					Assert.assertEquals(
							String.format("%s.hashCode() == %s.hashCode(); ", edge1, edge2),
							edge1.hashCode(),
							edge2.hashCode());
				}
			}
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Edge#getSymbol()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetKey()
	{
		for (final Character symbol : symbols)
		{
			for (final int state : states)
			{
				final Edge<Character> edge = createEdge(symbol, state);
				Assert.assertEquals(symbol, edge.getSymbol());
			}
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Edge#getState()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetValue()
	{
		for (final Character symbol : symbols)
		{
			for (final int state : states)
			{
				final Edge<Character> edge = createEdge(symbol, state);
				Assert.assertEquals(state, edge.getState());
			}
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.Edge#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject()
	{
		final List<Edge<Character>> edges1 = allEdges();
		final List<Edge<Character>> edges2 = allEdges();

		for (int i = 0; i < edges1.size(); i++)
		{
			final Edge<Character> edge1 = edges1.get(i);
			for (int j = 0; j < edges2.size(); j++)
			{
				final Edge<Character> edge2 = edges2.get(j);

				Assert.assertEquals(
						String.format("%s.equals(%s); ", edge1, edge2),
						i == j,
						edge1.equals(edge2));

				Assert.assertEquals(
						String.format("%s.equals(%s); ", edge2, edge1),
						i == j,
						edge2.equals(edge1));
			}
		}
	}
}
