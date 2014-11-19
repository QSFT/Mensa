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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.ITailBuffer;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class TailBufferTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	/**
	 * Capacity of buffer being tested.
	 */
	private static final int CAPACITY = 127;

	/**
	 * Null symbol period.
	 */
	private static final int NULL_PERIOD = 31;

	/**
	 * Position of first null symbol.
	 */
	private static final int NULL_POSITION = 13;

	/**
	 * Length of simulated text source.
	 */
	private static final int TEXT_LENGTH = 1000 * CAPACITY * NULL_PERIOD;

	/**
	 * Prime coefficient of symbol values such that symbolAt(i) = PRIME * i.
	 */
	private static final int PRIME = 63;

	/**
	 * Tail buffer being tested.
	 */
	private ITailBuffer<Integer> buffer;

	@Before
	public void setUp()
	{
		buffer = new TailBuffer<>(CAPACITY);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	@Test
	public void testBasics()
	{
		buffer = new TailBuffer<>(3);

		Assert.assertEquals(0, buffer.start());
		Assert.assertEquals(0, buffer.end());
		Assert.assertEquals(0, buffer.size());
		Assert.assertTrue(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());

		buffer.add(0);
		Assert.assertEquals(0, buffer.start());
		Assert.assertEquals(1, buffer.end());
		Assert.assertEquals(1, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(0), buffer.symbolAt(0));

		buffer.add(1);
		Assert.assertEquals(0, buffer.start());
		Assert.assertEquals(2, buffer.end());
		Assert.assertEquals(2, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(0), buffer.symbolAt(0));
		Assert.assertEquals(Integer.valueOf(1), buffer.symbolAt(1));

		buffer.add(2);
		Assert.assertEquals(0, buffer.start());
		Assert.assertEquals(3, buffer.end());
		Assert.assertEquals(3, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertTrue(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(0), buffer.symbolAt(0));
		Assert.assertEquals(Integer.valueOf(1), buffer.symbolAt(1));
		Assert.assertEquals(Integer.valueOf(2), buffer.symbolAt(2));

		buffer.add(3);
		Assert.assertEquals(1, buffer.start());
		Assert.assertEquals(4, buffer.end());
		Assert.assertEquals(3, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertTrue(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(1), buffer.symbolAt(1));
		Assert.assertEquals(Integer.valueOf(2), buffer.symbolAt(2));
		Assert.assertEquals(Integer.valueOf(3), buffer.symbolAt(3));

		Integer symbol = buffer.remove();
		Assert.assertEquals(2, buffer.start());
		Assert.assertEquals(4, buffer.end());
		Assert.assertEquals(2, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(1), symbol);
		Assert.assertEquals(Integer.valueOf(2), buffer.symbolAt(2));
		Assert.assertEquals(Integer.valueOf(3), buffer.symbolAt(3));

		symbol = buffer.remove();
		Assert.assertEquals(3, buffer.start());
		Assert.assertEquals(4, buffer.end());
		Assert.assertEquals(1, buffer.size());
		Assert.assertFalse(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(2), symbol);
		Assert.assertEquals(Integer.valueOf(3), buffer.symbolAt(3));

		symbol = buffer.remove();
		Assert.assertEquals(4, buffer.start());
		Assert.assertEquals(4, buffer.end());
		Assert.assertEquals(0, buffer.size());
		Assert.assertTrue(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
		Assert.assertEquals(Integer.valueOf(3), symbol);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#TailBuffer(int)}.
	 */
	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_IllegalArgument()
	{
		new TailBuffer<Integer>(0);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#TailBuffer()}.
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCtor()
	{
		final ITailBuffer<Integer> t = new TailBuffer<>();
		Assert.assertEquals(TailBuffer.DEFAULT_CAPACITY, t.capacity());
		Assert.assertEquals(0, t.start());
		Assert.assertEquals(0, t.end());
		Assert.assertEquals(0, t.size());
		Assert.assertTrue(t.isEmpty());
		Assert.assertFalse(t.isFull());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#add(java.lang.Object)}.
	 */
	@Test
	public void testAdd()
	{
		Assert.assertEquals(0, buffer.end());
		for (int i = 0; i < 2 * CAPACITY; i++)
		{
			buffer.add(i);
			Assert.assertEquals(i + 1 < CAPACITY ? 0 : i + 1 - CAPACITY, buffer.start());
			Assert.assertEquals(i + 1, buffer.end());
			Assert.assertEquals(Integer.valueOf(i), buffer.symbolAt(i));
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#capacity()}.
	 */
	@Test
	public void testCapacity()
	{
		Assert.assertEquals(TailBuffer.DEFAULT_CAPACITY, new TailBuffer<>().capacity());
		Assert.assertEquals(CAPACITY, buffer.capacity());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#clear()}.
	 */
	@Test
	public void testClear()
	{
		fill();
		Assert.assertEquals(CAPACITY, buffer.size());
		buffer.clear();
		Assert.assertEquals(CAPACITY, buffer.capacity());
		Assert.assertEquals(0, buffer.start());
		Assert.assertEquals(0, buffer.end());
		Assert.assertEquals(0, buffer.size());
		Assert.assertTrue(buffer.isEmpty());
		Assert.assertFalse(buffer.isFull());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#end()}.
	 */
	@Test
	public void testEnd()
	{
		Assert.assertEquals(0, buffer.end());
		for (int i = 0; i < 2 * CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertEquals(i + 1, buffer.end());
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#isEmpty()}.
	 */
	@Test
	public void testIsEmpty()
	{
		Assert.assertTrue(buffer.isEmpty());
		for (int i = 0; i < 2 * CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertFalse(buffer.isEmpty());
		}
		buffer.clear();
		Assert.assertTrue(buffer.isEmpty());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#isFull()}.
	 */
	@Test
	public void testIsFull()
	{
		Assert.assertFalse(buffer.isFull());
		for (int i = 0; i < 2 * CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertEquals(
					String.format("i=%d; ", i),
					i + 1 >= CAPACITY,
					buffer.isFull());
		}
		buffer.clear();
		Assert.assertFalse(buffer.isFull());
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#remove()}.
	 */
	@Test
	public void testRemove()
	{
		fill();
		int size = CAPACITY;
		Assert.assertEquals(size, buffer.size());
		Assert.assertEquals(TEXT_LENGTH, buffer.end());
		Assert.assertTrue(buffer.isFull());
		long start = buffer.start();
		for (int i = 0; i < CAPACITY; i++)
		{
			Assert.assertEquals(expectedSymbol(start), buffer.remove());
			++start;
			--size;
			Assert.assertEquals(start, buffer.start());
			Assert.assertEquals(TEXT_LENGTH, buffer.end());
			Assert.assertFalse(buffer.isFull());
			Assert.assertEquals(size, buffer.size());
		}
		Assert.assertEquals(0, buffer.size());
		Assert.assertTrue(buffer.isEmpty());
	}

	@Test(expected = IllegalStateException.class)
	public void testRemove_IllegalState()
	{
		buffer.remove();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#size()}.
	 */
	@Test
	public void testSize()
	{
		Assert.assertEquals(0, buffer.size());
		for (int i = 0; i < 2 * CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertEquals(
					i < CAPACITY ? i + 1 : CAPACITY,
					buffer.size());
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#start()}.
	 */
	@Test
	public void testStart()
	{
		Assert.assertEquals(0, buffer.start());
		for (int i = 0; i < CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertEquals(0, buffer.start());
		}

		for (int i = 1; i < 2 * CAPACITY; i++)
		{
			buffer.add(0);
			Assert.assertEquals(i, buffer.start());
		}
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.TailBuffer#symbolAt(long)}.
	 */
	@Test
	public void testSymbolAt()
	{
		fill();
		Assert.assertEquals(TEXT_LENGTH - CAPACITY, buffer.start());
		Assert.assertEquals(TEXT_LENGTH, buffer.end());
		validate();
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSymbolAt_Underflow()
	{
		fill();
		buffer.symbolAt(TEXT_LENGTH - CAPACITY - 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSymbolAt_Overflow()
	{
		fill();
		buffer.symbolAt(TEXT_LENGTH);
	}

	// =========================================================================
	// Internal methods
	// =========================================================================

	private static Integer expectedSymbol(final long i)
	{
		return i % NULL_PERIOD == NULL_POSITION ? null : Integer.valueOf((int) (i * PRIME));
	}

	private void fill()
	{
		for (int i = 0; i < TEXT_LENGTH; i++)
		{
			buffer.add(expectedSymbol(i));
		}
	}

	private void validate()
	{
		for (long i = buffer.start(); i < buffer.end(); i++)
		{
			Assert.assertEquals(expectedSymbol(i), buffer.symbolAt(i));
		}
	}
}
