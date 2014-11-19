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

import com.dell.mensa.IEdge;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public class Edge<S> implements IEdge<S>
{
	private static final String PARM_state = "state_";

	private final S symbol;
	private final int state;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance for the specified symbol and state values.
	 *
	 * @param symbol_
	 *            the symbol associated with this edge
	 * @param state_
	 *            the state associated with this edge
	 */
	public Edge(final S symbol_, final int state_)
	{
		super();
		Verify.notNegative(state_, PARM_state);
		this.symbol = symbol_;
		this.state = state_;
	}

	// =========================================================================
	// IEdge methods
	// =========================================================================
	/**
	 * @return Returns the symbol associated with this edge.
	 */
	@Override
	public S getSymbol()
	{
		return symbol;
	}

	/**
	 * @return Returns the state associated with this edge.
	 */
	@Override
	public int getState()
	{
		return state;
	}

	// =========================================================================
	// hashCode() and equals()
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		return prime * state + ((symbol == null) ? 0 : symbol.hashCode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof IEdge))
		{
			return false;
		}
		final IEdge<?> other = (IEdge<?>) obj;
		if (state != other.getState())
		{
			return false;
		}
		final Object otherSymbol = other.getSymbol();
		if (symbol == null)
		{
			if (otherSymbol != null)
			{
				return false;
			}
		}
		else if (!symbol.equals(otherSymbol))
		{
			return false;
		}
		return true;
	}

	// =========================================================================
	// toString() -- for debugging
	// =========================================================================
	@Override
	public String toString()
	{
		return String.format("Edge [%s -> %s]", symbol, state);
	}
}
