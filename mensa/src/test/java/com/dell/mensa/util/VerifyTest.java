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

import org.junit.Test;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class VerifyTest
{
	private static final String PARM_value = "value";

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#condition(boolean, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCondition_True()
	{
		Verify.condition(true, "true");
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalStateException.class)
	public void testCondition_False()
	{
		Verify.condition(false, "false");
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inClosedRange(int, int, int, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Int_Underflow()
	{
		final int lb = 0;
		final int ub = 100;

		Verify.inClosedRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInClosedRange_Int()
	{
		final int lb = 0;
		final int ub = 100;

		for (int value = lb; value <= ub; ++value)
		{
			Verify.inClosedRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Int_Overflow()
	{
		final int lb = 0;
		final int ub = 100;

		Verify.inClosedRange(ub + 1, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inClosedRange(long, long, long, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Long_Underflow()
	{
		final long lb = 0;
		final long ub = 100;

		Verify.inClosedRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInClosedRange_Long()
	{
		final long lb = 0;
		final long ub = 100;

		for (long value = lb; value <= ub; ++value)
		{
			Verify.inClosedRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Long_Overflow()
	{
		final long lb = 0;
		final long ub = 100;

		Verify.inClosedRange(ub + 1, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inClosedRange(double, double, double, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Double_Underflow()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		Verify.inClosedRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInClosedRange_Double()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		for (double value = lb; value <= ub; ++value)
		{
			Verify.inClosedRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInClosedRange_Double_Overflow()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		Verify.inClosedRange(ub + 1, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inRange(int, int, int, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Int_Underflow()
	{
		final int lb = 0;
		final int ub = 100;

		Verify.inRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInRange_Int()
	{
		final int lb = 0;
		final int ub = 100;

		for (int value = lb; value < ub; ++value)
		{
			Verify.inRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Int_Overflow()
	{
		final int lb = 0;
		final int ub = 100;

		Verify.inRange(ub, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inRange(long, long, long, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Long_Underflow()
	{
		final long lb = 0;
		final long ub = 100;

		Verify.inRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInRange_Long()
	{
		final long lb = 0;
		final long ub = 100;

		for (long value = lb; value < ub; ++value)
		{
			Verify.inRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Long_Overflow()
	{
		final long lb = 0;
		final long ub = 100;

		Verify.inRange(ub, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#inRange(double, double, double, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Double_Underflow()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		Verify.inRange(lb - 1, lb, ub, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testInRange_Double()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		for (double value = lb; value < ub; ++value)
		{
			Verify.inRange(value, lb, ub, PARM_value);
		}
	}

	@SuppressWarnings("static-method")
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInRange_Double_Overflow()
	{
		final double lb = 0.0;
		final double ub = 100.0;

		Verify.inRange(ub, lb, ub, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#isPositive(int, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testIsPositive_Int_Negative()
	{
		final int value = -1;
		Verify.isPositive(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testIsPositive_Int_Zero()
	{
		final int value = 0;
		Verify.isPositive(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsPositive_Int_Positive()
	{
		final int value = 1;
		Verify.isPositive(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#isPositive(long, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testIsPositive_Long_Negative()
	{
		final long value = -1;
		Verify.isPositive(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testIsPositive_Long_Zero()
	{
		final long value = 0;
		Verify.isPositive(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testIsPositive_Long_Positive()
	{
		final long value = 1;
		Verify.isPositive(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#notEmpty(java.lang.String, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testNotEmptyString_Null()
	{
		final String value = null;
		Verify.notEmpty(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyString_Empty()
	{
		final String value = "";
		Verify.notEmpty(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotEmptyString()
	{
		final String value = "x";
		Verify.notEmpty(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#notEmpty(java.lang.Object[], java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
			value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
			justification = "Testing null argument")
	public void testNotEmptyObjectArray_Null()
	{
		final Object[] value = null;
		Verify.notEmpty(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testNotEmptyObjectArray_Empty()
	{
		final Object[] value = {};
		Verify.notEmpty(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotEmptyObjectArray()
	{
		final Object[] value =
		{ "x" };
		Verify.notEmpty(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#notNegative(int, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testNotNegative_Int_Negative()
	{
		final int value = -1;
		Verify.notNegative(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotNegative_Int_Zero()
	{
		final int value = 0;
		Verify.notNegative(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotNegative_Int_Positive()
	{
		final int value = 1;
		Verify.notNegative(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#notNegative(long, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testNotNegative_Long_Negative()
	{
		final long value = -1;
		Verify.notNegative(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotNegative_Long_Zero()
	{
		final long value = 0;
		Verify.notNegative(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotNegative_Long_Positive()
	{
		final long value = 1;
		Verify.notNegative(value, PARM_value);
	}

	/**
	 * Test method for {@link com.dell.mensa.util.Verify#notNull(java.lang.Object, java.lang.String)}.
	 */
	@SuppressWarnings("static-method")
	@Test(expected = IllegalArgumentException.class)
	public void testNotNull_Null()
	{
		final Object value = null;
		Verify.notNull(value, PARM_value);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNotNull()
	{
		final Object value = "x";
		Verify.notNull(value, PARM_value);
	}
}
