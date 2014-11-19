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
package com.dell.mensa.util;

/**
 * A very simple integer counter.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class Counter
{
	// =========================================================================
	// Properties
	// =========================================================================
	private int count;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a counter with the specified initial value.
	 *
	 * @param initialCount_
	 *            the initial count
	 */
	public Counter(final int initialCount_)
	{
		super();
		this.count = initialCount_;
	}

	/**
	 * Constructs a counter with an inial value of zero.
	 */
	public Counter()
	{
		this(0);
	}

	// =========================================================================
	// Public methods
	// =========================================================================
	/**
	 * Gets the counter value.
	 *
	 * @return Returns the integer count.
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Increments the counter by one.
	 *
	 * @return Returns the new counter value.
	 */
	public int increment()
	{
		return ++count;
	}

	/**
	 * Sets the counter value.
	 *
	 * @param count_
	 *            the count value to set.
	 */
	public void setCount(final int count_)
	{
		count = count_;
	}
}