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
package com.dell.mensa.impl.character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IFailureFunction;
import com.dell.mensa.IGotoFunction;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IMatchListener;
import com.dell.mensa.INextMoveFunction;
import com.dell.mensa.IOutputFunction;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.generic.AbstractKeyword;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Keywords;
import com.dell.mensa.impl.generic.Match;
import com.dell.mensa.impl.generic.MatchCollector;
import com.dell.mensa.impl.generic.OrderedKeywords;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterAhoCorasicMachineTest extends AbstractCharacterAhoCorasickMachineTestBase
{
	// =========================================================================
	// Fixture
	// =========================================================================

	private static final int s0 = 0;
	private static final int s1 = 1;
	private static final int s2 = 2;
	private static final int s3 = 3;
	private static final int s4 = 4;
	private static final int s5 = 5;
	private static final int s6 = 6;
	private static final int s7 = 7;
	private static final int s8 = 8;
	private static final int s9 = 9;

	private final CharacterKeyword he = new CharacterKeyword("he");
	private final CharacterKeyword she = new CharacterKeyword("she");
	private final CharacterKeyword his = new CharacterKeyword("his");
	private final CharacterKeyword hers = new CharacterKeyword("hers");

	private IKeywords<Character> figure1Keywords;

	private AhoCorasickMachine<Character> machine;
	private AhoCorasickMachine<Character> machineEx;

	@Before
	public void setUp()
	{
		final IFactory<Character> factory = new CharacterFactory();
		machine = new AhoCorasickMachine<>(factory);
		machineEx = new CharacterAhoCorasickMachine();

		figure1Keywords = new OrderedKeywords<>();
		figure1Keywords.add(he);
		figure1Keywords.add(she);
		figure1Keywords.add(his);
		figure1Keywords.add(hers);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	/**
	 * Test method for {@link com.dell.mensa.impl.generic.AhoCorasickMachine#AhoCorasickMachine(IFactory)}.
	 */
	@SuppressWarnings(
	{ "unused", "static-method" })
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testBuilder()
	{
		new AhoCorasickMachine<>(null);
	}

	/**
	 * Test method for {@link AhoCorasickMachine#build(IKeywords)}.
	 */
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testBuild_IllegalArgument()
	{
		machine.build(null);
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuild_GotoAlreadyConstructed()
	{
		try
		{
			machine.buildGotoFunction(figure1Keywords);
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.build(figure1Keywords);
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuild_NextMoveAlreadyConstructed()
	{
		try
		{
			machine.buildGotoFunction(figure1Keywords);
			machine.buildFailureFunction();
			machine.buildNextMoveFunction();
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.build(figure1Keywords);
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuild_AlreadyBuilt()
	{
		try
		{
			machine.build(figure1Keywords);
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.build(figure1Keywords);
	}

	@Test
	public void testBuild() throws IOException
	{
		machine.build(figure1Keywords);

		Assert.assertNull(machine.getGotoFunction());
		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNotNull(machine.getNextMoveFunction());

		verifyFigure1Match(machine);
	}

	/**
	 * Test method for {@link AhoCorasickMachine#buildGotoFunction(IKeywords)}.
	 */
	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testBuildGotoFunction_IllegalArgument()
	{
		machine.buildGotoFunction(null);
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildGotoFunction_GotoAlreadyConsructed()
	{
		final IKeywords<Character> keywords = new Keywords<>();

		try
		{
			machine.buildGotoFunction(keywords);
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.buildGotoFunction(keywords);
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildGotoFunction_NextMoveAlreadyConsructed()
	{
		final IKeywords<Character> keywords = new Keywords<>();

		try
		{
			machine.buildGotoFunction(keywords);
			machine.buildFailureFunction();
			machine.buildNextMoveFunction();
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.buildGotoFunction(keywords);
	}

	@Test
	public void testBuildGotoFunction_NoKeywords()
	{
		final IKeywords<Character> keywords = new Keywords<>();

		final IGotoFunction<Character> gotoFunction = machine.buildGotoFunction(keywords);
		Assert.assertNotNull(gotoFunction);
		Assert.assertSame(gotoFunction, machine.getGotoFunction());

		Assert.assertNotNull(machine.getOutputFunction());

		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNull(machine.getNextMoveFunction());

		Assert.assertEquals(1, machine.getNumStates());

		final IEdgeMap<Character> edgeMap = gotoFunction.getEdgeMap(IGotoFunction.START_STATE);
		Assert.assertNotNull(edgeMap);
		Assert.assertEquals(1, edgeMap.size());
		Assert.assertEquals(IGotoFunction.START_STATE, edgeMap.get(null));
	}

	@Test
	public void testBuildGotoFunction_Paper_Figure1()
	{
		final IGotoFunction<Character> gotoFunction = machine.buildGotoFunction(figure1Keywords);
		Assert.assertNotNull(gotoFunction);
		Assert.assertSame(gotoFunction, machine.getGotoFunction());

		Assert.assertNotNull(machine.getOutputFunction());

		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNull(machine.getNextMoveFunction());

		Assert.assertEquals(10, machine.getNumStates());

		// Verify symbols
		// --------------
		final Collection<Character> symbols = gotoFunction.symbols();
		Assert.assertNotNull(symbols);
		Assert.assertEquals(6, symbols.size());
		Assert.assertTrue(symbols.contains('e'));
		Assert.assertTrue(symbols.contains('i'));
		Assert.assertTrue(symbols.contains('h'));
		Assert.assertTrue(symbols.contains('r'));
		Assert.assertTrue(symbols.contains('s'));
		Assert.assertTrue(symbols.contains(null));

		// Verify edges
		// ------------
		final IEdgeMap<Character> e0 = gotoFunction.getEdgeMap(s0);
		Assert.assertEquals(3, e0.size());
		Assert.assertEquals(s0, e0.get(null));
		Assert.assertEquals(s1, e0.get('h'));
		Assert.assertEquals(s3, e0.get('s'));

		final IEdgeMap<Character> e1 = gotoFunction.getEdgeMap(s1);
		Assert.assertEquals(2, e1.size());
		Assert.assertEquals(s2, e1.get('e'));
		Assert.assertEquals(s6, e1.get('i'));

		final IEdgeMap<Character> e2 = gotoFunction.getEdgeMap(s2);
		Assert.assertEquals(1, e2.size());
		Assert.assertEquals(s8, e2.get('r'));

		final IEdgeMap<Character> e3 = gotoFunction.getEdgeMap(s3);
		Assert.assertEquals(1, e3.size());
		Assert.assertEquals(s4, e3.get('h'));

		final IEdgeMap<Character> e4 = gotoFunction.getEdgeMap(s4);
		Assert.assertEquals(1, e4.size());
		Assert.assertEquals(s5, e4.get('e'));

		final IEdgeMap<Character> e5 = gotoFunction.getEdgeMap(s5);
		Assert.assertNull(e5);

		final IEdgeMap<Character> e6 = gotoFunction.getEdgeMap(s6);
		Assert.assertEquals(1, e6.size());
		Assert.assertEquals(s7, e6.get('s'));

		final IEdgeMap<Character> e7 = gotoFunction.getEdgeMap(s7);
		Assert.assertNull(e7);

		final IEdgeMap<Character> e8 = gotoFunction.getEdgeMap(s8);
		Assert.assertEquals(1, e8.size());
		Assert.assertEquals(s9, e8.get('s'));

		final IEdgeMap<Character> e9 = gotoFunction.getEdgeMap(s9);
		Assert.assertNull(e9);
	}

	/**
	 * Test method for {@link AhoCorasickMachine#buildFailureFunction()}.
	 */

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildFailureFunction_NoGotoConstructed()
	{
		machine.buildFailureFunction();
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildFailureFunction_FailureAlreadyConsructed()
	{
		final IKeywords<Character> keywords = new Keywords<>();

		try
		{
			machine.buildGotoFunction(keywords);
			machine.buildFailureFunction();
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}

		machine.buildFailureFunction();
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildFailureFunction_NextMoveAlreadyConsructed()
	{
		final IKeywords<Character> keywords = new Keywords<>();

		try
		{
			machine.buildGotoFunction(keywords);
			machine.buildFailureFunction();
			machine.buildNextMoveFunction();
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}

		machine.buildFailureFunction();
	}

	@Test()
	public void testBuildFailureFunction_NoKeywords()
	{
		final IKeywords<Character> keywords = new Keywords<>();
		machine.buildGotoFunction(keywords);

		final IFailureFunction failureFunction = machine.buildFailureFunction();
		Assert.assertNotNull(failureFunction);
		Assert.assertEquals(failureFunction, machine.getFailureFunction());

		Assert.assertNotNull(machine.getGotoFunction());
		Assert.assertNull(machine.getNextMoveFunction());

		Assert.assertEquals(0, failureFunction.getStates().size());
	}

	@Test
	public void testBuildFailureFunction_Paper_Figure1()
	{
		machine.buildGotoFunction(figure1Keywords);

		final IFailureFunction failureFunction = machine.buildFailureFunction();
		Assert.assertNotNull(failureFunction);
		Assert.assertEquals(failureFunction, machine.getFailureFunction());

		Assert.assertNotNull(machine.getGotoFunction());
		Assert.assertNull(machine.getNextMoveFunction());

		// Verify failure function
		// -----------------------
		Assert.assertEquals(IGotoFunction.NO_STATE, failureFunction.eval(s0));

		Assert.assertEquals(s0, failureFunction.eval(s1));
		Assert.assertEquals(s0, failureFunction.eval(s2));
		Assert.assertEquals(s0, failureFunction.eval(s3));
		Assert.assertEquals(s1, failureFunction.eval(s4));
		Assert.assertEquals(s2, failureFunction.eval(s5));
		Assert.assertEquals(s0, failureFunction.eval(s6));
		Assert.assertEquals(s3, failureFunction.eval(s7));
		Assert.assertEquals(s0, failureFunction.eval(s8));
		Assert.assertEquals(s3, failureFunction.eval(s9));

		// Verify output function
		// ----------------------
		final IOutputFunction<Character> outputFunction = machine.getOutputFunction();
		Assert.assertNotNull(outputFunction);

		Assert.assertNull(outputFunction.output(s0));

		Assert.assertNull(outputFunction.output(s1));

		Assert.assertNotNull(outputFunction.output(s2));
		Assert.assertEquals(1, outputFunction.output(s2).size());
		Assert.assertTrue(outputFunction.output(s2).contains(he));

		Assert.assertNull(outputFunction.output(s3));

		Assert.assertNull(outputFunction.output(s4));

		Assert.assertNotNull(outputFunction.output(s5));
		Assert.assertEquals(2, outputFunction.output(s5).size());
		Assert.assertTrue(outputFunction.output(s5).contains(she));
		Assert.assertTrue(outputFunction.output(s5).contains(he));

		Assert.assertNull(outputFunction.output(s6));

		Assert.assertNotNull(outputFunction.output(s7));
		Assert.assertEquals(1, outputFunction.output(s7).size());
		Assert.assertTrue(outputFunction.output(s7).contains(his));

		Assert.assertNull(outputFunction.output(s8));

		Assert.assertNotNull(outputFunction.output(s9));
		Assert.assertEquals(1, outputFunction.output(s9).size());
		Assert.assertTrue(outputFunction.output(s9).contains(hers));
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildNextMoveFunction_NoGotoFunction()
	{
		machine.buildNextMoveFunction();
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildNextMoveFunction_NoFailureFunctionFunction()
	{
		try
		{
			machine.buildGotoFunction(figure1Keywords);
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.buildNextMoveFunction();
	}

	@Test(expected = java.lang.IllegalStateException.class)
	public void testBuildNextMoveFunction_NextMoveAlreadyConstructed()
	{
		try
		{
			machine.buildGotoFunction(figure1Keywords);
			machine.buildFailureFunction();
			machine.buildNextMoveFunction();
		}
		catch (final IllegalStateException e)
		{
			unexpected(e);
		}
		machine.buildNextMoveFunction();
	}

	@Test
	public void testBuildNextMoveFunction_Paper_Figure1()
	{
		machine.buildGotoFunction(figure1Keywords);
		machine.buildFailureFunction();

		final INextMoveFunction<Character> nextMoveFunction = machine.buildNextMoveFunction();
		Assert.assertNotNull(nextMoveFunction);

		Assert.assertNull(machine.getGotoFunction());
		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNotNull(machine.getNextMoveFunction());

		Assert.assertEquals(10, machine.getNumStates());

		// Verify symbols
		// --------------
		final Collection<Character> symbols = nextMoveFunction.symbols();
		Assert.assertNotNull(symbols);
		Assert.assertEquals(6, symbols.size());
		Assert.assertTrue(symbols.contains('e'));
		Assert.assertTrue(symbols.contains('i'));
		Assert.assertTrue(symbols.contains('h'));
		Assert.assertTrue(symbols.contains('r'));
		Assert.assertTrue(symbols.contains('s'));
		Assert.assertTrue(symbols.contains(null));

		// Verify edges
		// ------------
		final IEdgeMap<Character> e0 = nextMoveFunction.getEdgeMap(s0);
		Assert.assertNotNull(e0);
		Assert.assertEquals(3, e0.size());
		Assert.assertEquals(s1, e0.get('h'));
		Assert.assertEquals(s3, e0.get('s'));
		Assert.assertEquals(s0, e0.get(null));

		final IEdgeMap<Character> e1 = nextMoveFunction.getEdgeMap(s1);
		Assert.assertNotNull(e1);
		Assert.assertEquals(5, e1.size());
		Assert.assertEquals(s2, e1.get('e'));
		Assert.assertEquals(s6, e1.get('i'));
		Assert.assertEquals(s1, e1.get('h'));
		Assert.assertEquals(s3, e1.get('s'));
		Assert.assertEquals(s0, e1.get(null));

		final IEdgeMap<Character> e2 = nextMoveFunction.getEdgeMap(s2);
		Assert.assertNotNull(e2);
		Assert.assertEquals(4, e2.size());
		Assert.assertEquals(s8, e2.get('r'));
		Assert.assertEquals(s1, e2.get('h'));
		Assert.assertEquals(s3, e2.get('s'));
		Assert.assertEquals(s0, e2.get(null));

		final IEdgeMap<Character> e3 = nextMoveFunction.getEdgeMap(s3);
		Assert.assertNotNull(e3);
		Assert.assertEquals(3, e3.size());
		Assert.assertEquals(s4, e3.get('h'));
		Assert.assertEquals(s3, e3.get('s'));
		Assert.assertEquals(s0, e3.get(null));

		final IEdgeMap<Character> e4 = nextMoveFunction.getEdgeMap(s4);
		Assert.assertNotNull(e4);
		Assert.assertEquals(5, e4.size());
		Assert.assertEquals(s5, e4.get('e'));
		Assert.assertEquals(s6, e4.get('i'));
		Assert.assertEquals(s1, e4.get('h'));
		Assert.assertEquals(s3, e4.get('s'));
		Assert.assertEquals(s0, e4.get(null));

		final IEdgeMap<Character> e5 = nextMoveFunction.getEdgeMap(s5);
		Assert.assertNotNull(e5);
		Assert.assertEquals(4, e5.size());
		Assert.assertEquals(s8, e5.get('r'));
		Assert.assertEquals(s1, e5.get('h'));
		Assert.assertEquals(s3, e5.get('s'));
		Assert.assertEquals(s0, e5.get(null));

		final IEdgeMap<Character> e6 = nextMoveFunction.getEdgeMap(s6);
		Assert.assertNotNull(e6);
		Assert.assertEquals(3, e6.size());
		Assert.assertEquals(s7, e6.get('s'));
		Assert.assertEquals(s1, e6.get('h'));
		Assert.assertEquals(s0, e6.get(null));

		final IEdgeMap<Character> e7 = nextMoveFunction.getEdgeMap(s7);
		Assert.assertNotNull(e7);
		Assert.assertEquals(3, e7.size());
		Assert.assertEquals(s4, e7.get('h'));
		Assert.assertEquals(s3, e7.get('s'));
		Assert.assertEquals(s0, e7.get(null));

		final IEdgeMap<Character> e8 = nextMoveFunction.getEdgeMap(s8);
		Assert.assertNotNull(e8);
		Assert.assertEquals(3, e8.size());
		Assert.assertEquals(s9, e8.get('s'));
		Assert.assertEquals(s1, e8.get('h'));
		Assert.assertEquals(s0, e8.get(null));

		final IEdgeMap<Character> e9 = nextMoveFunction.getEdgeMap(s9);
		Assert.assertNotNull(e9);
		Assert.assertEquals(3, e3.size());
		Assert.assertEquals(s4, e9.get('h'));
		Assert.assertEquals(s3, e9.get('s'));
		Assert.assertEquals(s0, e9.get(null));
	}

	@Test(expected = IllegalStateException.class)
	public void testMatch_NoGotoFuction() throws IOException
	{
		verifyFigure1Match(machine);
	}

	@Test(expected = IllegalStateException.class)
	public void testMatch_NoFailureFuction() throws IOException
	{
		machine.buildGotoFunction(figure1Keywords);
		verifyFigure1Match(machine);
	}

	@Test
	public void testMatch_WithGotoAndFailureFunction() throws IOException
	{
		machine.buildGotoFunction(figure1Keywords);
		machine.buildFailureFunction();

		Assert.assertNotNull(machine.getGotoFunction());
		Assert.assertNotNull(machine.getFailureFunction());
		Assert.assertNull(machine.getNextMoveFunction());
		Assert.assertNotNull(machine.getOutputFunction());

		verifyFigure1Match(machine);
	}

	@Test
	public void testMatchEx_WithGotoAndFailureFunction() throws IOException
	{
		machineEx.buildGotoFunction(figure1Keywords);
		machineEx.buildFailureFunction();

		Assert.assertNotNull(machineEx.getGotoFunction());
		Assert.assertNotNull(machineEx.getFailureFunction());
		Assert.assertNull(machineEx.getNextMoveFunction());
		Assert.assertNotNull(machineEx.getOutputFunction());

		verifyFigure1Match(machineEx);
	}

	@Test
	public void testMatch_WithNextMoveFunction() throws IOException
	{
		machine.buildGotoFunction(figure1Keywords);
		machine.buildFailureFunction();
		machine.buildNextMoveFunction();

		Assert.assertNull(machine.getGotoFunction());
		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNotNull(machine.getNextMoveFunction());
		Assert.assertNotNull(machine.getOutputFunction());

		verifyFigure1Match(machine);
	}

	@Test
	public void testMatchEx_WithNextMoveFunction() throws IOException
	{
		machineEx.buildGotoFunction(figure1Keywords);
		machineEx.buildFailureFunction();
		machineEx.buildNextMoveFunction();

		Assert.assertNull(machineEx.getGotoFunction());
		Assert.assertNull(machineEx.getFailureFunction());
		Assert.assertNotNull(machineEx.getNextMoveFunction());
		Assert.assertNotNull(machineEx.getOutputFunction());

		verifyFigure1Match(machineEx);
	}

	@Test
	public void testMatch_Basic() throws IOException
	{
		final String symbols = "Hello World";
		final String head = "\t";
		final String rawSymbols = "HELLO,\n  WORLD";
		final String tail = "\n \n ";

		verifySingleMatch(machineEx, symbols, 0, head, rawSymbols, tail);
	}

	@Test
	public void testMatch_Basic_Punctuation() throws IOException
	{
		final String symbols = "ax (by) cz";
		final String head = "";
		final String rawSymbols = "Ax (By) Cz";
		final String tail = "";

		verifySingleMatch(machineEx, symbols, 0, head, rawSymbols, tail);
	}

	@Test
	public void testMatch_CaseSensitivity() throws IOException
	{
		final String text = "South University, ANN\n  ARBOR, MICHIGAN";
		final ITextSource<Character> textSource = new CharacterStringTextSource(text);

		final IKeywords<Character> keywords = new Keywords<>();

		// Loop eight times to cover all permutations of case-sensitivity on each of
		// three different keywords.
		for (int i = 0; i < 8; i++)
		{
			keywords.clear();

			final IKeyword<Character> southU = createKeyword("South University", (i & 1) != 0);
			keywords.add(southU);

			final IKeyword<Character> annArbor = createKeyword("Ann Arbor", (i & 2) != 0);
			keywords.add(annArbor);

			final IKeyword<Character> michigan = createKeyword("Michigan", (i & 4) != 0);
			keywords.add(michigan);

			machineEx.reset();
			machineEx.build(keywords);

			final List<Match<Character>> expectedMatches = new ArrayList<>();

			// Always match "South University"
			add(expectedMatches, new Match<>(machineEx, southU, 0, 16));

			if (!annArbor.isCaseSensitive())
			{
				// Match "Ann Arbor" when keyword is not case-sensitive
				add(expectedMatches, new Match<>(machineEx, annArbor, 18, 29));
			}
			if (!michigan.isCaseSensitive())
			{
				// Match "Michigan" when keyword is not case-sensitive
				add(expectedMatches, new Match<>(machineEx, michigan, 31, 39));
			}

			verifyMatch(machineEx, textSource, expectedMatches);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testMatch_TextSourceNotOpen() throws IOException
	{
		machine.build(figure1Keywords);

		final ITextSource<Character> textSource = new CharacterStringTextSource("");
		final IMatchListener<Character> listener = new MatchCollector<>();

		machine.match(textSource, listener);
	}

	@Test(expected = IllegalStateException.class)
	public void testMatchIterator_TextSourceNotOpen()
	{
		machine.build(figure1Keywords);

		final ITextSource<Character> textSource = new CharacterStringTextSource("");

		machine.matchIterator(textSource);
	}

	@Test
	public void testReset()
	{
		machine.build(figure1Keywords);

		machine.setNotifyLongestMatch(true);
		machine.setNotifyMostPreciseMatch(true);
		machine.setNotifyRawSymbols(true);

		machine.reset();
		Assert.assertNull(machine.getGotoFunction());
		Assert.assertNull(machine.getFailureFunction());
		Assert.assertNull(machine.getNextMoveFunction());
		Assert.assertNull(machine.getOutputFunction());
		Assert.assertFalse(machine.isNotifyLongestMatch());
		Assert.assertFalse(machine.isNotifyMostPreciseMatch());
		Assert.assertFalse(machine.isNotifyRawSymbols());

		machine.build(figure1Keywords);
	}

	@Test
	public void testGetSet_NotifyLongestMatch()
	{
		Assert.assertFalse(machine.isNotifyLongestMatch());
		machine.setNotifyLongestMatch(true);
		Assert.assertTrue(machine.isNotifyLongestMatch());
		machine.setNotifyLongestMatch(false);
		Assert.assertFalse(machine.isNotifyLongestMatch());
	}

	@Test
	public void testGetSet_NotifyMostPreciseMatch()
	{
		Assert.assertFalse(machine.isNotifyMostPreciseMatch());
		machine.setNotifyMostPreciseMatch(true);
		Assert.assertTrue(machine.isNotifyMostPreciseMatch());
		machine.setNotifyMostPreciseMatch(false);
		Assert.assertFalse(machine.isNotifyMostPreciseMatch());
	}

	@Test
	public void testGetSet_NotifyRawSymbols()
	{
		Assert.assertFalse(machine.isNotifyRawSymbols());
		machine.setNotifyRawSymbols(true);
		Assert.assertTrue(machine.isNotifyRawSymbols());
		machine.setNotifyRawSymbols(false);
		Assert.assertFalse(machine.isNotifyRawSymbols());
	}

	@Test
	public void testMatch_NotifyLongestMatch() throws IOException
	{
		final IKeywords<Character> keywords = new Keywords<>();

		final IKeyword<Character> red = new CharacterKeyword("red");
		keywords.add(red);

		final IKeyword<Character> paint = new CharacterKeyword("paint");
		keywords.add(paint);

		final IKeyword<Character> redPaint = new CharacterKeyword("red paint");
		keywords.add(redPaint);

		machine.reset();
		machine.build(keywords);

		final String text = "They used red paint.";
		final ITextSource<Character> textSource = new CharacterStringTextSource(text);

		final List<Match<Character>> expectedMatches = new ArrayList<>();

		// They used red paint.
		// ----------1==1
		// ----------0==3
		add(expectedMatches, new Match<>(machine, red, 10, 13));

		// They used red paint.
		// ----------1========1
		// ----------0========9
		add(expectedMatches, new Match<>(machine, redPaint, 10, 19));

		// They used red paint.
		// --------------1====1
		// --------------4====9
		final Match<Character> matchPaint = new Match<>(machine, paint, 14, 19);
		add(expectedMatches, matchPaint);

		// By default, all keywords are matched.
		verifyMatch(machine, textSource, expectedMatches);

		// With longest keywords only, "paint" should not match. (But "red" will still match because
		// it has it's own unique ending position.)
		machine.setNotifyLongestMatch(true);
		expectedMatches.remove(matchPaint);
		verifyMatch(machine, textSource, expectedMatches);

		// Turning longest keywords only back off causes "paint" to match again.
		machine.setNotifyLongestMatch(false);
		expectedMatches.add(matchPaint);
		verifyMatch(machine, textSource, expectedMatches);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoLeadingWhitespaceInKeyword()
	{
		do_testNonNormalWhitespace(" foo");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoTrailingWhitespaceInKeyword()
	{
		do_testNonNormalWhitespace("foo ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoConsecutiveWhitespaceInKeyword_1()
	{
		do_testNonNormalWhitespace("foo  bar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoConsecutiveWhitespaceInKeyword_2()
	{
		do_testNonNormalWhitespace("foo\n bar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoConsecutiveWhitespaceInKeyword_3()
	{
		do_testNonNormalWhitespace("foo\t\tbar");
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private static IKeyword<Character> createKeyword(final String keyword_, final boolean bCaseSensitive_)
	{
		return bCaseSensitive_
				? new CharacterKeyword(keyword_, null, AbstractKeyword.CASE_SENSITIVE)
				: new CharacterKeyword(keyword_);
	}

	/**
	 * Verifies the expected matches using a "figure 1" machine using both upper case and lower case input text.
	 *
	 * @param machine_
	 *            specifies a machine that has been build as described in "figure 1" of the paper.
	 *
	 * @throws IOException
	 *             not expected!
	 */
	private void verifyFigure1Match(final AhoCorasickMachine<Character> machine_) throws IOException
	{
		verifyFigure1Match(machine_, false);
		verifyFigure1Match(machine_, true);
	}

	/**
	 * Verifies the expected matches using a "figure 1" machine.
	 *
	 * @param machine_
	 *            specifies a machine that has been build as described in "figure 1" of the paper.
	 *
	 * @param bUpper_
	 *            specifies whether input text should be forced to upper case.
	 *
	 * @throws IOException
	 *             not expected!
	 */
	private void verifyFigure1Match(final AhoCorasickMachine<Character> machine_, final boolean bUpper_) throws IOException
	{
		final boolean isCaseExtensionEnabled = machine_.getClassifier().isCaseExtensionEnabled();
		final boolean isWordBreakExtensionEnabled = machine_.getClassifier().isWordBreakExtensionEnabled();

		String text = "she ushers in his sheet for hers";
		if (bUpper_)
		{
			text = text.toUpperCase(Locale.ENGLISH);
		}

		final ITextSource<Character> textSource = new CharacterStringTextSource(text);

		// Create the expected matches, in the expected order.
		final List<Match<Character>> expectedMatches = new ArrayList<>();

		if (isCaseExtensionEnabled || !bUpper_)
		{
			// she ushers in his sheet for hers
			// 0==3
			add(expectedMatches, new Match<>(machine_, she, 0, 3));

			// she ushers in his sheet for hers
			// _1=3
			add(expectedMatches, new Match<>(machine_, he, 1, 3), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// _____5==8
			add(expectedMatches, new Match<>(machine_, she, 5, 8), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ______6=8
			add(expectedMatches, new Match<>(machine_, he, 6, 8), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ______6===1
			// __________0
			add(expectedMatches, new Match<>(machine_, hers, 6, 10), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ______________1==1
			// ______________4==7
			add(expectedMatches, new Match<>(machine_, his, 14, 17));

			// she ushers in his sheet for hers
			// __________________1==2
			// __________________8==1
			add(expectedMatches, new Match<>(machine_, she, 18, 21), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ___________________1=2
			// ___________________9=1
			add(expectedMatches, new Match<>(machine_, he, 19, 21), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ____________________________2=3
			// ____________________________8=0
			add(expectedMatches, new Match<>(machine_, he, 28, 30), !isWordBreakExtensionEnabled);

			// she ushers in his sheet for hers
			// ____________________________2===3
			// ____________________________8===2
			add(expectedMatches, new Match<>(machine_, hers, 28, 32));
		}

		verifyMatch(machine_, textSource, expectedMatches);
	}

	private void do_testNonNormalWhitespace(final String keyword_)
	{
		final IKeywords<Character> keywords = new Keywords<>();
		keywords.add(new CharacterKeyword(keyword_));

		try
		{
			// This is okay because this machine does not do space normalization.
			machine.build(keywords);
		}
		catch (final IllegalArgumentException e)
		{
			final String msg = String.format("unexpected IllegalArgumentException for keyword: \"%s\"", keyword_);
			Assert.fail(msg);
		}

		machineEx.build(keywords);
	}
}
