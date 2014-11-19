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
 * {@link IGotoFunction} specifies the <i>goto function</i> interface. A goto function maps a given state and symbol to
 * a new state (via the {@link #eval(int, Object)} method). Such mappings are referred to as <i>state transitions</i> or
 * <i>edges</i> (as they can be visualized as edges connecting states in an {@link IStateMap} graph.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IGotoFunction<S>
{
	/**
	 * The special state value 0 is known as the <i>start state</i>.
	 */
	int START_STATE = 0;

	/**
	 * The special state value used to indicate there is no state.
	 */
	int NO_STATE = -1;

	/**
	 * Resets the call counter.
	 *
	 * @see #getCalls()
	 */
	void clearCalls();

	/**
	 * Maps a given state and symbol to a new state.
	 *
	 * @param state_
	 *            the given state.
	 * @param a_
	 *            the given symbol, which may be {@code null} to define the a default (i.e., error) transition.
	 *
	 * @return Returns a new state or {@link #NO_STATE} if there this {@link IGotoFunction} does not define a transition
	 *         for the given state and symbol.
	 */
	int eval(final int state_, final S a_);

	/**
	 * Optional diagnostic method to return the number of calls to {@link #eval(int, Object)} that have been made on
	 * this instance. An implementation is not required to actually maintain this count at all, or to guarantee
	 * thread-safety if it does maintain a count.
	 *
	 * @return Returns the number of calls to {@link #eval(int, Object)} since this instance was created or since the
	 *         most recent call to {@link #clearCalls()}. If an implementation does not maintain a count, this method
	 *         <i>should</i> return -1.
	 */
	int getCalls();

	/**
	 * Gets the {@link IEdgeMap}, if any, representing the <i>edges</i> originating from a given state.
	 *
	 * @param state_
	 *            the given state
	 *
	 * @return Returns an {@link IEdgeMap} or {@code null} if the specified state has no edges.
	 */
	IEdgeMap<S> getEdgeMap(final int state_);

	/**
	 * Optimizes this instance by removing any explicit edges that are redundant with the default (i.e., "failure")
	 * edges. Implementations should also compact data structures to minimize memory footprint and/or improve runtime
	 * performance.
	 */

	void optimize();

	/**
	 * Adds a new state transition from a given start state to a given next state upon recognizing a given symbol.
	 *
	 * @param state_
	 *            specifies the start state
	 * @param a_
	 *            specifies the symbol
	 * @param nextState_
	 *            specifies the next state
	 *
	 * @throws IllegalArgumentException
	 *             if the specified state is negative.
	 */
	void put(final int state_, final S a_, final int nextState_);

	/**
	 * Returns an unmodifiable collection of symbols, possibly including {@code null}, with edges in this goto function.
	 *
	 * @return Returns an unmodifiable collection of symbols, possibly including {@code null}, with edges in this goto
	 *         function.
	 */
	Collection<S> symbols();
}
