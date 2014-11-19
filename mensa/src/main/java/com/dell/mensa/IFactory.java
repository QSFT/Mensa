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

import com.dell.mensa.impl.generic.GotoFunction;
import com.dell.mensa.impl.generic.OutputFunction;

/**
 * {@link IFactory} specifies the interface to an object factory used to create various object types without exposing
 * implementation class details to the client.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface IFactory<S>
{
	/**
	 * Creates a concrete {@link IEdgeMap} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	IEdgeMap<S> createEdgeMap();

	/**
	 * Creates a concrete {@link IFailureFunction} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	IFailureFunction createFailureFunction();

	/**
	 * Creates a concrete {@link GotoFunction} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	GotoFunction<S> createGotoFunction();

	/**
	 * Creates a concrete {@link IKeywords} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	IKeywords<S> createKeywords();

	/**
	 * Creates a concrete {@link INextMoveFunction} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	INextMoveFunction<S> createNextMoveFunction();

	/**
	 * Creates a concrete {@link OutputFunction} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	OutputFunction<S> createOutputFunction();

	/**
	 * Creates a concrete {@link IStateMap} instance.
	 *
	 * @return Returns a newly created instance.
	 */
	IStateMap<S> createStateMap();
}
