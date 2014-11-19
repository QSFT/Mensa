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

import com.dell.mensa.IKeyword;
import com.dell.mensa.ISymbolClassifier;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.ITextSource;
import com.dell.mensa.impl.generic.DefaultMatchPrecisionFunction;

/**
 * {@link CharacterMatchPrecisionFunction} specializes the {@link DefaultMatchPrecisionFunction} for use with
 * {@link Character} symbols. The {@link #evalImpl(IKeyword, ITextSource, long, long)} method is overridden provide
 * smarter heuristics for character symbol precision calculations.
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 *
 */
public class CharacterMatchPrecisionFunction extends DefaultMatchPrecisionFunction<Character>
{
	/**
	 * Precision of a normal keyword symbol that exactly matches a raw symbol in the text source.
	 */
	public static final double NORMAL_EXACT_MATCH = MAX_PRECISION;

	/**
	 * Precision of a normal keyword symbol that matches a raw symbol in the text source after some type of case
	 * conversion.
	 */
	public static final double NORMAL_LOWERCASE_MATCH = 0.8 * MAX_PRECISION;

	/**
	 * Precision of a normal keyword symbol that does not match. (This would only occur in practice if some future text
	 * source performed an unusual raw symbol transformation.)
	 */
	public static final double NORMAL_OTHER_MATCH = 0.5 * MAX_PRECISION;

	/**
	 * Precision of a punctuation keyword symbol exactly matches a symbol in the text source.
	 */
	public static final double PUNCTUATION_EXACT_MATCH = MAX_PRECISION;

	/**
	 * Precision of a punctuation keyword symbol that was fuzzily matches against a different punctuation symbol in the
	 * text source.
	 */
	public static final double PUNCTUATION_IGNORED = 0;

	/**
	 * Precision of a punctuation keyword symbol that had no corresponding match in the text source.
	 */
	public static final double PUNCTUATION_OMITTED = 0.5 * MAX_PRECISION;

	/**
	 * Precision of a punctuation keyword symbol exactly matches a symbol in the text source.
	 */
	public static final double WHITE_SPACE_EXACT_MATCH = MAX_PRECISION;

	/**
	 * Coefficient used to compute the impact of extra (ignored) symbol in the text source. The general form of the
	 * calculation is:
	 *
	 * <pre>
	 * effectivePrecision = maxPrecision / (EXTRA_DECAY_RATE * extra + 1)
	 * </pre>
	 */
	public static final double EXTRA_DECAY_RATE = 0.2;

	// =========================================================================
	// Properties
	// =========================================================================
	private final ISymbolClassifier<Character> classifier;
	private final boolean isCaseExtensionEnabled;
	private final boolean isPunctuationExtensionEnabled;

	// =========================================================================
	// Constructors
	// =========================================================================
	/**
	 * Constructs a new instance that will used the specified symbol classifier.
	 *
	 * @param classifier_
	 *            the {@link ISymbolClassifier} used by this instance.
	 */
	public CharacterMatchPrecisionFunction(final ISymbolClassifier<Character> classifier_)
	{
		super();
		this.classifier = classifier_;
		this.isCaseExtensionEnabled = classifier_.isCaseExtensionEnabled();
		this.isPunctuationExtensionEnabled = classifier_.isPunctuationExtensionEnabled();
	}

	// =========================================================================
	// Protected methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.DefaultMatchPrecisionFunction#evalImpl(com.dell.mensa.IKeyword,
	 * com.dell.mensa.ITextSource, long, long)
	 */
	@Override
	protected double evalImpl(final IKeyword<Character> keyword_, final ITextSource<Character> textSource_, final long start_, final long end_)
	{
		final ITailBuffer<Character> buffer = textSource_.getTailBuffer();
		final ITailBuffer<Character> rawBuffer = textSource_.getRawTailBuffer();
		final long bufferEnd = buffer.end();
		final int keywordLength = keyword_.length();

		double sum = 0.0;
		long position = start_;
		for (int i = 0; i < keywordLength && position < bufferEnd; ++i)
		{
			final Character keywordSymbol = keyword_.symbolAt(i);

			final double p;
			final int extra;

			if (classifier.isWhitespace(keywordSymbol))
			{
				extra = countExtra(buffer, position + 1);
				p = precisionOfWhitespace(extra);
			}
			else if (isPunctuationExtensionEnabled && classifier.isPunctuation(keywordSymbol))
			{
				extra = countExtra(buffer, position + 1);
				p = precisionOfPunctuation(keywordSymbol, rawBuffer, position, extra);
			}
			else
			{
				extra = 0;
				final Character rawSymbol = rawBuffer.symbolAt(position);
				p = precisionOfNormalSymbol(keywordSymbol, rawSymbol);
			}

			sum += p;
			position += 1 + extra;
		}

		return sum / keywordLength;
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	private static int countExtra(final ITailBuffer<Character> buffer_, final long position_)
	{
		int extra = 0;
		final long end = buffer_.end();
		while (position_ + extra < end && buffer_.symbolAt(position_ + extra) == null)
		{
			++extra;
		}
		return extra;
	}

	private static double decay(final double precision_, final int extra_)
	{
		return precision_ / (EXTRA_DECAY_RATE * extra_ + 1);
	}

	/**
	 * @param keywordSymbol_
	 * @param rawSymbol_
	 * @return
	 */
	private double precisionOfNormalSymbol(final Character keywordSymbol_, final Character rawSymbol_)
	{
		if (keywordSymbol_.equals(rawSymbol_))
		{
			return NORMAL_EXACT_MATCH;
		}

		if (isCaseExtensionEnabled &&
				classifier.toLowerCase(rawSymbol_).equals(classifier.toLowerCase(keywordSymbol_)))
		{
			return NORMAL_LOWERCASE_MATCH;
		}

		return NORMAL_OTHER_MATCH;
	}

	/**
	 * @param keywordSymbol_
	 * @param rawBuffer_
	 * @param position_
	 * @param extra_
	 * @return
	 */
	private double precisionOfPunctuation(final Character keywordSymbol_, final ITailBuffer<Character> rawBuffer_, final long position_, final int extra_)
	{
		// Find the raw symbol that triggered the match.
		long position = position_;
		final long end = position_ + 1 + extra_;
		Character a = rawBuffer_.symbolAt(position++);
		while (classifier.isWhitespace(a) && position < end)
		{
			a = rawBuffer_.symbolAt(position++);
		}

		// Evaluation precision.
		final double precision = keywordSymbol_.equals(a)
				? PUNCTUATION_EXACT_MATCH
				: isPunctuationExtensionEnabled && classifier.isPunctuation(a)
						? PUNCTUATION_IGNORED
						: PUNCTUATION_OMITTED;

		return decay(precision, extra_);
	}

	/**
	 * @param extra_
	 * @return
	 */
	private static double precisionOfWhitespace(final int extra_)
	{
		return decay(WHITE_SPACE_EXACT_MATCH, extra_);
	}
}
