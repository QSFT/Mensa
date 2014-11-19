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
public class ReadOnlyTailBufferTest
{
	// =========================================================================
	// Fixture
	// =========================================================================
	protected static class TestBuffer implements ITailBuffer<Object>
	{
		public String result;

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#add(java.lang.Object)
		 */
		@Override
		public void add(final Object symbol_)
		{
			result = String.format("add(%s)", symbol_);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#capacity()
		 */
		@Override
		public int capacity()
		{
			result = "capacity()";
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#clear()
		 */
		@Override
		public void clear()
		{
			result = "clear()";
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#end()
		 */
		@Override
		public long end()
		{
			result = "end()";
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#isEmpty()
		 */
		@Override
		public boolean isEmpty()
		{
			result = "isEmpty()";
			return false;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#isFull()
		 */
		@Override
		public boolean isFull()
		{
			result = "isFull()";
			return false;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#remove()
		 */
		@Override
		public Object remove()
		{
			result = "remove()";
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#size()
		 */
		@Override
		public int size()
		{
			result = "size()";
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#start()
		 */
		@Override
		public long start()
		{
			result = "start()";
			return 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.dell.mensa.ITailBuffer#symbolAt(int)
		 */
		@Override
		public Object symbolAt(final long iPosition_)
		{
			result = String.format("symbolAt(%s)", iPosition_);
			return null;
		}
	}

	private TestBuffer testBuffer;
	private ITailBuffer<Object> roBuffer;

	@Before
	public void setUp()
	{
		testBuffer = new TestBuffer();
		roBuffer = new ReadOnlyTailBuffer<>(testBuffer);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for
	 * {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#ReadOnlyTailBuffer(com.dell.mensa.ITailBuffer)} .
	 */
	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = IllegalArgumentException.class)
	public void testCtor_IllegalArgument()
	{
		new ReadOnlyTailBuffer<>(null);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#add(java.lang.Object)}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testAdd()
	{
		roBuffer.add(0);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#capacity()}.
	 */
	@Test
	public void testCapacity()
	{
		roBuffer.capacity();
		Assert.assertEquals("capacity()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#clear()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testClear()
	{
		roBuffer.clear();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#end()}.
	 */
	@Test
	public void testEnd()
	{
		roBuffer.end();
		Assert.assertEquals("end()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#isEmpty()}.
	 */
	@Test
	public void testIsEmpty()
	{
		roBuffer.isEmpty();
		Assert.assertEquals("isEmpty()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#isFull()}.
	 */
	@Test
	public void testIsFull()
	{
		roBuffer.isFull();
		Assert.assertEquals("isFull()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#remove()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemove()
	{
		roBuffer.remove();
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#size()}.
	 */
	@Test
	public void testSize()
	{
		roBuffer.size();
		Assert.assertEquals("size()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#start()}.
	 */
	@Test
	public void testStart()
	{
		roBuffer.start();
		Assert.assertEquals("start()", testBuffer.result);
	}

	/**
	 * Test method for {@link com.dell.mensa.impl.generic.ReadOnlyTailBuffer#symbolAt(long)}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testSymbolAt()
	{
		roBuffer.add(0);
	}
}
