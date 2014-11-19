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

import com.dell.mensa.impl.generic.AhoCorasickMachine;

/**
 * {@link IMatchListener} defines the interface used by a {@link AhoCorasickMachine} to send notifications when a
 * keyword is matched.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public interface IMatchListener<S extends Comparable<S>>
{
	/**
	 * Called by a pattern matching machine at the start of a matching operation.
	 *
	 * @param machine_
	 *            specifies the machine performing the matching operation.
	 *
	 * @return Returns {@code true} if this listener is interested in further notifications for the current match
	 *         operation, or {@code false} otherwise.
	 */
	boolean notifyBeginMatching(final AhoCorasickMachine<S> machine_);

	/**
	 * Called by a pattern matching machine at end of a matching operation.
	 *
	 * @param machine_
	 *            specifies the machine performing the matching operation.
	 */
	void notifyEndMatching(final AhoCorasickMachine<S> machine_);

	/**
	 * Called by a pattern matching machine when a keyword is matched.
	 *
	 * @param match_
	 *            specifies the newly recognized match.
	 *
	 * @return Returns {@code true} if this listener is interested in further notifications for the current match
	 *         operation, or {@code false} otherwise.
	 */
	boolean notifyMatch(IMatch<S> match_);
}
