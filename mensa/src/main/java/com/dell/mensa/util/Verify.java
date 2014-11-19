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
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public final class Verify
{
	private static final String FAILURE_NOT_NULL = "%s may not be null";
	private static final String FAILURE_NOT_EMPTY = "%s may not be empty";
	private static final String FAILURE_NOT_NEGATIVE = "%s may not be negative";
	private static final String FAILURE_IS_POSITIVE = "%s must be positive";
	private static final String FAILURE_CONDITION = "required condition not satisfied: %s";
	private static final String FAILURE_IN_CLOSED_RANGE = "%s (%s) must be in range [%s, %s]";
	private static final String FAILURE_IN_RANGE = "%s (%s) must be in range [%s, %s)";

	private Verify()
	{
		// Do not instantiate
	}

	// =========================================================================
	// Public methods
	// =========================================================================

	public static void condition(final boolean bCondition_, final String msg_)
	{
		if (!bCondition_)
		{
			throw illegalStateException(FAILURE_CONDITION, msg_);
		}
	}

	/**
	 * Verifies that a value is in the specified (closed) interval, {@code [lb_, ub_]}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (inclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inClosedRange(final int value_, final int lb_, final int ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ <= ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_CLOSED_RANGE, value_, lb_, ub_, name_);
		}
	}

	/**
	 * Verifies that a value is in the specified (closed) interval, {@code [lb_, ub_]}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (inclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inClosedRange(final long value_, final long lb_, final long ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ <= ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_CLOSED_RANGE, value_, lb_, ub_, name_);
		}
	}

	/**
	 * Verifies that a value is in the specified (closed) interval, {@code [lb_, ub_]}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (inclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inClosedRange(final double value_, final double lb_, final double ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ <= ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_CLOSED_RANGE, value_, lb_, ub_, name_);
		}
	}

	/**
	 * Verifies that a value is in the specified (right-open) interval, {@code [lb_, ub_)}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (exclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inRange(final int value_, final int lb_, final int ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ < ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_RANGE, value_, lb_, ub_, name_);
		}
	}

	/**
	 * Verifies that a value is in the specified (right-open) interval, {@code [lb_, ub_)}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (exclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inRange(final long value_, final long lb_, final long ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ < ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_RANGE, value_, lb_, ub_, name_);
		}
	}

	/**
	 * Verifies that a value is in the specified (right-open) interval, {@code [lb_, ub_)}.
	 *
	 * @param value_
	 *            the value to be tested
	 * @param lb_
	 *            the lower bound (inclusive)
	 * @param ub_
	 *            the upper bound (exclusive)
	 * @param name_
	 *            the name of the value being tested
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the value is not in the specified range.
	 */
	public static void inRange(final double value_, final double lb_, final double ub_, final String name_)
	{
		if (!(lb_ <= value_ && value_ < ub_))
		{
			throw indexOutOfBoundsException(FAILURE_IN_RANGE, value_, lb_, ub_, name_);
		}
	}

	public static void isPositive(final int n_, final String name_)
	{
		if (n_ <= 0)
		{
			throw illegalArgumentException(FAILURE_IS_POSITIVE, name_);
		}
	}

	public static void isPositive(final long n_, final String name_)
	{
		if (n_ <= 0)
		{
			throw illegalArgumentException(FAILURE_IS_POSITIVE, name_);
		}
	}

	public static void notEmpty(final String s_, final String name_)
	{
		notNull(s_, name_);
		if (s_.length() == 0)
		{
			throw illegalArgumentException(FAILURE_NOT_EMPTY, name_);
		}
	}

	public static void notEmpty(final Object[] a_, final String name_)
	{
		notNull(a_, name_);
		if (a_.length == 0)
		{
			throw illegalArgumentException(FAILURE_NOT_EMPTY, name_);
		}
	}

	public static void notNegative(final int n_, final String name_)
	{
		if (n_ < 0)
		{
			throw illegalArgumentException(FAILURE_NOT_NEGATIVE, name_);
		}
	}

	public static void notNegative(final long n_, final String name_)
	{
		if (n_ < 0)
		{
			throw illegalArgumentException(FAILURE_NOT_NEGATIVE, name_);
		}
	}

	public static void notNull(final Object object_, final String name_)
	{
		if (object_ == null)
		{
			throw illegalArgumentException(FAILURE_NOT_NULL, name_);
		}
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private static IllegalArgumentException illegalArgumentException(final String format_, final String name_)
	{
		final String msg = String.format(format_, name_);
		return new IllegalArgumentException(msg);
	}

	private static IllegalStateException illegalStateException(final String format_, final String msg_)
	{
		final String msg = String.format(format_, msg_);
		return new IllegalStateException(msg);
	}

	private static IndexOutOfBoundsException indexOutOfBoundsException(final String format_, final Object value_, final Object lb_, final Object ub_,
			final String name_)
	{
		final String msg = String.format(format_, name_, value_, lb_, ub_);
		return new IndexOutOfBoundsException(msg);
	}
}
