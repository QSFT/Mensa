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

/**
 * {@link IEdge} specifies the interface for an <i>edges</i> in an {@link IEdgeMap}
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 */
public interface IEdge<S>
{
	/**
	 * The symbol associated with this edge.
	 *
	 * @return Returns the symbol associated with this edge.
	 */
	S getSymbol();

	/**
	 * The state associated with this edge.
	 *
	 * @return Returns the state associated with this edge.
	 */
	int getState();

	/**
	 * Implementations must override hashCode to provide value semantics.
	 *
	 * @return Returns a hash code for this instance.
	 */
	@Override
	int hashCode();

	/**
	 * Implementations must override hashCode to provide value semantics.
	 *
	 * @param obj_
	 *            the other object to compare to this instance.
	 *
	 * @return Returns true if the other object is an {@link IEdge} that has identical symbol and state values as this
	 *         instance.
	 */
	@Override
	boolean equals(final Object obj_);
}
