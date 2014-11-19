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
package com.dell.mensa;

import java.util.Collection;

/**
 * {@link IEdgeMap} is the interface to a collection of <i>edges</i> representing state transition from some state
 * <i>s</i> to state <i>s<sub>a</sub></i> upon recognizing the symbol <i>a</i>. A {@code null} symbol is used to
 * represent the default (i.e., "fail") state transition.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IEdgeMap<S>
{
	/**
	 * Gets all edges in this map.
	 *
	 * @return Returns an unmodifiable collection edges.
	 */
	Collection<IEdge<S>> getEdges();

	/**
	 * Gets the state corresponding to the specified symbol.
	 *
	 * @param a_
	 *            the symbol identifying a specific edge.
	 *
	 * @return Returns the state corresponding to the specified symbol, or {@link IGotoFunction#NO_STATE} if this map
	 *         does not define transition for the specified symbol.
	 */
	int get(final S a_);

	/**
	 * Optimizes this instance by removing any explicit edges that are redundant with the default (i.e., "failure")
	 * edge. Implementations should also compact data structures to minimize memory footprint and/or improve runtime
	 * performance.
	 */
	void optimize();

	/**
	 * Adds an edge to this map.
	 *
	 * @param a_
	 *            the symbol that identifies the edge, which may be {@code null} to define the a default (i.e., error)
	 *            transition.
	 * @param state_
	 *            the state to which the edge connects
	 *
	 * @return Returns the state to which the specified symbol previously connected, or {@code null} if this map did not
	 *         already contain an edge for the specified symbol.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified state is negative.
	 */
	int put(final S a_, final int state_);

	/**
	 * Returns the number of edges in this map.
	 *
	 * @return Returns the number of edges in this map.
	 */
	int size();

	/**
	 * Returns an unmodifiable collection of unique states reachable by the edges in this map.
	 *
	 * @return Returns an unmodifiable set of states reachable by the edges in this map.
	 */
	Collection<Integer> getStates();

	/**
	 * Returns an unmodifiable collection of symbols, possibly including {@code null}, with edges in this map.
	 *
	 * @return Returns an unmodifiable collection of symbols, possibly including {@code null}, with edges in this map.
	 */
	Collection<S> getSymbols();
}
