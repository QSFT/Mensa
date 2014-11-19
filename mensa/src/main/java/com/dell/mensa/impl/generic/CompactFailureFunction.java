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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.dell.mensa.IFailureFunction;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.util.Verify;

/**
 * {@link CompactFailureFunction} is a generic, concrete {@link IFailureFunction} implementation backed by an
 * {@code int[]} array.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CompactFailureFunction implements IFailureFunction
{
	private static final String PARM_state = "state_";
	private static final String PARM_failureState = "failureState_";

	private static final int INITIAL_CAPACITY = 128;

	// =========================================================================
	// Properties
	// =========================================================================
	private int[] map;
	private int maxIndex;

	// =========================================================================
	// Constructors
	// =========================================================================

	public CompactFailureFunction(final int initialCapacity_)
	{
		super();
		this.map = allocArray(initialCapacity_);
		this.maxIndex = -1;
	}

	public CompactFailureFunction()
	{
		this(INITIAL_CAPACITY);
	}

	// =========================================================================
	// IFailureFunction methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IFailureFunction#f(com.dell.mensa.int)
	 */
	@Override
	public int eval(final int state_)
	{
		Verify.notNegative(state_, PARM_state);
		return state_ < map.length ? map[state_] : IGotoFunction.NO_STATE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IFailureFunction#add(com.dell.mensa.int, com.dell.mensa.int)
	 */
	@Override
	public int put(final int state_, final int failureState_)
	{
		Verify.notNegative(state_, PARM_state);
		Verify.notNegative(failureState_, PARM_failureState);

		while (state_ >= map.length)
		{
			expand();
		}

		if (state_ > maxIndex)
		{
			maxIndex = state_;
		}

		final int previous = map[state_];
		map[state_] = failureState_;
		return previous;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IFailureFunction#getStates()
	 */
	@Override
	public Set<Integer> getStates()
	{
		final Set<Integer> set = new HashSet<>();

		for (int i = 0; i <= maxIndex; i++)
		{
			if (map[i] != IGotoFunction.NO_STATE)
			{
				set.add(i);
			}
		}

		return Collections.unmodifiableSet(set);
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	/**
	 * @param capacity_
	 * @return Returns a newly allocated array, filled with {@link IGotoFunction#NO_STATE} values.
	 */
	private static int[] allocArray(final int capacity_)
	{
		assert capacity_ >= 0;

		final int[] array = new int[capacity_];
		Arrays.fill(array, IGotoFunction.NO_STATE);
		return array;
	}

	private void expand()
	{
		setCapacity(map.length * 2);
	}

	private void setCapacity(final int capacity_)
	{
		assert capacity_ > 0;

		if (capacity_ != map.length)
		{
			final int[] newMap = new int[capacity_];
			System.arraycopy(map, 0, newMap, 0, maxIndex + 1);
			map = newMap;
		}
	}

}
