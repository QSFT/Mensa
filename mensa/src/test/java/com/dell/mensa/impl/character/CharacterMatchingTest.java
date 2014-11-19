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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.dell.mensa.IFactory;
import com.dell.mensa.IKeywords;
import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.generic.AbstractKeyword;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Keywords;
import com.dell.mensa.impl.generic.Match;
import com.dell.mensa.util.Verify;

/**
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
@RunWith(Parameterized.class)
public class CharacterMatchingTest extends AbstractCharacterAhoCorasickMachineTestBase
{
	// private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

	public static final String ATTR_caseSensitive = "caseSensitive";
	public static final String ATTR_id = "id";
	public static final String ATTR_kwid = "kwid";
	public static final String ATTR_start = "start";
	public static final String ATTR_end = "end";
	public static final String ATTR_enabled = "enabled";
	public static final String ATTR_punctuationSensitive = "punctuationSensitive";
	public static final String ATTR_raw = "raw";

	public static final String TAG_caseExtension = "caseExtension";
	public static final String TAG_expectedMatch = "expectedMatch";
	public static final String TAG_globals = "globals";
	public static final String TAG_head = "head";
	public static final String TAG_keyword = "keyword";
	public static final String TAG_notifyLongestKeywordsOnly = "notifyLongestKeywordsOnly";
	public static final String TAG_notifyMostPreciseMatch = "notifyMostPreciseMatch";
	public static final String TAG_notifyRawSymbols = "notifyRawSymbols";
	public static final String TAG_punctuationExtension = "punctuationExtension";
	public static final String TAG_symbols = "symbols";
	public static final String TAG_tail = "tail";
	public static final String TAG_text = "text";
	public static final String TAG_testCase = "testCase";
	public static final String TAG_wordBreakExtension = "wordBreakExtension";

	// =========================================================================
	// Fixture
	// =========================================================================
	private final TestCase testCase;

	private static class Globals
	{
		private final Element globalsElement;
		private final Map<String, CharacterKeyword> keywordMap;
		private IKeywords<Character> keywords;

		public Globals(final Element globalsElement_)
		{
			super();
			this.globalsElement = globalsElement_;
			this.keywordMap = new HashMap<>();

			final NodeList keywordNodes = globalsElement_.getElementsByTagName(TAG_keyword);
			for (int i = 0; i < keywordNodes.getLength(); ++i)
			{
				final Element keywordElement = (Element) keywordNodes.item(i);
				addKeyword(keywordElement);
			}
		}

		public CharacterKeyword getKeywordByID(final String id_)
		{
			final CharacterKeyword keyword = keywordMap.get(id_);

			if (keyword == null)
			{
				final String msg = String.format("unknown keyword ID: %s", id_);
				Assert.fail(msg);
			}

			return keyword;
		}

		public IKeywords<Character> getKeywords()
		{
			if (keywords == null)
			{
				keywords = new Keywords<>();
				keywords.addAll(keywordMap.values());
			}
			return keywords;
		}

		public boolean isCaseExtensionEnabled()
		{
			return getBoolOption(globalsElement, TAG_caseExtension, true);
		}

		public boolean isNotifyLongestKeywordsOnly()
		{
			return getBoolOption(globalsElement, TAG_notifyLongestKeywordsOnly, false);
		}

		public boolean isNotifyMostPreciseMatch()
		{
			return getBoolOption(globalsElement, TAG_notifyMostPreciseMatch, false);
		}

		public boolean isNotifyRawSymbols()
		{
			return getBoolOption(globalsElement, TAG_notifyRawSymbols, false);
		}

		public boolean isPunctuationExtensionEnabled()
		{
			return getBoolOption(globalsElement, TAG_punctuationExtension, true);
		}

		public boolean isWordBreakExtensionEnabled()
		{
			return getBoolOption(globalsElement, TAG_wordBreakExtension, true);
		}

		private void addKeyword(final Element keywordElement_)
		{
			assert keywordElement_ != null;
			assert keywords == null;

			final String keyword = eval(keywordElement_);

			final Attr idAttr = keywordElement_.getAttributeNode(ATTR_id);
			final String id = idAttr == null ? keyword : idAttr.getValue();
			Verify.notEmpty(id, String.format("id for keyword \"%s\"", keyword));

			if (keywordMap.containsKey(id))
			{
				final String msg = String.format("duplicate keyword ID: %s", id);
				Assert.fail(msg);
			}

			keywordMap.put(id, createKeyword(keywordElement_));
		}
	}

	private static class TestCase
	{
		private final Globals globals;
		private final Element testCaseElement;

		private CharacterAhoCorasickMachine machine;
		public ITextSource<Character> textSource;
		public ISymbolClassifier<Character> classifier;
		private IFactory<Character> factory;

		public TestCase(final Globals globals_, final Element testCaseElement_)
		{
			this.globals = globals_;
			this.testCaseElement = testCaseElement_;
		}

		public ISymbolClassifier<Character> getClassifier()
		{
			if (classifier == null)
			{
				classifier = new CharacterSymbolClassifier(
						isCaseExtensionEnabled(),
						isPunctuationExtensionEnabled(),
						isWordBreakExtensionEnabled());
			}
			return classifier;
		}

		/**
		 * Returns the expected matches. Several configuration are supported, listed in (decreasing) precedence order:
		 *
		 * <ul>
		 *
		 * <li>If {@link CharacterMatchingTest#TAG_symbols} is specified, expect exactly one match of a keyword with
		 * these symbols. By default, the expected raw symbols set to the keyword symbols, but
		 * {@link CharacterMatchingTest#ATTR_raw} may be used to override the expected raw symbols.</li>
		 *
		 * <li>Zero or more {@link CharacterMatchingTest#TAG_expectedMatch} elements explicitly define the expected
		 * matches.</li>
		 *
		 * </ul>
		 *
		 * @return Returns the expected matches.
		 */
		public List<Match<Character>> getExpectedMatches()
		{
			final List<Match<Character>> expectedMatches = new ArrayList<>();

			final Element symbolsElement = getSingleElementByTagName(testCaseElement, TAG_symbols);
			if (symbolsElement != null)
			{
				final CharacterKeyword keyword = createKeyword(symbolsElement);

				final String rawSymbols = getRawSymbols(symbolsElement);

				final long start = eval(testCaseElement, TAG_head).length();
				final long end = start + rawSymbols.length();

				final Match<Character> expectedMatch = createMatch(keyword, start, end);
				expectedMatch.setRawSymbols(createRawSymbols(rawSymbols));

				expectedMatches.add(expectedMatch);
				return expectedMatches;
			}

			final NodeList expectedMatchNodes = testCaseElement.getElementsByTagName(TAG_expectedMatch);
			for (int i = 0; i < expectedMatchNodes.getLength(); ++i)
			{
				final Element expectedMatchElement = (Element) expectedMatchNodes.item(i);

				final CharacterKeyword keyword = globals.getKeywordByID(expectedMatchElement.getAttribute(ATTR_kwid));
				final long start = getIntAttr(expectedMatchElement, ATTR_start);
				final long end = getIntAttr(expectedMatchElement, ATTR_end);

				final Match<Character> expectedMatch = createMatch(keyword, start, end);

				final Attr raw = expectedMatchElement.getAttributeNode(ATTR_raw);
				if (raw != null)
				{
					expectedMatch.setRawSymbols(createRawSymbols(raw.getValue()));
				}

				expectedMatches.add(expectedMatch);
			}

			return expectedMatches;
		}

		/**
		 * @return Returns a {@link CharacterFactory}.
		 */
		public IFactory<Character> getFactory()
		{
			if (factory == null)
			{
				factory = new CharacterFactory();
			}
			return factory;
		}

		/**
		 * Returns the keywords used to build the machine. Several configuration are supported, listed in (decreasing)
		 * precedence order:
		 *
		 * <ul>
		 *
		 * <li>If {@link CharacterMatchingTest#TAG_symbols} is specified, return exactly on keyword ass defined by that
		 * element.</li>
		 *
		 * <li>If one or more {@link CharacterMatchingTest#TAG_keyword} elements are specified, return the keywords
		 * defined by those elements.</li>
		 *
		 * <li>Use the global keywords as specified by the {@link CharacterMatchingTest#TAG_keyword} child elements of
		 * the {@link CharacterMatchingTest#TAG_globals} element</li>
		 *
		 * </ul>
		 *
		 * @return Returns the keywords used to build the machine..
		 */
		public IKeywords<Character> getKeywords()
		{
			final Element symbolsElement = getSingleElementByTagName(testCaseElement, TAG_symbols);
			if (symbolsElement != null)
			{
				final IKeywords<Character> keywords = new Keywords<>();
				keywords.add(createKeyword(symbolsElement));
				return keywords;
			}

			// XXX: How to reference these keywords in expected matches?
			final NodeList keywordNodes = testCaseElement.getElementsByTagName(TAG_keyword);
			final int nKeywordNodes = keywordNodes.getLength();
			if (nKeywordNodes > 0)
			{
				final IKeywords<Character> keywords = new Keywords<>();
				for (int i = 0; i < nKeywordNodes; ++i)
				{
					final Element keywordElement = (Element) keywordNodes.item(i);
					keywords.add(createKeyword(keywordElement));
				}
				return keywords;
			}

			return globals.getKeywords();
		}

		public AhoCorasickMachine<Character> getMachine()
		{
			if (machine == null)
			{
				machine = new CharacterAhoCorasickMachine(getFactory(), getClassifier());

				machine.setNotifyLongestMatch(isNotifyLongestKeywordsOnly());
				machine.setNotifyMostPreciseMatch(isNotifyMostPreciseMatch());
				machine.setNotifyRawSymbols(isNotifyRawSymbols());
				machine.build(getKeywords());
			}

			return machine;
		}

		/**
		 * Returns the input text. Several configuration are supported, listed in (decreasing) precedence order:
		 *
		 * <ul>
		 *
		 * <li>{@link CharacterMatchingTest#TAG_text} element specifies the input text.</li>
		 *
		 * <li>{@link CharacterMatchingTest#TAG_symbols} specifies the input text (which is obtained by calling
		 * {@link CharacterMatchingTest#getRawSymbols(Element)}). {@link CharacterMatchingTest#TAG_head} and
		 * {@link CharacterMatchingTest#TAG_tail} specify optional input text that appears before and after the symbols,
		 * respectively.</li>
		 *
		 * </ul>
		 *
		 * @return Returns the input text.
		 */
		public String getText()
		{
			final Element textElement = getSingleElementByTagName(testCaseElement, TAG_text);
			if (textElement != null)
			{
				return eval(textElement);
			}

			final Element symbolsElement = getSingleElementByTagName(testCaseElement, TAG_symbols);
			if (symbolsElement != null)
			{
				final StringBuffer buf = new StringBuffer();

				buf
						.append(eval(testCaseElement, TAG_head))
						.append(getRawSymbols(symbolsElement))
						.append(eval(testCaseElement, TAG_tail));

				return buf.toString();
			}

			throw new AssertionError("unknown input text for test case");
		}

		public ITextSource<Character> getTextSoure()
		{
			if (textSource == null)
			{
				textSource = new CharacterStringTextSource(getText());
			}
			return textSource;
		}

		public boolean isCaseExtensionEnabled()
		{
			return getBoolOption(testCaseElement, TAG_caseExtension, globals.isCaseExtensionEnabled());
		}

		public boolean isNotifyLongestKeywordsOnly()
		{
			return getBoolOption(testCaseElement, TAG_notifyLongestKeywordsOnly, globals.isNotifyLongestKeywordsOnly());
		}

		public boolean isNotifyMostPreciseMatch()
		{
			return getBoolOption(testCaseElement, TAG_notifyMostPreciseMatch, globals.isNotifyMostPreciseMatch());
		}

		public boolean isNotifyRawSymbols()
		{
			return getBoolOption(testCaseElement, TAG_notifyRawSymbols, globals.isNotifyRawSymbols());
		}

		public boolean isPunctuationExtensionEnabled()
		{
			return getBoolOption(testCaseElement, TAG_punctuationExtension, globals.isPunctuationExtensionEnabled());
		}

		public boolean isWordBreakExtensionEnabled()
		{
			return getBoolOption(testCaseElement, TAG_wordBreakExtension, globals.isWordBreakExtensionEnabled());
		}

		private Match<Character> createMatch(final CharacterKeyword keyword_, final long start_, final long end_)
		{
			return new Match<>(getMachine(), keyword_, start_, end_);
		}
	}

	// =========================================================================
	// Parameters
	// =========================================================================
	@Parameterized.Parameters
	public static Collection<Object[]> generateData() throws ParserConfigurationException, SAXException, IOException
	{
		// XXX: Ideally, support multiple xml file resources to test with different keyword sets.
		// OR... support multiple different keyword sets in one big file.

		final String testCasesResource = CharacterMatchingTest.class.getSimpleName() + ".xml";
		final Document doc;
		try (final InputStream is = CharacterMatchingTest.class.getResourceAsStream(testCasesResource))
		{
			if (is == null)
			{
				throw new FileNotFoundException(testCasesResource);
			}

			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
		}
		doc.getDocumentElement().normalize();
		final Collection<Object[]> data = new ArrayList<>();

		final Element documentElenent = doc.getDocumentElement();
		final Element globalElement = getSingleElementByTagName(documentElenent, TAG_globals);

		final Globals globals = new Globals(globalElement);

		final NodeList testCaseNodes = documentElenent.getElementsByTagName(TAG_testCase);
		for (int i = 0; i < testCaseNodes.getLength(); ++i)
		{
			final Node testCaseNode = testCaseNodes.item(i);

			final Object[] parms =
			{ globals, testCaseNode };

			data.add(parms.clone());
		}

		return data;
	}

	// =========================================================================
	// Constructors
	// =========================================================================
	public CharacterMatchingTest(final Globals globals_, final Element testCaseElement_)
	{
		super();
		this.testCase = new TestCase(globals_, testCaseElement_);
	}

	// =========================================================================
	// Test methods
	// =========================================================================
	@Test
	public void runTestCase() throws IOException
	{
		verifyMatch(
				testCase.getMachine(),
				testCase.getTextSoure(),
				testCase.getExpectedMatches());
	}

	// =========================================================================
	// Internal methods
	// =========================================================================

	/**
	 * @param element_
	 * @return
	 */
	private static CharacterKeyword createKeyword(final Element element_)
	{
		assert element_ != null;

		final String symbols = eval(element_);
		final Object userData = null;
		int flags = 0;

		if (getBoolAttr(element_, ATTR_caseSensitive, false))
		{
			flags |= AbstractKeyword.CASE_SENSITIVE;
		}
		if (getBoolAttr(element_, ATTR_punctuationSensitive, false))
		{
			flags |= AbstractKeyword.PUNCTUATION_SENSITIVE;
		}

		return new CharacterKeyword(symbols, userData, flags);
	}

	/**
	 * @param element_
	 * @return
	 */
	private static String eval(final Element element_)
	{
		final StringBuilder buf = new StringBuilder();
		for (Node child = element_.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE)
			{
				buf.append(child.getNodeValue());
			}
		}
		return buf.toString();
	}

	/**
	 * @param root_
	 * @param tagName_
	 * @return
	 */
	private static String eval(final Element root_, final String tagName_)
	{
		assert root_ != null;
		assert tagName_ != null;

		final Element element = getSingleElementByTagName(root_, tagName_);
		return element == null ? "" : eval(element);
	}

	/**
	 * @param element_
	 * @param attrName_
	 * @return
	 */
	private static long getIntAttr(final Element element_, final String attrName_)
	{
		return Long.parseLong(element_.getAttribute(attrName_));
	}

	private static boolean getBoolAttr(final Element element_, final String attrName_, final boolean default_)
	{
		assert element_ != null;
		assert attrName_ != null;

		final Attr attr = element_.getAttributeNode(attrName_);
		return attr == null ? default_ : Boolean.parseBoolean(attr.getValue());
	}

	/**
	 * Gets the raw symbol from an element. If {@link CharacterMatchingTest#ATTR_raw} is specified, the symbols are
	 * obtained from that attribute, otherwise the symbols are obtained from the text content the element.
	 *
	 * @param element_
	 *            the element
	 * @return Returns the raw symbol
	 */
	private static String getRawSymbols(final Element element_)
	{
		final Attr raw = element_.getAttributeNode(ATTR_raw);
		return raw == null ? eval(element_) : raw.getValue();
	}

	private static Element getSingleElementByTagName(final Element root_, final String tagName_)
	{
		assert root_ != null;
		assert tagName_ != null;

		final NodeList elements = root_.getElementsByTagName(tagName_);
		return elements.getLength() == 0 ? null : (Element) elements.item(0);
	}

	private static boolean getBoolOption(final Element root_, final String tagName_, final boolean defaultValue_)
	{
		final Element optionElement = getSingleElementByTagName(root_, tagName_);
		return optionElement == null ? defaultValue_ : getBoolAttr(optionElement, ATTR_enabled, defaultValue_);
	}
}
