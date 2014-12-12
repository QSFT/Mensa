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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import com.dell.mensa.IEdge;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IFailureFunction;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IMatch;
import com.dell.mensa.IMatchListener;
import com.dell.mensa.IMatchPrecisionFunction;
import com.dell.mensa.INextMoveFunction;
import com.dell.mensa.IOutputFunction;
import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.util.Verify;

/**
 * <p>
 * {@link AhoCorasickMachine} is a generic, flexible, enhanced, and efficient implementation of a pattern matching state
 * machine as described by the <a href="#paper">1975 paper by Alfred Aho and Margaret Corasick</a>. This implementation
 * is
 * </p>
 *
 * <ul>
 *
 * <li><i>generic</i> in that it can be used to match any type of symbols as defined by the Java template type {@code S}
 * &mdash; e.g., it is possible to create a machine to match bytes, characters, integers, gene sequences, bit sequences,
 * etc.;</li>
 *
 * <li><i>flexible</i> in that the architecture allows for granular extension, customization, or replacement of
 * framework components;</li>
 *
 * <li><i>enhanced</i> in that it supports a number of useful extension not addressed in the original paper, such as
 * whole-word matching, case-sensitivity controls, fuzzy whitespace matching, fuzzy punctuation matching, incremental
 * matching (i.e., iterators), matching event listeners, etc.; and</li>
 *
 * <li><i>efficient</i> in that it performs well in terms of both time and resource usages on very large (~million)
 * keyword sets.</li>
 *
 * </ul>
 *
 * <p>
 * Usage consists of three phases:
 * </p>
 *
 * <ol>
 *
 * <li>Construction (i.e., creating a machine instance);</li>
 *
 * <li>Initialization (i.e., preparing the machine for use with a specific set of keywords); and</li>
 *
 * <li>Matching (i.e., running the machine to find matches in a text source).</li>
 *
 * </ol>
 *
 * <p>
 * A machine instance is constructed exactly once. It must the be initialized before matching can occur. Once
 * initialized, the machine be used repeatedly for matching operations. It is also possible to reset and reinitialize a
 * machine for use with a different set of keywords.
 * </p>
 *
 * <h3>Construction</h3>
 *
 * The {@link #AhoCorasickMachine(IFactory, ISymbolClassifier)} constructor accepts two parameters that abstract two
 * important implementation details: object creation and symbol classification.
 *
 * The {@link IFactory} parameter provides a concrete object factory used to create instances of the various objects
 * used by the machine during the initialization and matching phases. This makes it possible to utilize implementation
 * that have different runtime characteristics or that may be optimized for specific symbol types.
 *
 * The {@link ISymbolClassifier} parameter provides a concrete instance of various symbol-type specific operations and
 * option settings such as converting a symbol to lower case, which is a data type dependent operation.
 *
 * <h3>Initialization</h3>
 *
 * Once a machine is constructed, it exists, but has no knowledge of what keywords to find. During the initialization
 * phase, the {@link #build(IKeywords)} method can be used to build the internal data structures used by the machine to
 * match a given collection of keywords.
 *
 * <p>
 * An application may also set various machine options, such as {@link #setNotifyLongestMatch(boolean)} or
 * {@link #setNotifyRawSymbols(boolean)} during the initialization phase. (Although it is possible to change options
 * during the matching phase, in most applications, doing so is not necessary.)
 * </p>
 *
 * <h3>Matching</h3>
 *
 * <p>
 * Once a machine has been initialed, it may be used to find keywords within a text source. There are two ways to
 * perform a matching operation: using an iterator or using a callback.
 * </p>
 *
 * <p>
 * Regardless of which mechanism is used, matching is performed against an {@link ITextSource} that defines the input
 * against which the machine will execute. The application is responsible for opening the text source prior to matching
 * and closing the text source after matching.
 * </p>
 *
 * <p>
 * <i>Using an iterator:</i>
 * </p>
 *
 * <p>
 * Call {@link #matchIterator(ITextSource)} to obtain a match {@link Iterator} for a given text source. Then use the
 * iterator as you would any Java iterator to iterate over the collection of {@link IMatch} instances. Matching is
 * performed lazily, so the machine will do only as much work as necessary to satisfy actual requests to
 * {@link Iterator#hasNext()} and/or {@link Iterator#next()}.
 * </p>
 *
 * <p>
 * <i>Using a callback:</i>
 * </p>
 *
 * <p>
 * First, define an {@link IMatchListener} instance to receive match notifications. Then call
 * {@link #match(ITextSource, IMatchListener)} to perform the matching operation. The machine will notify the listener
 * when matching begins, when each new match is discovered, and when matching finishes. The listener can terminate
 * matching at any time by returning {@code false} to the machine in response to a match notification.
 * </p>
 *
 * <h3>Thread Safety</h3>
 *
 * <p>
 * Instances of this class are thread-safe, in that, <i>once initialized</i>, a single instance can be used concurrently
 * by multiple threads to perform matching operations against different text sources.
 * </p>
 *
 * <h3><a name="paper">Reference</a></h3>
 *
 * <blockquote> Aho, Alfred V.; Corasick, Margaret J. (June 1975). <i>Efficient string matching: An aid to bibliographic
 * search</i>. Communications of the ACM 18 (6): 333&ndash;340. doi:<a
 * href="http://dx.doi.org/10.1145%2F360825.360855">10.1145/360825.360855</a></blockquote>
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 * @param <S>
 *            the data type of the symbols
 *
 */
public class AhoCorasickMachine<S extends Comparable<S>>
{
	// private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	private static final String PARM_factory = "factory_";
	private static final String PARM_classifier = "classifier_";
	private static final String PARM_keywords = "keywords_";
	private static final String PARM_listener = "listener_";
	private static final String PARM_textSource = "textSource_";

	private static final String MSG_CONSECUTIVE_WHITESPACE = "keyword contains consecutive whitespace symbols: %s";
	private static final String MSG_GOTO_ALREADY_CONSTRUCTED = "goto function is already constructed";
	private static final String MSG_GOTO_NOT_CONSTRUCTED = "goto function has not yet been constructed";
	private static final String MSG_FAILURE_ALREADY_CONSTRUCTED = "failure function is already constructed";
	private static final String MSG_FAILURE_NOT_CONSTRUCTED = "failure function has not yet been constructed";
	private static final String MSG_LEADING_WHITESPACE = "keyword contains leading whitespace symbols: %s";
	private static final String MSG_NEXT_MOVE_ALREADY_CONSTRUCTED = "next move function is already constructed";
	private static final String MSG_RANGE_OUTSIDE_BUFFER = "requested range [%d, %d) is outside available buffer range [%d, %d)";
	private static final String MSG_TRAILING_WHITESPACE = "keyword contains trailing whitespace symbols: %s";
	private static final double PRECISION_DELTA = 0.0000001;

	// =========================================================================
	// Properties
	// =========================================================================
	private final IFactory<S> factory;
	private final ISymbolClassifier<S> classifier;
	private final S space;
	private final boolean isCaseExtensionEnabled;
	private final boolean isPunctuationExtensionEnabled;
	private final boolean isWordBreakExtensionEnabled;
	private final IMatchPrecisionFunction<S> matchPrecisionFunction;

	/**
	 * This {@link Comparator} determines the order that keyword matches reported when multiple matches are found at the
	 * exact same position.
	 */
	private final Comparator<IKeyword<S>> keywordComparator;

	private int numStates;
	private INextMoveFunction<S> nextMoveFunction;
	private IGotoFunction<S> gotoFunction;
	private IFailureFunction failureFunction;
	private IOutputFunction<S> outputFunction;

	private boolean bNotifyLongestMatch;
	private boolean bNotifyMostPreciseMatch;
	private boolean bNotifyRawSymbols;

	/**
	 * Returns the associated {@link ISymbolClassifier}.
	 *
	 * @return Returns the associated {@link ISymbolClassifier}.
	 */
	public ISymbolClassifier<S> getClassifier()
	{
		return classifier;
	}

	/**
	 * @return the failureFunction
	 */
	public IFailureFunction getFailureFunction()
	{
		return failureFunction;
	}

	/**
	 * @return the gotoFunction
	 */
	public IGotoFunction<S> getGotoFunction()
	{
		return gotoFunction;
	}

	/**
	 * @return the nextMoveFunction
	 */
	public INextMoveFunction<S> getNextMoveFunction()
	{
		return nextMoveFunction;
	}

	/**
	 * @return The number of states.
	 */
	public int getNumStates()
	{
		return numStates;
	}

	/**
	 * @return the outputFunction
	 */
	public IOutputFunction<S> getOutputFunction()
	{
		return outputFunction;
	}

	/**
	 * Determines how multiple keyword matches the same ending position are reported. See
	 * {@link #isNotifyLongestMatch()} for further discussion.
	 *
	 * @return Returns {@code true} if only the longest keyword at an ending position will be reported. Returns
	 *         {@code false} if all keywords at an ending position will be reported.
	 */
	public boolean isNotifyLongestMatch()
	{
		return bNotifyLongestMatch;
	}

	/**
	 * @return the bNotifyMostPreciseMatch
	 */
	public boolean isNotifyMostPreciseMatch()
	{
		return bNotifyMostPreciseMatch;
	}

	/**
	 * Determines if raw symbols are included in match notifications. See {@link #setNotifyRawSymbols(boolean)} for
	 * further discussion.
	 *
	 * @return Returns {@code true} if raw symbols are reported in match notifications; {@code false} otherwise.
	 */
	public boolean isNotifyRawSymbols()
	{
		return this.bNotifyRawSymbols;
	}

	/**
	 * Determines whether or not match length filtering is performed when reporting matches. Match length filtering
	 * affects how multiple keyword matches at the same match position are reported.
	 *
	 * <p>
	 * When match length filtering is disabled, all matches are reported. When match length filtering is enabled, only
	 * those matches having the greatest match length are reported.
	 * </p>
	 *
	 * @param bEnable_
	 *            specify {@code true} to enable match length filtering, or {@code false} to disable match length
	 *            filtering.
	 */
	public void setNotifyLongestMatch(final boolean bEnable_)
	{
		this.bNotifyLongestMatch = bEnable_;
	}

	/**
	 * Determines whether or not precision filtering is performed when reporting matches. Precision filtering affects
	 * how multiple keyword matches at the same match position are reported.
	 *
	 * <p>
	 * When precision filtering is disabled, all matches are reported. When precision filtering is enabled, only those
	 * matches having the highest precision value are reported.
	 * </p>
	 *
	 * @param bEnable_
	 *            specify {@code true} to enable precision filtering, or {@code false} to disable precision filtering.
	 */
	public void setNotifyMostPreciseMatch(final boolean bEnable_)
	{
		this.bNotifyMostPreciseMatch = bEnable_;
	}

	/**
	 * Controls whether or not raw symbols are included in match notifications.
	 *
	 * <p>
	 * When {@link #match(ITextSource, IMatchListener)} or {@link #match(ITextSource, IMatchListener)} finds a new
	 * match, a new {@link IMatch} instance is created containing details of the match. By default, the details do not
	 * include the raw symbols from the input text source that were matched. However, calling
	 * {@link #setNotifyRawSymbols(boolean)} with a value of {@code true} instructs the machine to include the raw
	 * symbols matched in the details.
	 * </p>
	 *
	 * @param bEnable_
	 *            specify {@code true} to include raw symbols in match details, or {@code false} to omit raw symbols
	 *            from match details.
	 *
	 * @see IMatch#getRawSymbols()
	 */
	public void setNotifyRawSymbols(final boolean bEnable_)
	{
		this.bNotifyRawSymbols = bEnable_;
	}

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance using the specified object factory and a {@link DefaultSymbolClassifier}.
	 *
	 * @param factory_
	 *            specifies the object factory to use with this instance.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified object factory is {@code null}.
	 */
	public AhoCorasickMachine(final IFactory<S> factory_)
	{
		this(factory_, new DefaultSymbolClassifier<S>());
	}

	/**
	 * Constructs a new instance using the specified object factory and symbol classifier.
	 *
	 * @param factory_
	 *            specifies the object factory to use with this instance.
	 * @param classifier_
	 *            specifies the symbol classifier to use with this instance.
	 *
	 * @throws IllegalArgumentException
	 *             if the specified object factory or symbol classifier is {@code null}.
	 */
	public AhoCorasickMachine(final IFactory<S> factory_, final ISymbolClassifier<S> classifier_)
	{
		Verify.notNull(factory_, PARM_factory);
		Verify.notNull(classifier_, PARM_classifier);

		this.factory = factory_;
		this.classifier = classifier_;
		this.space = classifier_.getSpace();
		this.isCaseExtensionEnabled = classifier_.isCaseExtensionEnabled();
		this.isPunctuationExtensionEnabled = classifier_.isPunctuationExtensionEnabled() && space != null;
		this.isWordBreakExtensionEnabled = classifier.isWordBreakExtensionEnabled();
		this.matchPrecisionFunction = classifier_.getMatchPrecisionFunction();

		this.keywordComparator = new Comparator<IKeyword<S>>()
		{
			@Override
			public int compare(final IKeyword<S> keyword1_, final IKeyword<S> keyword2_)
			{
				// Sort key #1: length
				// keyword1_ comes before keyword2_ if keyword1_ is *longer* than keyword2_.
				int result = keyword2_.length() - keyword1_.length();
				if (result != 0)
				{
					return result;
				}

				// Sort key #2: "alphabetic"
				// Keywords of the same length are ordered "alphabetically" by comparing symbols.
				final int length = keyword1_.length();
				for (int i = 0; i < length; ++i)
				{
					final S a1 = keyword1_.symbolAt(i);
					final S a2 = keyword2_.symbolAt(i);

					result = a1.compareTo(a2);

					if (result != 0)
					{
						return result;
					}
				}

				// Sort key #3: punctuation sensitivity
				// Punctuation-sensitive keywords come first
				Boolean b1 = Boolean.valueOf(keyword1_.isPunctuationSensitive());
				Boolean b2 = Boolean.valueOf(keyword2_.isPunctuationSensitive());

				result = b1.compareTo(b2);
				if (result != 0)
				{
					return -result;
				}

				// Sort key #4: case sensitivity
				// Case-sensitive keywords come first
				b1 = Boolean.valueOf(keyword1_.isCaseSensitive());
				b2 = Boolean.valueOf(keyword2_.isCaseSensitive());

				result = b1.compareTo(b2);
				if (result != 0)
				{
					return -result;
				}

				// Sort key #5: hash code
				return keyword1_.hashCode() - keyword2_.hashCode();
			}
		};

		_reset();
	}

	// =========================================================================
	// Public methods
	// =========================================================================

	/**
	 * Builds the machine state necessary to match the specified keywords.
	 *
	 * @param keywords_
	 *            specifies the set of keywords to be matched by the machine. This list may be empty but must not be
	 *            {@code null}.
	 *
	 * @throws IllegalArgumentException
	 *             if the keywords parameter is {@code null} or if a keyword contains non-normalized whitespace symbols.
	 *             See {@link #buildGotoFunction(IKeywords)} for further discussion.
	 *
	 * @throws IllegalStateException
	 *             if a goto function or next move function has already been constructed.
	 */
	public void build(final IKeywords<S> keywords_)
	{
		buildGotoFunction(keywords_);
		buildFailureFunction();
		buildNextMoveFunction();
	}

	/**
	 * Construction of the failure function. This method implements <b>Algorithm 3</b> described in <a
	 * href="#paper">1975 paper by Alfred V. Aho and Margaret J. Corasick</a>.
	 *
	 * <p>
	 * This method uses as input the output of {@link #buildGotoFunction(IKeywords)} and therefore, this method must be
	 * called after that method.
	 * </p>
	 *
	 * @return Returns the newly constructed {@link IFailureFunction}. This result can also be accessed later using
	 *         {@link #getFailureFunction()}.
	 *
	 * @throws IllegalStateException
	 *             if the goto function has not yet been constructed or the failure function (or next move function) has
	 *             already been constructed.
	 */
	public IFailureFunction buildFailureFunction()
	{
		if (nextMoveFunction != null)
		{
			throw new IllegalStateException(MSG_NEXT_MOVE_ALREADY_CONSTRUCTED);
		}

		if (gotoFunction == null)
		{
			throw new IllegalStateException(MSG_GOTO_NOT_CONSTRUCTED);
		}

		if (failureFunction != null)
		{
			throw new IllegalStateException(MSG_FAILURE_ALREADY_CONSTRUCTED);
		}

		failureFunction = factory.createFailureFunction();

		// Compute the states of depth 1 and enter them into a FIFO queue.
		// ---------------------------------------------------------------
		final Queue<Integer> queue = new LinkedList<>();
		for (final int s : gotoFunction.getEdgeMap(IGotoFunction.START_STATE).getStates())
		{
			if (s != IGotoFunction.START_STATE)
			{
				queue.add(s);
				failureFunction.put(s, IGotoFunction.START_STATE);
			}
		}

		// Loop to compute the set of states of depth d from the set of states of depth d-1.
		// ---------------------------------------------------------------------------------
		while (!queue.isEmpty())
		{
			final int r = queue.remove();
			final IEdgeMap<S> edgeMap = gotoFunction.getEdgeMap(r);
			if (edgeMap != null)
			{
				for (final IEdge<S> edge : edgeMap.getEdges())
				{
					final S a = edge.getSymbol();
					assert a != null;

					final int s = edge.getState();
					assert s >= 0;

					queue.add(s);

					int state = failureFunction.eval(r);
					while (gotoFunction.eval(state, a) == IGotoFunction.NO_STATE)
					{
						state = failureFunction.eval(state);
					}

					failureFunction.put(s, gotoFunction.eval(state, a));
					outputFunction.put(s, outputFunction.output(failureFunction.eval(s)));
				}
			}
		}

		return failureFunction;
	}

	/**
	 * Construction of the goto function (and the output function). This method implements <b>Algorithm 2</b> described
	 * in <a href="#paper">1975 paper by Alfred V. Aho and Margaret J. Corasick</a>.
	 *
	 * @param keywords_
	 *            specifies the set of keywords to be matched by the machine. This list may be empty but must not be
	 *            {@code null}. Keywords containing whitespace symbols (as determined by
	 *            {@link ISymbolClassifier#isWhitespace(Object)} must be <i>normalized</i>. Specifically, a keyword must
	 *            not contain leading, trailing, nor consecutive whitespace symbols.
	 *
	 * @return Returns the newly constructed {@link IGotoFunction}. This result can also be accessed later using
	 *         {@link #getGotoFunction()}.
	 *
	 * @throws IllegalArgumentException
	 *             if the keywords parameter is {@code null} or if if a keyword contains non-normalized whitespace
	 *             symbols.
	 *
	 * @throws IllegalStateException
	 *             if a goto function or next move function has already been constructed.
	 */
	public IGotoFunction<S> buildGotoFunction(final IKeywords<S> keywords_)
	{
		Verify.notNull(keywords_, PARM_keywords);

		if (nextMoveFunction != null)
		{
			throw new IllegalStateException(MSG_NEXT_MOVE_ALREADY_CONSTRUCTED);
		}

		if (gotoFunction != null)
		{
			throw new IllegalStateException(MSG_GOTO_ALREADY_CONSTRUCTED);
		}

		final int startState = createState();
		gotoFunction = factory.createGotoFunction();
		outputFunction = factory.createOutputFunction();

		for (final IKeyword<S> keyword : keywords_)
		{
			enter(keyword);
		}

		gotoFunction.put(startState, null, startState);

		return gotoFunction;
	}

	/**
	 * Construction of a deterministic finite automaton. This method implements <b>Algorithm 4</b> described in <a
	 * href="#paper">1975 paper by Alfred V. Aho and Margaret J. Corasick</a>.
	 *
	 * @return Returns the newly constructed {@link INextMoveFunction}. This result can also be accessed later using
	 *         {@link #getNextMoveFunction()}.
	 *
	 * @throws IllegalStateException
	 *             if the goto and failure functions have not yet been constructed or the next move function has already
	 *             been constructed.
	 */
	public INextMoveFunction<S> buildNextMoveFunction()
	{
		if (nextMoveFunction != null)
		{
			throw new IllegalStateException(MSG_NEXT_MOVE_ALREADY_CONSTRUCTED);
		}

		if (failureFunction == null)
		{
			throw new IllegalStateException(MSG_FAILURE_NOT_CONSTRUCTED);
		}

		if (gotoFunction == null)
		{
			throw new IllegalStateException(MSG_GOTO_NOT_CONSTRUCTED);
		}

		nextMoveFunction = factory.createNextMoveFunction();

		final Queue<Integer> queue = new ArrayDeque<>(); // new LinkedList<>();
		final Collection<S> symbols = gotoFunction.symbols();

		for (final S a : symbols)
		{
			final int s = gotoFunction.eval(IGotoFunction.START_STATE, a);
			nextMoveFunction.put(IGotoFunction.START_STATE, a, s);
			if (s != IGotoFunction.START_STATE)
			{
				queue.add(s);
			}
		}

		while (!queue.isEmpty())
		{
			final int r = queue.remove();

			for (final S a : symbols)
			{
				final int s = gotoFunction.eval(r, a);
				final int next;
				if (s == IGotoFunction.NO_STATE)
				{
					next = nextMoveFunction.eval(failureFunction.eval(r), a);
				}
				else
				{
					queue.add(s);
					next = s;
				}
				nextMoveFunction.put(r, a, next);
			}
		}

		nextMoveFunction.optimize();

		gotoFunction = null;
		failureFunction = null;

		return nextMoveFunction;
	}

	/**
	 * Runs this pattern matching machine to find keywords in a specified text source. This method implements (an
	 * enhanced version of) <b>Algorithm 1</b> described in <a href="#paper">Aho-Corasick 1975</a>.
	 *
	 * <p>
	 * This machine instance must be have already been initialized such that it has a "next move" function OR it has
	 * both "goto" and "error" functions. Note that, for performance reasons, it is generally preferable to use a
	 * "next move" function.
	 * </p>
	 *
	 * <p>
	 * Matching proceeds sequentially through the input text source beginning with position zero and proceeding to
	 * end-of-file. Matches are reported to the specified {@link IMatchListener} in the order in which they are
	 * discovered based on the <i>ending</i> position of the match. Thus, reported ending positions will increase
	 * monotonically, but reported starting positions may not necessarily increase monotonically. (E.g., A shorter
	 * keyword contained within a longer keyword may be reported before the longer keyword, even thought the longer
	 * keyword <i>started</i> before the shorter keyword.)
	 * </p>
	 *
	 * <p>
	 * When multiple keywords share the same ending position, they are reported in order of descending keyword length
	 * (and thus, increasing starting position). By default, all such keywords are reported, but this behavior can be
	 * changed using {@link #setNotifyLongestMatch(boolean)}.
	 * </p>
	 *
	 * <p>
	 * Matching proceeds until end-of-file reached or until the listener returns {@code false}, indicating that it is no
	 * longer interested in receiving further notifications.
	 * </p>
	 *
	 * @param textSource_
	 *            specifies the input text source in against which matching is performed. The caller is responsible for
	 *            opening and closing the text source.
	 * @param listener_
	 *            specifes the listener to notify of matches.
	 *
	 * @return Returns the number of keywords matched.
	 *
	 * @throws IllegalStateException
	 *             if this instance has not been properly initialized of if the specified text source is not open.
	 *
	 * @throws IOException
	 *             if an error occurs reading the text source.
	 */
	public int match(final ITextSource<S> textSource_, final IMatchListener<S> listener_) throws IOException
	{
		Verify.notNull(listener_, PARM_listener);

		int matchCounter = 0;
		boolean bListening = listener_.notifyBeginMatching(this);
		if (bListening)
		{
			final Iterator<IMatch<S>> iterator = matchIterator(textSource_);
			while (bListening && iterator.hasNext())
			{
				bListening = listener_.notifyMatch(iterator.next());
				++matchCounter;
			}
			listener_.notifyEndMatching(this);
		}

		return matchCounter;
	}

	/**
	 * Creates an {@link Iterator} that uses this pattern matching machine to find keywords in a specified text source.
	 * This method implements (an enhanced version of) <b>Algorithm 1</b> described in <a href="#paper">Aho-Corasick
	 * 1975</a>.
	 *
	 * <p>
	 * This machine instance must be have already been initialized such that it has a "next move" function OR it has
	 * both "goto" and "error" functions. Note that, for performance reasons, it is generally preferable to use a
	 * "next move" function.
	 * </p>
	 *
	 * <p>
	 * Matching proceeds sequentially through the input text source beginning with position zero and proceeding to
	 * end-of-file. Matches are returned by the returned iterator's {@code next()} method in the order in which they are
	 * discovered based on the <i>ending</i> position of the match. Thus, returned ending positions will increase
	 * monotonically, but returned starting positions may not necessarily increase monotonically. (E.g., A shorter
	 * keyword contained within a longer keyword may be returned before the longer keyword, even thought the longer
	 * keyword <i>started</i> before the shorter keyword.)
	 * </p>
	 *
	 * <p>
	 * When multiple keywords share the same ending position, they are returned in order of descending keyword length
	 * (and thus, increasing starting position). By default, all such keywords are returned, but this behavior can be
	 * changed using {@link #setNotifyLongestMatch(boolean)}.
	 * </p>
	 *
	 * <p>
	 * Matching proceeds until end-of-file reached.
	 * </p>
	 *
	 * @param textSource_
	 *            specifies the input text source in against which matching is performed. The caller is responsible for
	 *            opening and closing the text source.
	 *
	 *
	 * @return Returns an new {@link Iterator} that uses this pattern matching machine to find keywords in a specified
	 *         text source.
	 *
	 * @throws IllegalStateException
	 *             if this instance has not been properly initialized of if the specified text source is not open.
	 */
	public Iterator<IMatch<S>> matchIterator(final ITextSource<S> textSource_)
	{
		Verify.notNull(textSource_, PARM_textSource);
		Verify.condition(textSource_.isOpen(), "textSource_ must be open");

		return new MatchIterator(textSource_);
	}

	/**
	 * Resets the machine to its initial state. This allows a machine instance to be rebuilt for use with a different
	 * set of keywords.
	 */
	public void reset()
	{
		_reset();
	}

	// =========================================================================
	// class: MatchIterator
	// =========================================================================
	/**
	 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
	 *
	 */
	private class MatchIterator implements Iterator<IMatch<S>>
	{
		private final ITextSource<S> textSource;
		private final Deque<IMatch<S>> matches;

		private int state;

		// =========================================================================
		// Constructors
		// =========================================================================
		public MatchIterator(final ITextSource<S> textSource_)
		{
			assert textSource_ != null;
			assert textSource_.isOpen();

			this.textSource = textSource_;
			this.matches = new ArrayDeque<>();

			state = IGotoFunction.START_STATE;

			if (nextMoveFunction == null)
			{
				if (gotoFunction == null)
				{
					throw new IllegalStateException(MSG_GOTO_NOT_CONSTRUCTED);
				}

				if (failureFunction == null)
				{
					throw new IllegalStateException(MSG_FAILURE_NOT_CONSTRUCTED);
				}

				gotoFunction.clearCalls();
			}
			else
			{
				nextMoveFunction.clearCalls();
			}
		}

		// =========================================================================
		// Iterator methods
		// =========================================================================
		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext()
		{
			findNext();
			return !matches.isEmpty();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#next()
		 */
		@Override
		public IMatch<S> next()
		{
			findNext();
			return matches.remove(); // This throws NoSuchElementException if matches.isEmpty().
		}

		// =========================================================================
		// Internal methods
		// =========================================================================

		private void findNext()
		{
			while (matches.isEmpty() && !textSource.isEof())
			{
				S a = read();

				if (isCaseExtensionEnabled)
				{
					a = classifier.toLowerCase(a);
				}

				state = move(state, a);
				if (state != IGotoFunction.START_STATE)
				{
					final IKeywords<S> keywords = outputFunction.output(state);
					if (keywords != null)
					{
						assert !keywords.isEmpty();

						if (isWordBreak(textSource.peek()))
						{
							try
							{
								collectMatches(keywords, textSource, matches);
							}
							catch (final IOException e)
							{
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		}

		private S read()
		{
			try
			{
				return textSource.read();
			}
			catch (final IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	// =========================================================================
	// class: KeywordMetaData
	// =========================================================================
	/**
	 * {link KeywordMetaData} encapsulates meta data describing an {@link IKeyword} instance. Keyword meta data is
	 * primarily useful when evaluating fuzzy matching heuristics.
	 *
	 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
	 *
	 */
	private class KeywordMetaData
	{
		private static final String PARM_keyword = "keyword_";

		// =========================================================================
		// Properties
		// =========================================================================
		private final IKeyword<S> keyword;
		private final List<S> requiredSymbols;
		private final int prefixSize;
		private final int suffixSize;

		/**
		 * @return Returns the {@link IKeyword} instance.
		 */
		public IKeyword<S> getKeyword()
		{
			return keyword;
		}

		/**
		 * @return Returns the symbols required to match this keyword.
		 */
		public List<S> getRequiredSymbols()
		{
			return requiredSymbols;
		}

		/**
		 * Gets the number of optionally matched whitespace/punctuation prefix symbols.
		 *
		 * @return Returns the number of optional prefix symbols.
		 */
		public int getPrefixSize()
		{
			return prefixSize;
		}

		/**
		 * Gets the number of optionally matched whitespace/punctuation suffix symbols.
		 *
		 * @return Returns the number of optional suffix symbols
		 */
		public int getSuffixSize()
		{
			return suffixSize;
		}

		// =========================================================================
		// Constructors
		// =========================================================================
		public KeywordMetaData(final IKeyword<S> keyword_)
		{
			super();

			Verify.notNull(keyword_, PARM_keyword);
			this.keyword = keyword_;

			requiredSymbols = new ArrayList<>(keyword_.length());

			int first = -1;
			int last = -1;

			boolean bSpacePending = false;
			final int length = keyword_.length();
			for (int i = 0; i < length; ++i)
			{
				S a = keyword_.symbolAt(i);

				if (isPunctuationExtensionEnabled && classifier.isPunctuation(a))
				{
					a = classifier.getSpace();

					// Although getSpace() may return null, it should never do so here
					// because isPunctuationExtensionEnabled implies getSpace() returns
					// a non-null symbol.
					assert a != null;
				}

				if (isCaseExtensionEnabled)
				{
					a = classifier.toLowerCase(a);
				}

				if (space != null && classifier.isWhitespace(a))
				{
					bSpacePending = true;
				}
				else
				{
					if (first == -1)
					{
						first = i;
					}
					else if (bSpacePending)
					{
						requiredSymbols.add(space);
					}
					bSpacePending = false;

					requiredSymbols.add(a);

					last = i;
				}
			}

			prefixSize = first;
			suffixSize = length - last - 1;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return String.format("KeywordMetaData [keyword=%s, requiredSymbols=%s, prefixSize=%s, suffixSize=%s]", keyword, requiredSymbols, prefixSize,
					suffixSize);
		}
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private void _reset()
	{
		this.numStates = 0;
		this.nextMoveFunction = null;
		this.gotoFunction = null;
		this.failureFunction = null;
		this.outputFunction = null;
		this.bNotifyLongestMatch = false;
		this.bNotifyMostPreciseMatch = false;
		this.bNotifyRawSymbols = false;
	}

	/**
	 * Filters a matched keyword.
	 *
	 * @param keyword_
	 *            the keyword to filter
	 * @param textSource_
	 *            the text source in which the keyword was matched
	 * @param start_
	 *            the keyword starting position (i.e., position of first matched symbol
	 * @param end_
	 *            the keyword ending position (i.e., position of after last symbol of match)
	 *
	 * @return Returns {@code true} if the keyword is accepted; {@code false} otherwise.
	 */
	private boolean accept(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_)
	{
		return acceptWordBreak(textSource_, start_)
				&& acceptCase(keyword_, textSource_, start_, end_)
				&& acceptPunctuation(keyword_, textSource_, start_, end_);
	}

	/**
	 * Filters a keyword based on symbol case.
	 *
	 * @param keyword_
	 *            the keyword to filter
	 * @param textSource_
	 *            the text source in which the keyword was matched
	 * @param start_
	 *            the keyword starting position (i.e., position of first matched symbol
	 * @param end_
	 *            the keyword ending position (i.e., position of after last symbol of match)
	 *
	 * @return Returns {@code true} if the keyword is accepted; {@code false} otherwise.
	 */
	private boolean acceptCase(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_)
	{
		assert keyword_ != null;
		assert textSource_ != null;
		assert start_ >= 0;
		assert end_ > start_;

		if (isCaseExtensionEnabled && keyword_.isCaseSensitive())
		{
			final ITailBuffer<S> buffer = textSource_.getTailBuffer();

			int iKeywordPostion = 0; // position of symbol in keyword
			long iMatchedPosition = start_; // position of matched symbol in buffer

			final int keywordLength = keyword_.length();
			while (iKeywordPostion < keywordLength && iMatchedPosition < end_)
			{
				S matchedSymbol = buffer.symbolAt(iMatchedPosition++);
				while (matchedSymbol == null && iMatchedPosition < end_)
				{
					matchedSymbol = buffer.symbolAt(iMatchedPosition++);
				}

				if (matchedSymbol == null)
				{
					return false;
				}

				final S keywordSymbol = keyword_.symbolAt(iKeywordPostion++);
				if (!matchedSymbol.equals(keywordSymbol))
				{
					// It might be the case that the symbols are not equal because we of a fuzzy punctuation
					if (isPunctuationExtensionEnabled && matchedSymbol.equals(space) && classifier.isPunctuation(keywordSymbol))
					{
						continue;
					}
					return false;
				}
			}
		}

		return true;
	}

	private boolean acceptLength(final IMatch<S> match_, final long lengthThreshold_)
	{
		return !bNotifyLongestMatch
				|| (match_.getEnd() - match_.getStart()) >= lengthThreshold_;
	}

	private boolean acceptPrecision(final IMatch<S> match_, final double precisionThreshold_)
	{
		return !bNotifyMostPreciseMatch
				|| match_.getPrecision() >= precisionThreshold_ - PRECISION_DELTA;
	}

	/**
	 * Filters a keyword based on punctuation symbols.
	 *
	 * @param keyword_
	 *            the keyword to filter
	 * @param textSource_
	 *            the text source in which the keyword was matched
	 * @param start_
	 *            the keyword starting position (i.e., position of first matched symbol
	 * @param end_
	 *            the keyword ending position (i.e., position of after last symbol of match)
	 *
	 * @return Returns {@code true} if the keyword is accepted; {@code false} otherwise.
	 */
	private boolean acceptPunctuation(final IKeyword<S> keyword_, final ITextSource<S> textSource_, final long start_, final long end_)
	{
		assert keyword_ != null;
		assert textSource_ != null;
		assert start_ >= 0;
		assert end_ > start_;

		if (isPunctuationExtensionEnabled && keyword_.isPunctuationSensitive())
		{
			final ITailBuffer<S> rawBuffer = textSource_.getRawTailBuffer();

			int iKeywordPostion = 0; // position of symbol in keyword
			long iMatchedPosition = start_; // position of matched symbol in buffer
			boolean pOptionalWhitespace = false; // true if we may need to consume optional whitespace.

			final int keywordLength = keyword_.length();
			while (iKeywordPostion < keywordLength)
			{
				if (iMatchedPosition >= end_)
				{
					return false;
				}

				final S keywordSymbol = keyword_.symbolAt(iKeywordPostion++);

				if (classifier.isWhitespace(keywordSymbol))
				{
					// Must match one or more whitespace symbols in buffer.
					final int n = skipWhite(rawBuffer, iMatchedPosition);

					if (n == 0)
					{
						return false;
					}

					iMatchedPosition += n;
					pOptionalWhitespace = false;
				}
				else if (classifier.isPunctuation(keywordSymbol))
				{
					// Must match zero or more whitespace symbols, followed by an exact punctuation symbol,
					// followed by zero or more whitespace symbols.
					iMatchedPosition += skipWhite(rawBuffer, iMatchedPosition);

					final S matchedSymbol = rawBuffer.symbolAt(iMatchedPosition++);
					if (!(iMatchedPosition <= end_ && keywordSymbol.equals(matchedSymbol)))
					{
						return false;
					}

					pOptionalWhitespace = true;
				}
				else
				{
					// Must match the keyword symbol, subject to case-sensitivity options.
					// However, we don't really need to verify this here since we already
					// know that we're dealing with a keyword match and since there is a
					// separate acceptCase() filter.

					if (pOptionalWhitespace)
					{
						iMatchedPosition += skipWhite(rawBuffer, iMatchedPosition);
					}
					++iMatchedPosition;
					pOptionalWhitespace = false;
				}
			}
		}

		return true;
	}

	/**
	 * Determines if a whole word could begin at the specified start position.
	 *
	 * @param textSource_
	 *            the text source
	 * @param start_
	 *            the start position
	 *
	 * @return Returns {@code true} if a whole word could begin at the start position; {@code false} otherwise.
	 */
	private boolean acceptWordBreak(final ITextSource<S> textSource_, final long start_)
	{
		assert textSource_ != null;
		assert start_ >= 0;

		final ITailBuffer<S> buffer = textSource_.getTailBuffer();

		long i = start_;
		S previousSymbol = null;
		while (previousSymbol == null && i > buffer.start())
		{
			previousSymbol = buffer.symbolAt(--i);
		}

		return isWordBreak(previousSymbol);
	}

	/**
	 * Adds match notification (i.e., {@link IMatch} instances) to the specified queue. When multiple keywords are
	 * specified, notifications are added in order defined by {@link #keywordComparator}. By definition, all keywords
	 * have the same ending position.
	 *
	 * @param keywords_
	 *            specifies the keywords to report.
	 * @param textSource_
	 *            specifies the text source being matched, positioned at the point where the match recognition occurred.
	 * @param matches_
	 *            specifies queue to which the collected matches will be added. Must be non-null.
	 *
	 * @throws IOException
	 *             if an error occurs reading additional input symbols
	 */
	private void collectMatches(final IKeywords<S> keywords_, final ITextSource<S> textSource_, final Deque<IMatch<S>> matches_) throws IOException
	{
		assert keywords_ != null;
		assert textSource_ != null;
		assert matches_ != null;

		final long matchPosition = textSource_.getPosition();

		final Collection<IKeyword<S>> orderedKeywords;
		if (keywords_.size() > 1)
		{
			orderedKeywords = new TreeSet<>(keywordComparator);
			orderedKeywords.addAll(keywords_);
		}
		else
		{
			orderedKeywords = keywords_;
		}

		// Perform preliminary filtering (i.e., all filtering that depends only on a match
		// itself and not the a relationship between match candidates) and record longest
		// match length for subsequent filtering.
		final List<Match<S>> candidateMatches = new ArrayList<>(orderedKeywords.size());
		long lengthThreshold = 0;
		for (final IKeyword<S> keyword : orderedKeywords)
		{
			final KeywordMetaData metaData = new KeywordMetaData(keyword);

			final long start = findStart(metaData, textSource_, matchPosition);
			final long end = findEnd(metaData, textSource_, matchPosition);

			if (accept(keyword, textSource_, start, end))
			{
				final long length = end - start;
				if (length > lengthThreshold)
				{
					lengthThreshold = length;
				}

				final Match<S> match = new Match<>(this, keyword, start, end);
				final double precision = matchPrecisionFunction.eval(keyword, textSource_, start, end);
				match.setPrecision(precision);

				candidateMatches.add(match);
			}
		}

		// Perform length filtering and record the highest match precision for subsequent
		// filtering.
		final Iterator<Match<S>> iterator = candidateMatches.iterator();
		double precisionThreshold = IMatchPrecisionFunction.MIN_PRECISION;
		while (iterator.hasNext())
		{
			final IMatch<S> match = iterator.next();
			if (acceptLength(match, lengthThreshold))
			{
				final double precision = match.getPrecision();
				if (precision > precisionThreshold)
				{
					precisionThreshold = precision;
				}
			}
			else
			{
				iterator.remove();
			}
		}

		// Filter candidate matches precision and add accepted matches to the
		// output queue.
		for (final Match<S> match : candidateMatches)
		{
			if (acceptPrecision(match, precisionThreshold))
			{
				if (bNotifyRawSymbols)
				{
					final S[] rawSymols = findRawSymbols(textSource_, match.getStart(), match.getEnd());
					match.setRawSymbols(rawSymols);
				}

				matches_.add(match);
			}
		}
	}

	private int createState()
	{
		return numStates++;
	}

	/**
	 * Enters a new keyword into machine state. This method will always create at least one new state and may create up
	 * to one new state per keyword symbol. The goto function is updated with a new transition for new state. The output
	 * function is updated to add the keyword to the last state created.
	 *
	 * @param keyword_
	 *            the keyword to be entered.
	 *
	 * @throws IllegalArgumentException
	 *             if a keyword contains non-normalized whitespace symbols. See {@link #buildGotoFunction(IKeywords)}
	 *             for further discussion.
	 */
	private void enter(final IKeyword<S> keyword_)
	{
		assert keyword_ != null;

		verifyNormalWhitespace(keyword_);

		final KeywordMetaData metaData = new KeywordMetaData(keyword_);
		final List<S> symbols = metaData.getRequiredSymbols();

		int state = IGotoFunction.START_STATE;
		boolean bCheckSymbol = true;

		for (final S a : symbols)
		{
			if (bCheckSymbol)
			{
				final int nextState = gotoFunction.eval(state, a);
				if (nextState != IGotoFunction.NO_STATE)
				{
					state = nextState;
					continue;
				}

				bCheckSymbol = false;
			}

			final int nextState = createState();
			gotoFunction.put(state, a, nextState);
			state = nextState;
		}

		outputFunction.put(state, keyword_);
	}

	/**
	 * Finds the raw symbols from a text source for the range {@code [start_, end_)}.
	 *
	 * @param textSource_
	 *            specifies the text file containing desired symbols.
	 * @param start_
	 *            specifies the starting position (closed) of the range.
	 * @param end_
	 *            specifies the ending position (open) of the range.
	 *
	 * @return Returns an array of symbols, or {@code null} if the specified range is empty.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the specified range is outside the buffered range of the text source.
	 */
	private S[] findRawSymbols(final ITextSource<S> textSource_, final long start_, final long end_)
	{
		assert textSource_ != null;
		assert start_ >= 0;
		assert end_ >= start_;

		final ITailBuffer<S> rawBuffer = textSource_.getRawTailBuffer();
		assert rawBuffer != null;

		if (start_ < rawBuffer.start() || rawBuffer.end() < end_)
		{
			final String msg = String.format(MSG_RANGE_OUTSIDE_BUFFER,
					start_, end_, rawBuffer.start(), rawBuffer.end());
			throw new IndexOutOfBoundsException(msg);
		}

		final int length = (int) (end_ - start_);
		if (length == 0)
		{
			return null;
		}

		final S exemplar = rawBuffer.symbolAt(start_);
		@SuppressWarnings("unchecked")
		final S[] symbols = (S[]) Array.newInstance(exemplar.getClass(), length);

		for (int i = 0; i < length; i++)
		{
			symbols[i] = rawBuffer.symbolAt(start_ + i);
		}

		return symbols;
	}

	/**
	 * Finds the start position of the specified keyword.
	 *
	 * @param metaData_
	 *            specifies the meta data for the keyword for which the start position is requested.
	 * @param textSource_
	 *            specifies the text source in which the keyword was just matched.
	 * @param matchPosition_
	 *            specifies the text source position where the match was recognized.
	 *
	 * @return Returns the start position of the keyword.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if start of the the keyword is before the start buffered range of the text source.
	 */
	private long findStart(final KeywordMetaData metaData_, final ITextSource<S> textSource_, final long matchPosition_)
	{
		assert metaData_ != null;
		assert textSource_ != null;

		final List<S> symbols = metaData_.getRequiredSymbols();
		final int length = symbols.size();

		// A zero-length keyword would start at the end current text source position.
		long start = matchPosition_;

		// Now walk backward in the effective symbols until we have found the right number of
		// non-null symbols.
		final ITailBuffer<S> buffer = textSource_.getTailBuffer();
		for (int i = 0; i < length; i++)
		{
			while (buffer.symbolAt(--start) == null)
			{
				// skip "extra" symbol
			}
		}

		// Now walk even further backward, if possible, to match any optional prefix characters.
		final ITailBuffer<S> rawBuffer = textSource_.getRawTailBuffer();
		final IKeyword<S> keyword = metaData_.getKeyword();
		int toMatch = metaData_.getPrefixSize();
		while (toMatch > 0 && start > rawBuffer.start())
		{
			// Get the next optional prefix character to match, working right to left
			// within the prefix symbols.
			final S a = keyword.symbolAt(--toMatch);

			// If the optional symbol is whitespace, expand to include leading whitespace, if any.
			if (classifier.isWhitespace(a))
			{
				while (start > rawBuffer.start() && classifier.isWhitespace(rawBuffer.symbolAt(start - 1)))
				{
					--start;
				}
			}

			// If the optional symbol is punctuation, expand to include matching punctuation symbol, if any.
			else if (classifier.isPunctuation(a) && a.equals(rawBuffer.symbolAt(start - 1)))
			{
				--start;
			}
		}

		return start;
	}

	/**
	 * Finds the end position of the specified keyword.
	 *
	 * @param metaData_
	 *            specifies the meta data for the keyword for which the end position is requested.
	 * @param textSource_
	 *            specifies the text source in which the keyword was just matched.
	 * @param matchPosition_
	 *            specifies the text source position where the match was recognized.
	 *
	 * @return Returns the end position of the keyword.
	 *
	 * @throws IOException
	 *             if an error occurs reading additonal input symbols
	 *
	 * @throws IndexOutOfBoundsException
	 *             if start of the the keyword is before the start buffered range of the text source.
	 */
	private long findEnd(final KeywordMetaData metaData_, final ITextSource<S> textSource_, final long matchPosition_) throws IOException
	{
		assert metaData_ != null;
		assert textSource_ != null;

		long end = matchPosition_;

		// Attempt to walk further forward, if possible, to match any optional suffix characters.
		if (space != null)
		{
			int toMatch = metaData_.getSuffixSize();
			if (toMatch > 0)
			{
				final IKeyword<S> keyword = metaData_.getKeyword();
				final int length = keyword.length();
				final ITailBuffer<S> rawBuffer = textSource_.getRawTailBuffer();
				final long savedPosition = textSource_.getPosition();

				while (toMatch > 0 && isWhitespaceAt(textSource_, end))
				{
					// Count raw white space symbols at end position.
					int nWhite = 0;
					while (end + nWhite < rawBuffer.end() && classifier.isWhitespace(rawBuffer.symbolAt(end + nWhite)))
					{
						++nWhite;
					}

					// Get the next optional suffix character to match, working left to right
					// within the suffix symbols.
					final S a = keyword.symbolAt(length - toMatch--);

					// If the optional symbol is whitespace, expand to include trailing whitespace, if any.
					if (classifier.isWhitespace(a))
					{
						end += nWhite;
					}

					// If the optional symbol is punctuation, expand to include matching punctuation symbol, if any.
					else if (classifier.isPunctuation(a) && end + nWhite < rawBuffer.end() && a.equals(rawBuffer.symbolAt(end + nWhite)))
					{
						end += nWhite + 1;
					}
				}
				textSource_.setPosition(savedPosition);
			}
		}

		return end;
	}

	/**
	 * Tests to determine if there is a whitespace symbols available in the a text source at a specific position. If the
	 * specified position has not yet been read from the text source but that position does contain a whitespace symbol,
	 * the symbol is read from the text source (making it available in the tail buffer).
	 *
	 * @param textSource_
	 *            specifies the text source
	 * @param iPosition_
	 *            specifies the position to test, must be in a position in already in the text source's tail buffer, or
	 *            just at the end of the tail buffer.
	 *
	 * @return Returns true if there is a whitespace symbol available at the specified position.
	 *
	 * @throws IOException
	 *             if an error occurs reading reading the space symbol from the text source.
	 */
	private boolean isWhitespaceAt(final ITextSource<S> textSource_, final long iPosition_) throws IOException
	{
		assert textSource_ != null;
		assert space != null;

		final ITailBuffer<S> buffer = textSource_.getTailBuffer();

		assert buffer.start() <= iPosition_ && iPosition_ <= buffer.end();

		boolean result = false;

		if (iPosition_ < buffer.end())
		{
			// The requested position is already available in the tail buffer. Test the symbol
			// at that position to see if it is a space symbol.
			result = classifier.isWhitespace(buffer.symbolAt(iPosition_));
		}
		else if (!textSource_.isEof() && classifier.isWhitespace(textSource_.peek()))
		{
			// The requested position is not yet in the tail buffer, but it is the next position
			// to be read AND that position contains whitespace. So, we'll read that symbol,
			// making it available in the tail buffer (and the corresponding raw input symbols
			// available in the raw buffer).
			textSource_.read();
			result = true;
		}

		return result;
	}

	/**
	 * Determines if a symbol is classified as a word-break character. If word-break extensions are disabled, every
	 * symbol is considered to be a word-break character. Otherwise, the result is determined by {@link #classifier}.
	 *
	 * @param a_
	 *            the symbol to classify; use {@code null} to specify start-of-file or end-of-file.
	 *
	 * @return Return {@code true} if the specified symbol is a word break symbol; {@code false} otherwise.
	 */
	private boolean isWordBreak(final S a_)
	{
		return !isWordBreakExtensionEnabled || classifier.isWordBreak(a_);
	}

	/**
	 * Determines the next move of the matching machine.
	 *
	 * <p>
	 * If {@link #nextMoveFunction} is available, that function is used to directly determine the next move. Otherwise,
	 * the {@link #gotoFunction} and {@link #failureFunction} are used to compute the next move.
	 * </p>
	 *
	 * @param state_
	 *            specifies the current state of the machine.
	 * @param a_
	 *            specifies the input symbol driving the move.
	 *
	 * @return Returns the state to which the machine should move.
	 */
	private int move(final int state_, final S a_)
	{
		if (nextMoveFunction != null)
		{
			return nextMoveFunction.eval(state_, a_);
		}

		int state = state_;
		int s = gotoFunction.eval(state, a_);

		while (s == IGotoFunction.NO_STATE)
		{
			state = failureFunction.eval(state);
			s = gotoFunction.eval(state, a_);
		}

		return s;
	}

	private IllegalArgumentException nonNormalWhitespaceException(final String format_, final IKeyword<S> keyword_)
	{
		final String msg = String.format(format_, keyword_);
		return new IllegalArgumentException(msg);
	}

	private int skipWhite(final ITailBuffer<S> buffer_, final long iPosition_)
	{
		assert buffer_ != null;
		assert buffer_.start() <= iPosition_ && iPosition_ <= buffer_.end();

		final long end = buffer_.end();
		int n = 0;
		for (long i = iPosition_; i < end && classifier.isWhitespace(buffer_.symbolAt(i)); ++i)
		{
			++n;
		}
		return n;
	}

	/**
	 * @param keyword_
	 */
	private void verifyNormalWhitespace(final IKeyword<S> keyword_)
	{
		assert keyword_ != null;

		if (space != null)
		{
			final int length = keyword_.length();
			boolean haveWhitespace = false;
			for (int i = 0; i < length; ++i)
			{
				final S a = keyword_.symbolAt(i);

				if (classifier.isWhitespace(a))
				{
					if (i == 0)
					{
						throw nonNormalWhitespaceException(MSG_LEADING_WHITESPACE, keyword_);
					}

					if (haveWhitespace)
					{
						throw nonNormalWhitespaceException(MSG_CONSECUTIVE_WHITESPACE, keyword_);
					}

					haveWhitespace = true;
				}
				else
				{
					haveWhitespace = false;
				}
			}

			if (haveWhitespace)
			{
				throw nonNormalWhitespaceException(MSG_TRAILING_WHITESPACE, keyword_);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("AhoCorasickMachine [classifier=%s]", classifier);
	}
}
