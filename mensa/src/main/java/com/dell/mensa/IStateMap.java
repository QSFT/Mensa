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
 * {@link IStateMap}, together with {IEdgeMap}, specifies the interface to the graph that defines the state transitions
 * for a finite state machine. States, which are nodes in the graph, are denoted by an integer state number, with the
 * zero representing the <i>start state</i>. Each state (i.e., node in the graph) has an <i>edge map</i> that represents
 * the set of labeled edges representing transitions from the state to a subsequent state.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IStateMap<S>
{
	/**
	 * Gets the edge map for a given state.
	 *
	 * @param state_
	 *            specifies the given state
	 *
	 * @return Returns the edge map for the giving state, or {@code null} if the given state is not part of the graph.
	 */
	IEdgeMap<S> get(int state_);

	/**
	 * Optimizes the state map. This method is called after the state map is fully constructed.
	 *
	 * <p>
	 * An implementation MUST call {@link IEdgeMap#optimize()} on all of the edge maps in the graph to remove redundant
	 * default transitions.
	 * </p>
	 *
	 * <p>
	 * An implementation MAY also use this method to optimize the internal data structures used by the state map. An
	 * implementation is free to decide what sort of optimizations are appropriate; e.g., minimize resource usage,
	 * maximize runtime performance, etc.
	 * </p>
	 */
	void optimize();

	/**
	 * Adds a new edge map for a given state to the graph.
	 *
	 * @param state_
	 *            specifies the state whose edge map is to be added.
	 *
	 * @param edgeMap_
	 *            specifies the edge map to add.
	 *
	 * @return Returns the previous edge map for the given state, or {@code null} if the state was not part of the
	 *         graph.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified edge map is {@code null}.
	 */
	IEdgeMap<S> put(int state_, IEdgeMap<S> edgeMap_);

	/**
	 * Returns an unordered array of states contained in this state map.
	 *
	 * @return Returns an array of states, which may be empty but never {@code null}.
	 */
	int[] states();

	/**
	 * Gets all the unique symbols used to label edges in the graph.
	 *
	 * @return Returns an unmodifiable collection of unique symbols that are used to label edges in the graph.
	 */
	Collection<S> symbols();
}
