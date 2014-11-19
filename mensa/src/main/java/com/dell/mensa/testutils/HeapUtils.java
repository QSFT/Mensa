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
package com.dell.mensa.testutils;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public final class HeapUtils
{
	private static final Runtime RUNTIME = Runtime.getRuntime();

	private HeapUtils()
	{
		// do not instantiate
	}

	public static long heapSnapshot()
	{
		runGC();
		return usedMemory();
	}

	public static long usedMemory()
	{
		return RUNTIME.totalMemory() - RUNTIME.freeMemory();
	}

	private static void runGC()
	{
		// It helps to call Runtime.gc() using several method calls:
		for (int r = 0; r < 4; ++r)
		{
			_runGC();
		}
	}

	private static void _runGC()
	{
		long usedMem1 = usedMemory();
		long usedMem2 = Long.MAX_VALUE;

		for (int i = 0; usedMem1 < usedMem2 && i < 500; ++i)
		{
			RUNTIME.runFinalization();
			RUNTIME.gc();
			Thread.currentThread();
			Thread.yield();

			usedMem2 = usedMem1;
			usedMem1 = usedMemory();
		}
	}
}
