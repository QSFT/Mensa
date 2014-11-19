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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.dell.mensa.util.LoremIpsum;

/**
 * {@link GenerateFiles} is a command-line utility program that generates test data file for testing an Aho-Corasick
 * machine implementation.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public class GenerateFiles
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

	private final String keywordResource;

	/**
	 * @param args_
	 *            not used
	 *
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public GenerateFiles(final String... args_) throws IOException
	{
		bForce = true;
		dataDirName = "target/test-data";
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
		keywordResource = "animals.keywords";
		bUpperCase = false;
		maxKeywords = 0; // Zero for unlimited
		bNoisyWordBreaks = true;

		words = new LoremIpsum();
		nextWord = 0;

		rnd = new Random();

		keywords = loadKeywords(keywordResource);
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
		new GenerateFiles(args_).run();
	}

	/**
	 * @throws FileNotFoundException
	 *
	 */
	private void run() throws FileNotFoundException
	{
		println("GenerateFiles v0.01");
		println(String.format("%d keywords loaded", keywords.length));

		final File dataDir = new File(dataDirName, dataSetName);
		if (!dataDir.exists())
		{
			dataDir.mkdirs();
			println(String.format("Created dataset dir: %s", dataDir.getAbsolutePath()));
		}

		final File kwFile = new File(dataDir, "keywords.txt");
		try (final PrintWriter kwWriter = new PrintWriter(kwFile))
		{

			for (final String keyword : keywords)
			{
				kwWriter.println(keyword);
			}
		}

		for (int iSubdir = 0; iSubdir < numSubdirs; iSubdir++)
		{
			final File subdir = new File(dataDir, String.format("%04d", iSubdir)); // NOPMD by <a
																					// href="http://www.linkedin.com/in/faseidl/"
																					// target="_blank">F. Andy Seidl</a>
																					// on 9/16/14 10:10
																					// PM
			generateSubdir(subdir);
		}

		println("Done.");
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
	private static void println(final String msg_)
	{
		System.out.println(msg_); // NOPMD by <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy
									// Seidl</a> on 10/1/14 4:10 PM
	}
}
