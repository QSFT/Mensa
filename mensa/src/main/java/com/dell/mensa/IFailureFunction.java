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
 * {@link IFailureFunction} specifies the <i>failure function</i> interface. A failure function maps a given state to a
 * <i>failure state</i> (via the {@link #eval(int)} method).
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public interface IFailureFunction
{
	/**
	 * Gets the failure state for a given state.
	 *
	 * @param state_
	 *            the state whose failure state is being requested
	 *
	 * @return Returns the failure state for the specified state, or {@code null} if the specified state does not have a
	 *         failure state.
	 */
	int eval(final int state_);

	/**
	 * Get an unmodifiable collection of unique states that have failure states.
	 *
	 * @return Returns an unmodifiable set of states.
	 */
	Collection<Integer> getStates();

	/**
	 * Sets the failure state for a given state.
	 *
	 * @param state_
	 *            specifies the state whose failures state is being set.
	 *
	 * @param failState_
	 *            specifies the failure state.
	 *
	 * @return Returns the previous failure state for the given state, or {@code null} if a failure state had not
	 *         previously been set.
	 */
	int put(int state_, int failState_);
}
