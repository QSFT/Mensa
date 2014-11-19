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

import com.dell.mensa.IFactory;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.INextMoveFunction;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class NextMoveFunction<S> extends GotoFunction<S> implements INextMoveFunction<S>
{
	private static final String MSG_NULL_RESULT = "unexpected null result for next move function; state=%s, symbol=%s";

	public NextMoveFunction(final IFactory<S> factory_)
	{
		super(factory_);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.GotoFunction#eval(com.dell.mensa.int, java.lang.Object)
	 */
	@Override
	public int eval(final int state_, final S a_)
	{
		final int result = super.eval(state_, a_);
		if (result == IGotoFunction.NO_STATE)
		{
			final String msg = String.format(MSG_NULL_RESULT, state_, a_);
			throw new IllegalStateException(msg);
		}
		return result;
	}
}
