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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.dell.mensa.IFailureFunction;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.util.Verify;

/**
 * {@link FailureFunction} is a generic, concrete {@link IFailureFunction} implementation backed by a {#link HashMap}.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class FailureFunction implements IFailureFunction
{
	private static final String PARM_state = "state_";
	private static final String PARM_failureState = "failureState_";

	private final Map<Integer, Integer> map = new HashMap<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IFailureFunction#f(com.dell.mensa.int)
	 */
	@Override
	public int eval(final int state_)
	{
		Verify.notNegative(state_, PARM_state);

		final Integer result = map.get(state_);
		return result == null ? IGotoFunction.NO_STATE : result;
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

		final Integer state = Integer.valueOf(state_);
		final Integer failureState = Integer.valueOf(failureState_);

		final Integer previous = map.put(state, failureState);

		return previous == null ? IGotoFunction.NO_STATE : previous;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.IFailureFunction#getStates()
	 */
	@Override
	public Set<Integer> getStates()
	{
		return Collections.unmodifiableSet(map.keySet());
	}
}
