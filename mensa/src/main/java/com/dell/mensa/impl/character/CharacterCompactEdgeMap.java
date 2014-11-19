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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.dell.mensa.IEdge;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.impl.generic.Edge;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterCompactEdgeMap implements IEdgeMap<Character>
{
	private static final char NULL_SYMBOL = (char) -1;

	private static final String PARM_state = "state_";

	private char[] symbols;
	private int[] states;
	private int n;
	private boolean bOptimized;

	// =========================================================================
	// Constructors
	// =========================================================================
	public CharacterCompactEdgeMap(final int initialCapacity_)
	{
		this.symbols = new char[initialCapacity_];
		this.states = new int[initialCapacity_];
		this.n = 0;
		this.bOptimized = false;
	}

	// =========================================================================
	// IEdgeMap methods
	// =========================================================================
	@Override
	public Collection<IEdge<Character>> getEdges()
	{
		final List<IEdge<Character>> edges = new ArrayList<>(n);

		for (int i = 0; i < n; i++)
		{
			edges.add(createEdge(symbols[i], states[i]));
		}

		return Collections.unmodifiableCollection(edges);
	}

	@Override
	public int get(final Character a_)
	{
		final int index = indexOf(a_);
		return index == -1 ? IGotoFunction.NO_STATE : states[index];
	}

	@Override
	public void optimize()
	{
		final int failState = get(null);
		if (failState != IGotoFunction.NO_STATE)
		{
			for (int i = n - 1; i >= 0; i--)
			{
				if (symbols[i] != NULL_SYMBOL && failState == states[i])
				{
					// remove entry i
					//
					final int k = n - i - 1; // number of entries to shift left
					if (k > 0)
					{
						System.arraycopy(symbols, i + 1, symbols, i, k);
						System.arraycopy(states, i + 1, states, i, k);
					}

					n--;
				}
			}
		}

		setCapacity(n);
		quickSort(0, n - 1);
		bOptimized = true;
	}

	@Override
	public int put(final Character a_, final int state_)
	{
		Verify.notNegative(state_, PARM_state);

		bOptimized = false;

		final int index = indexOf(a_);
		if (index == -1)
		{
			if (symbols.length == n)
			{
				expand();
			}
			symbols[n] = a_ == null ? NULL_SYMBOL : a_;
			states[n] = state_;
			n++;
			return IGotoFunction.NO_STATE;
		}

		final int previous = states[index];
		states[index] = state_;
		return previous;
	}

	@Override
	public int size()
	{
		return n;
	}

	@Override
	public Collection<Integer> getStates()
	{
		// Use a set to ensure only unique states are included.
		final Set<Integer> set = new HashSet<>(n);
		for (int i = 0; i < n; i++)
		{
			set.add(states[i]);
		}

		return Collections.unmodifiableCollection(set);
	}

	@Override
	public Collection<Character> getSymbols()
	{
		// Use an array because we already know the symbols are unique.
		final List<Character> set = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
		{
			final char key = symbols[i];
			set.add(key == NULL_SYMBOL ? null : Character.valueOf(key));
		}

		return Collections.unmodifiableCollection(set);
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private static IEdge<Character> createEdge(final char symbol_, final int state_)
	{
		return new Edge<>(symbol_ == NULL_SYMBOL ? null : Character.valueOf(symbol_), state_);
	}

	private void expand()
	{
		setCapacity(symbols.length * 2);
	}

	private int indexOf(final Character key_)
	{
		final char key = key_ == null ? NULL_SYMBOL : key_.charValue();

		if (bOptimized)
		{
			for (int i = 0; i < n; i++)
			{
				final char symbol = symbols[i];
				if (key == symbol)
				{
					return i;
				}

				// When optimized, the symbols are sorted. Short-circuit the search if we've
				// already passed the value we're looking for.
				if (key < symbol)
				{
					break;
				}
			}
		}
		else
		{
			for (int i = 0; i < n; i++)
			{
				if (key == symbols[i])
				{
					return i;
				}
			}
		}

		return -1;
	}

	private void setCapacity(final int capacity_)
	{
		if (capacity_ != symbols.length)
		{
			final char[] newKeys = new char[capacity_];
			System.arraycopy(symbols, 0, newKeys, 0, n);
			symbols = newKeys;

			final int[] newValues = new int[capacity_];
			System.arraycopy(states, 0, newValues, 0, n);
			states = newValues;
		}
	}

	// =========================================================================
	// Internal methods: quick sort
	// =========================================================================
	/**
	 * Implements a quick sort on the both the {@link #symbols} and {@link #states} arrays, using the symbols as the
	 * sort keys.
	 *
	 * @param p
	 * @param r
	 *
	 * @see <a href="http://codereview.stackexchange.com/questions/4022/java-implementation-of-quick-sort">Java
	 *      Implementation of Quick Sort</a>
	 */
	private void quickSort(final int p, final int r)
	{
		if (p < r)
		{
			final int q = partition(p, r);
			quickSort(p, q);
			quickSort(q + 1, r);
		}
	}

	private int partition(final int p, final int r)
	{

		final char symbol = symbols[p];
		int i = p - 1;
		int j = r + 1;

		while (true)
		{
			++i;
			while (i < r && symbols[i] < symbol)
			{
				++i;
			}
			--j;
			while (j > p && symbols[j] > symbol)
			{
				--j;
			}

			if (i < j)
			{
				swap(i, j);
			}
			else
			{
				return j;
			}
		}
	}

	private void swap(final int i, final int j)
	{
		final char symbol = symbols[i];
		symbols[i] = symbols[j];
		symbols[j] = symbol;

		final int state = states[i];
		states[i] = states[j];
		states[j] = state;
	}
}
