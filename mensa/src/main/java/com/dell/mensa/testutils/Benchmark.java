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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.dell.mensa.IEdgeMap;
import com.dell.mensa.IFactory;
import com.dell.mensa.IKeyword;
import com.dell.mensa.IKeywords;
import com.dell.mensa.IMatch;
import com.dell.mensa.INextMoveFunction;
import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.character.CharacterFactory;
import com.dell.mensa.impl.character.CharacterFileTextSource;
import com.dell.mensa.impl.character.CharacterKeyword;
import com.dell.mensa.impl.character.CharacterSymbolClassifier;
import com.dell.mensa.impl.generic.AbstractKeyword;
import com.dell.mensa.impl.generic.AhoCorasickMachine;
import com.dell.mensa.impl.generic.Match;
import com.dell.mensa.impl.generic.MatchCollector;
import com.dell.mensa.util.LoremIpsum;

/**
 * {@link Benchmark} is a command-line utility program that generates test data file for testing an Aho-Corasick machine
 * implementation.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class Benchmark
{
	private static final String PARA_BREAK = "\n\n";
	private static final String LINE_BREAK = "\n";
	private static final String WORD_BREAK = " ";

	private final boolean bForce;
	private final String dataDirName;
	private final String dataSetName;
	private final int numSubdirs;
	private final int numFilesPerSubdir;
	private final int minFileSize;
	private final int maxFileSize;
	private final int minLineSize;
	private final int maxLineSize;
	private final int minParaSize;
	private final int maxParaSize;
	private final int avgKeywordPeriod; // keyword probability = 1 / avgKeywordPeriod
	private final boolean bUpperCase;
	private final int maxKeywords;
	private final boolean caseSensitive; // Use case sensitive keywords?
	private final boolean bNoisyWordBreaks;

	private final LoremIpsum words;
	private int nextWord;

	private final Random rnd;

	private final String[] keywords;
	private static final String[] noisyWordBreaks =
	{
			"  ",
			"   ",
			"    ",
			"\n",
			" \n",
			" \n ",
			"\n    ",
			"    \n    "
	};

	private static final long RANDOM_SEED = 1;

	private final String keywordsResource;

	private final IFactory<Character> factory;
	private final AhoCorasickMachine<Character> machine;
	private final ISymbolClassifier<Character> classifier;

	private int totalBytes;
	private int totalMsec;
	private int totalMatched;
	private int totalExpectedNotFound;
	private int totalUnexpectedFound;

	private final boolean showDetails; // Show per file detailed reports?
	private final boolean dumpActualMatches; // Write actual matches to disk (for debugging)?

	private PrintWriter actualMatchesWriter;

	private StringBuilder out;
	private final boolean notifyLongestMatch;
	private final boolean notifyMostPreciseMatch;
	private final boolean notifyRawSymbols;

	/**
	 * @param args_
	 *            not used
	 *
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public Benchmark(final String... args_) throws IOException
	{
		bForce = true;
		dataDirName = "target/benchmark-data";
		dataSetName = "animals";
		numSubdirs = 10;
		numFilesPerSubdir = 10;
		minFileSize = 1 * 1024;
		maxFileSize = 1024 * 1024;
		minLineSize = 60;
		maxLineSize = 120;
		minParaSize = 400;
		maxParaSize = 1200;
		avgKeywordPeriod = 100;
		keywordsResource = "animals.keywords";
		bUpperCase = true;
		maxKeywords = 0; // Zero for unlimited
		caseSensitive = false;
		bNoisyWordBreaks = true;

		words = new LoremIpsum();
		nextWord = 0;

		rnd = new Random(RANDOM_SEED);

		factory = new CharacterFactory();
		classifier = new CharacterSymbolClassifier();
		machine = new AhoCorasickMachine<>(factory, classifier);

		keywords = loadKeywords(keywordsResource);

		showDetails = false;
		dumpActualMatches = false;

		notifyLongestMatch = true;
		notifyMostPreciseMatch = true;
		notifyRawSymbols = false;
	}

	/**
	 * Main entry point.
	 *
	 * @param args_
	 *            not used
	 *
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public static void main(final String... args_) throws IOException
	{
		new Benchmark(args_).runImpl();
	}

	public String run() throws IOException
	{
		out = new StringBuilder();
		runImpl();
		return out.toString();
	}

	/**
	 * @throws IOException
	 *
	 */
	private void runImpl() throws IOException
	{
		println("Benchmark v0.01");
		println(String.format("%d keywords loaded", keywords.length));

		final File dataDir = new File(dataDirName, dataSetName);
		if (!dataDir.exists())
		{
			dataDir.mkdirs();
			println(String.format("Created dataset dir: %s", dataDir.getAbsolutePath()));
		}

		writeKeywords(dataDir);
		generateFiles(dataDir);
		matchFiles();
		println("Done.");
	}

	/**
	 * @throws IOException
	 */
	private void matchFiles() throws IOException
	{
		final File dataDir = new File(dataDirName, dataSetName);

		final IKeywords<Character> keywordSet = createKeywordSet();
		println(String.format("%d keywords loaded; case-sensitive: %b", keywordSet.size(), caseSensitive));

		if (dumpActualMatches)
		{
			final File file = new File(dataDir, caseSensitive ? "actualMatches-caseSensitive.txt" : "actualMatches.txt");
			println(String.format("Writing actual matches to file: %s", file));
			actualMatchesWriter = new PrintWriter(file);
		}

		buildMachine(keywordSet);

		totalBytes = 0;
		totalMsec = 0;
		totalMatched = 0;
		totalExpectedNotFound = 0;
		totalUnexpectedFound = 0;

		matchFiles(dataDir);

		if (dumpActualMatches)
		{
			actualMatchesWriter.close();
		}

		final String msg = String.format("Summary: %7.3f MB processed in %5.2f seconds; rate: %s; keywords matched: %d, not found: %d, unexpected: %d",
				totalBytes / (1024.0 * 1024.0),
				totalMsec / 1000.0,
				rate(totalBytes, totalMsec / 1000.0),
				totalMatched,
				totalExpectedNotFound,
				totalUnexpectedFound);

		println(msg);

		if (totalExpectedNotFound > 0)
		{
			throw new RuntimeException(String.format("%d expected keyword(s) were not found", totalExpectedNotFound));
		}
	}

	/**
	 * @param keywords_
	 */
	private void buildMachine(final IKeywords<Character> keywords_)
	{
		// Warm up all classes/methods we will use
		HeapUtils.heapSnapshot();

		machine.reset();
		machine.setNotifyLongestMatch(notifyLongestMatch);
		machine.setNotifyMostPreciseMatch(notifyMostPreciseMatch);
		machine.setNotifyRawSymbols(notifyRawSymbols);

		final long h0 = HeapUtils.heapSnapshot();
		final long t0 = System.currentTimeMillis();
		machine.buildGotoFunction(keywords_);
		final long t1 = System.currentTimeMillis();
		machine.buildFailureFunction();
		final long t2 = System.currentTimeMillis();
		machine.buildNextMoveFunction();
		final long t3 = System.currentTimeMillis();
		final long h3 = HeapUtils.heapSnapshot();

		final INextMoveFunction<Character> nextMoveFunction = machine.getNextMoveFunction();

		int nEdges = 0;
		for (int state = 0; state < machine.getNumStates(); state++)
		{
			final IEdgeMap<Character> edgeMap = nextMoveFunction.getEdgeMap(state);
			nEdges += edgeMap.size();
		}

		final String msg = String
				.format("%s\nKeywords: %d, States: %d, Edges: %d (%f per state), build time: %d mSec (goto: %d, failure: %d, nextMove: %d), heap used: %d (%d per keyword)",
						machine,
						keywords_.size(),
						machine.getNumStates(),
						nEdges,
						(1.0 * nEdges) / machine.getNumStates(),
						t3 - t0,
						t1 - t0,
						t2 - t1,
						t3 - t2,
						h3 - h0,
						(h3 - h0) / keywords_.size());
		println(msg);
	}

	/**
	 * @param file_
	 * @throws IOException
	 */
	private void matchFiles(final File file_) throws IOException
	{
		if (file_ != null)
		{
			if (file_.isDirectory())
			{
				final String[] list = file_.list();
				if (list != null)
				{
					final Set<String> sortedList = new TreeSet<>();
					for (final String name : list)
					{
						sortedList.add(name);
					}

					for (final String name : sortedList)
					{
						matchFiles(new File(file_, name));
					}
				}
			}
			else if (file_.isFile())
			{
				matchFile(file_);
			}
		}
	}

	/**
	 * @param file_
	 * @throws IOException
	 */
	private void matchFile(final File file_) throws IOException
	{
		final String filePath = file_.getAbsolutePath();
		if (filePath.endsWith(".txt"))
		{
			final File idxFile = new File(file_.getAbsolutePath().replaceAll("\\.txt", ".idx"));
			if (idxFile.exists())
			{
				matchDataFile(file_, idxFile);
			}
		}
	}

	/**
	 * @param file_
	 * @param idxFile_
	 * @throws IOException
	 */
	private void matchDataFile(final File file_, final File idxFile_) throws IOException
	{
		final Charset charset = Charset.defaultCharset();
		final ITextSource<Character> textSource = new CharacterFileTextSource(file_, charset);

		final MatchCollector<Character> collector = new MatchCollector<>();

		textSource.open();
		final long t0 = now();
		machine.match(textSource, collector);
		final long t1 = now();
		textSource.close();

		final int nMatched = collector.getMatches().size();
		totalMatched += nMatched;

		final long fileSize = file_.length();

		final long dt = t1 - t0;

		totalBytes += fileSize;
		totalMsec += dt;

		final String msg = String.format("%s, size: %d bytes, time: %d mSec, rate: %s, keywords matched: %d",
				file_.getAbsolutePath(), fileSize, dt, rate(fileSize, dt / 1000.0), nMatched);

		boolean bMsgPrinted = false;

		if (showDetails)
		{
			println(msg);
			bMsgPrinted = true;

			// if (nMatched > 0)
			// {
			// println(collector.toString());
			// }
		}

		final MatchCollector<Character> expected = loadExpectedMatches(machine, idxFile_);

		final List<IMatch<Character>> actualMatches = collector.getMatches();
		final List<IMatch<Character>> expectedMatches = expected.getMatches();

		for (final IMatch<Character> match : expectedMatches)
		{
			if (!actualMatches.contains(match))
			{
				if (!bMsgPrinted)
				{
					println(msg);
					bMsgPrinted = true;
				}
				println(String.format("error: expected match not found: %s", match));
				totalExpectedNotFound++;
			}
		}

		for (final IMatch<Character> match : actualMatches)
		{
			if (!expectedMatches.contains(match) && !isOverlappedMatch(expectedMatches, match))
			{
				if (!bMsgPrinted)
				{
					println(msg);
					bMsgPrinted = true;
				}
				println(String.format("error: unexpected match found: %s", match));
				totalUnexpectedFound++;
			}

			if (dumpActualMatches)
			{
				final String keyword = match.getKeyword().toString().replaceFirst(".*: ", "");
				actualMatchesWriter.println(String.format("%s: %s, [%d,%d]", file_, keyword, match.getStart(), match.getEnd()));
			}
		}
	}

	/**
	 * @param expectedMatches_
	 * @param match_
	 * @return
	 */
	private boolean isOverlappedMatch(final List<IMatch<Character>> expectedMatches_, final IMatch<Character> match_)
	{
		final IKeyword<Character> keyword = match_.getKeyword();
		final long start = match_.getStart();
		final long end = match_.getEnd();

		for (final IMatch<Character> match : expectedMatches_)
		{
			if (match.getStart() == start)
			{
				if (startsWith(match.getKeyword(), keyword))
				{
					return true;
				}
			}

			if (match.getEnd() == end)
			{
				if (endsWith(match.getKeyword(), keyword))
				{
					return true;
				}
			}

			if (match.getStart() <= start && end <= match.getEnd())
			{
				if (contains(match.getKeyword(), keyword))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param keyword_
	 * @param substr_
	 * @return
	 */
	private boolean contains(final IKeyword<Character> keyword_, final IKeyword<Character> substr_)
	{
		final String keyword = asString(keyword_);
		final String substr = asString(substr_);

		return keyword.contains(substr);
	}

	/**
	 * @param keyword_
	 * @return
	 */
	private String asString(final IKeyword<Character> keyword_)
	{
		final boolean isCaseExtensionEnabled = classifier.isCaseExtensionEnabled();
		final boolean isPunctuationExtensionEnabled = classifier.isPunctuationExtensionEnabled();
		final boolean isCaseSensitive = keyword_.isCaseSensitive();
		final boolean isPunctuationSensitive = keyword_.isPunctuationSensitive();
		final Character space = classifier.getSpace();
		final int length = keyword_.length();
		final StringBuilder buf = new StringBuilder();

		boolean bSpacePending = false;
		for (int i = 0; i < length; i++)
		{
			Character a = keyword_.symbolAt(i);
			if (classifier.isPunctuation(a))
			{
				if (isPunctuationExtensionEnabled && !isPunctuationSensitive)
				{
					bSpacePending = true;
					continue;
				}
			}
			else
			{
				if (isCaseExtensionEnabled && !isCaseSensitive)
				{
					a = classifier.toLowerCase(a);
				}

				if (space.equals(a))
				{
					bSpacePending = true;
					continue;
				}
			}

			if (bSpacePending && buf.length() > 0)
			{
				buf.append(space);
				bSpacePending = false;
			}

			buf.append(a);
		}
		return buf.toString();
	}

	/**
	 * @param keyword_
	 * @param prefix_
	 * @return
	 */
	private boolean startsWith(final IKeyword<Character> keyword_, final IKeyword<Character> prefix_)
	{
		final String keyword = asString(keyword_);
		final String prefix = asString(prefix_);

		return keyword.startsWith(prefix);
	}

	/**
	 * @param keyword_
	 * @param suffix_
	 * @return
	 */
	private boolean endsWith(final IKeyword<Character> keyword_, final IKeyword<Character> suffix_)
	{
		final String keyword = asString(keyword_);
		final String suffix = asString(suffix_);

		return keyword.endsWith(suffix);
	}

	/**
	 * @param machine_
	 * @param idxFile_
	 * @return
	 * @throws IOException
	 */
	private static MatchCollector<Character> loadExpectedMatches(final AhoCorasickMachine<Character> machine_, final File idxFile_) throws IOException
	{
		final MatchCollector<Character> expected = new MatchCollector<>();
		expected.notifyBeginMatching(machine_);

		try (final Reader fileReader = new FileReader(idxFile_);
				final BufferedReader reader = new BufferedReader(fileReader))
		{

			final Pattern pattern = Pattern.compile("^([* ])([0-9]+)\\s+([0-9]+)\\s+(.+)$");

			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				final Matcher matcher = pattern.matcher(line);
				if (matcher.matches())
				{
					// final boolean bNoisy = matcher.group(1).equals("*");
					final int start = Integer.parseInt(matcher.group(2));
					final int end = Integer.parseInt(matcher.group(3));
					final IKeyword<Character> keyword = new CharacterKeyword(matcher.group(4));

					final Match<Character> match = new Match<>(machine_, keyword, start, end);
					expected.notifyMatch(match);
				}
			}
		}

		expected.notifyEndMatching(machine_);
		return expected;
	}

	/**
	 * @param bytes_
	 * @param sec_
	 * @return
	 */
	private static String rate(final long bytes_, final double sec_)
	{
		if (sec_ == 0)
		{
			return bytes_ == 0 ? "-" : "screaming!";
		}

		return String.format("%7.3f Mb/sec", bytes_ / (1024.0 * 1024.0 * sec_));
	}

	/**
	 * @return
	 */
	private static long now()
	{
		return System.currentTimeMillis();
	}

	/**
	 * @param kewordResource_
	 * @return
	 * @throws IOException
	 */
	private String[] loadKeywords(final String kewordResource_) throws IOException
	{
		final List<String> list = new ArrayList<>();

		final InputStream is = getClass().getResourceAsStream(kewordResource_);
		try (final Reader fileReader = new InputStreamReader(is);
				final BufferedReader reader = new BufferedReader(fileReader))
		{

			int n = 0;
			final int max = maxKeywords == 0 ? Integer.MAX_VALUE : maxKeywords;
			for (String keyword = reader.readLine(); keyword != null && n < max; keyword = reader.readLine())
			{
				list.add(keyword);
				n++;
			}
		}

		return list.toArray(new String[list.size()]);
	}

	private IKeywords<Character> createKeywordSet()
	{
		final IKeywords<Character> set = factory.createKeywords();
		for (final String keyword : this.keywords)
		{
			set.add(createKeyword(keyword, caseSensitive));
		}
		return set;
	}

	private static IKeyword<Character> createKeyword(final String keyword_, final boolean bCaseSensitive_)
	{
		return bCaseSensitive_
				? new CharacterKeyword(keyword_, null, AbstractKeyword.CASE_SENSITIVE)
				: new CharacterKeyword(keyword_);
	}

	/**
	 * @param dataDir
	 * @throws FileNotFoundException
	 *
	 */
	private void writeKeywords(final File dataDir) throws FileNotFoundException
	{
		final File kwFile = new File(dataDir, "keywords.txt");
		try (final PrintWriter kwWriter = new PrintWriter(kwFile))
		{
			for (final String keyword : keywords)
			{
				kwWriter.println(keyword);
			}
		}
	}

	/**
	 * @param dataDir
	 * @throws FileNotFoundException
	 *
	 */
	private void generateFiles(final File dataDir) throws FileNotFoundException
	{
		for (int iSubdir = 0; iSubdir < numSubdirs; iSubdir++)
		{
			final File subdir = new File(dataDir, String.format("%04d", iSubdir)); // NOPMD by <a
																					// href="http://www.linkedin.com/in/faseidl/"
																					// target="_blank">F. Andy Seidl</a>
																					// on 9/16/14 10:10
																					// PM
			generateSubdir(subdir);
		}
	}

	/**
	 * @param subdir_
	 * @throws FileNotFoundException
	 */
	private void generateSubdir(final File subdir_) throws FileNotFoundException
	{
		if (!subdir_.exists())
		{
			subdir_.mkdirs();
			println(String.format("Created subdir: %s", subdir_.getAbsolutePath()));
		}

		for (int iFile = 0; iFile < numFilesPerSubdir; iFile++)
		{
			generateFile(subdir_, iFile);
		}
	}

	/**
	 * @param subdir_
	 * @param iFile_
	 * @throws FileNotFoundException
	 */
	private void generateFile(final File subdir_, final int iFile_) throws FileNotFoundException
	{
		final File txtFile = new File(subdir_, String.format("%04d.txt", iFile_));

		if (bForce || !txtFile.exists())
		{
			final File idxFile = new File(subdir_, String.format("%04d.idx", iFile_));

			try (final PrintWriter txtWriter = new PrintWriter(txtFile);
					final PrintWriter idxWriter = new PrintWriter(idxFile))
			{

				generateFile(txtWriter, idxWriter);
			}

			println(String.format("Created file: %s", txtFile.getAbsolutePath()));
		}
	}

	/**
	 * @param txtWriter_
	 * @param idxWriter_
	 */
	private void generateFile(final PrintWriter txtWriter_, final PrintWriter idxWriter_)
	{
		final int targetFileSize = minFileSize + rnd.nextInt(maxFileSize - minFileSize + 1);
		final int targetLineSize = minLineSize + rnd.nextInt(maxLineSize - minLineSize + 1);
		final int targetParaSize = minParaSize + rnd.nextInt(maxParaSize - minParaSize + 1);

		int fileSize = 0;
		int lineSize = 0;
		int paraSize = 0;

		while (fileSize < targetFileSize)
		{
			if (paraSize > targetParaSize)
			{
				txtWriter_.print(PARA_BREAK);
				fileSize += PARA_BREAK.length();
				lineSize = 0;
				paraSize = 0;
			}
			else if (lineSize > targetLineSize)
			{
				txtWriter_.print(LINE_BREAK);
				fileSize += LINE_BREAK.length();
				lineSize = 0;
				paraSize += LINE_BREAK.length();
			}
			else if (fileSize > 0)
			{
				txtWriter_.print(WORD_BREAK);
				fileSize += WORD_BREAK.length();
				lineSize += WORD_BREAK.length();
				paraSize += WORD_BREAK.length();
			}

			final String word;
			if (rnd.nextInt(avgKeywordPeriod) == 0)
			{
				final String keyword = getKeyword();

				// Determine if we should inject noise into a multi-word keyword.
				final boolean bAddNoise = bNoisyWordBreaks && keyword.contains(WORD_BREAK) && rnd.nextBoolean();

				// Create the word, with or without noise, as appropriate.
				word = bAddNoise
						? keyword.replaceAll(WORD_BREAK, getNoisyWordBreak())
						: keyword;

				idxWriter_.printf("%c%d %d %s\n",
						bAddNoise ? '*' : ' ',
						fileSize,
						fileSize + word.length(),
						keyword);
			}
			else
			{
				word = getWord();
			}

			final String actualWord = bUpperCase ? word.toUpperCase(Locale.US) : word;

			txtWriter_.print(actualWord);

			final int actualWordLength = actualWord.length();
			fileSize += actualWordLength;
			lineSize += actualWordLength;
			paraSize += actualWordLength;
		}
	}

	/**
	 * @return
	 */
	private String getWord()
	{
		return words.getWord(nextWord++ % words.getNumWords());
	}

	private String getKeyword()
	{
		return keywords[rnd.nextInt(keywords.length)];
	}

	/**
	 * Returns a random noisy word break sequence.
	 *
	 * @return Returns a random noisy word break sequence.
	 */
	private String getNoisyWordBreak()
	{
		return noisyWordBreaks[rnd.nextInt(noisyWordBreaks.length)];
	}

	/**
	 * @param msg_
	 */
	private void println(final String msg_)
	{
		if (out == null)
		{
			System.out.println(msg_); // NOPMD by <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy
										// Seidl</a> on 10/1/14 4:10 PM
		}
		else
		{
			out.append(msg_);
			out.append('\n');
		}
	}
}
