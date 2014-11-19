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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import com.dell.mensa.ITailBuffer;
import com.dell.mensa.impl.generic.AbstractTextSource;

/**
 * {@link AbstractCharacterTextSource} extends {@link AbstractTextSource} for reading {@link Character} symbols from the
 * {@link Reader} returned by the abstract {@link #getReader()} method.
 *
 * <p>
 * This implementation performs white space normalization by reducing runs of consecutive white space character (as
 * determined by {@link #isWhitespace(int)}) to a single logical character. That is, {@link #read()} will consume the an
 * entire run of consecutive white space characters, advance the position to the next non-white-space character, and
 * return a single character. The raw characters can be obtained using {@link #getRawTailBuffer()}.
 * </p>
 *
 * <p>
 * This implementation uses the following heuristics to determine which single character is returned by {@link #read()}
 * to represent a run of consecutive white space characters:
 * </p>
 *
 * <ul>
 *
 * <li>If the run contains a {@link #FF} character, return {@link #FF}.</li>
 *
 * <li>If the run contains two or more {@link #LF} characters, return {@link #LF}.</li>
 *
 * <li>Otherwise, return {@link #SPACE}.</li>
 *
 * </ul>
 *
 * @author <a href="http://www.linkedin.com/in/faseidl/" target="_blank">F. Andy Seidl</a>
 */
public abstract class AbstractCharacterTextSource extends AbstractTextSource<Character>
{
	/**
	 * Unicode Character 'CHARACTER TABULATION' (U+0009)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/0009/index.htm">FileFormat.Info</a>
	 */
	public static final char TAB = 0x0009;

	/**
	 * Unicode Character 'LINE FEED (LF)' (U+000A)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/000a/index.htm">FileFormat.Info</a>
	 */
	public static final char LF = 0x000A;

	/**
	 * Unicode Character 'LINE TABULATION' (U+000B) - Vertical Tab
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/000B/index.htm">FileFormat.Info</a>
	 */
	public static final char VTAB = 0x000B;

	/**
	 * Unicode Character 'FORM FEED (FF)' (U+000C)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/000c/index.htm">FileFormat.Info</a>
	 */
	public static final char FF = 0x000C;

	/**
	 * Unicode Character 'CARRIAGE RETURN (CR)' (U+000D)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/000d/index.htm">FileFormat.Info</a>
	 */
	public static final char CR = 0x000D;

	/**
	 * Unicode Character 'INFORMATION SEPARATOR FOUR' (U+001C) - File Separator
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/001c/index.htm">FileFormat.Info</a>
	 */
	public static final char FILESEP = 0x001C;

	/**
	 * Unicode Character 'INFORMATION SEPARATOR THREE' (U+001D) - Group Separator
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/001d/index.htm">FileFormat.Info</a>
	 */
	public static final char GROUPSEP = 0x001D;

	/**
	 * Unicode Character 'INFORMATION SEPARATOR TWO' (U+001E) - Record Separator
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/001e/index.htm">FileFormat.Info</a>
	 */
	public static final char RECORDSEP = 0x001E;

	/**
	 * Unicode Character 'INFORMATION SEPARATOR ONE' (U+001F) - Unit Separator
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/001f/index.htm">FileFormat.Info</a>
	 */
	public static final char UNITSEP = 0x001F;

	/**
	 * Unicode Character 'SPACE' (U+0020)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/0020/index.htm">FileFormat.Info</a>
	 */
	public static final char SPACE = 0x0020;

	/**
	 * Unicode Character 'NO-BREAK SPACE' (U+00A0)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/00A0/index.htm">FileFormat.Info</a>
	 */
	public static final char NBSP = 0x00A0;

	/**
	 * Unicode Character 'FIGURE SPACE' (U+2007)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/2007/index.htm">FileFormat.Info</a>
	 */
	public static final char FIGURESPACE = 0x2007;

	/**
	 * Unicode Character 'NARROW NO-BREAK SPACE' (U+202F)
	 *
	 * @see <a href="http://www.fileformat.info/info/unicode/char/202f/index.htm">FileFormat.Info</a>
	 */
	public static final char NNBSP = 0x202F;

	/**
	 * Table of UTF-16 character values for punctuation characters. This table is derived from the .NET <a
	 * href="http://msdn.microsoft.com/en-us/library/system.char.ispunctuation(v=vs.110).aspx">
	 * {@code Char.IsPunctuation()}</a> method using a simple unit test method:
	 *
	 * <pre>
	 * [TestMethod]
	 * public void GeneratePunctuationTable()
	 * {
	 *     int n = 0;
	 *     for ( char c = Character.MIN_VALUE; c &lt; Character.MAX_VALUE; ++c)
	 *     {
	 *         if (System. Char.IsPunctuation(c))
	 *         {
	 *             Console.Write( @"0x{0:x4}, " , (int )c);
	 *             if (++n%10 == 0)
	 *             {
	 *                 Console.WriteLine();
	 *             }
	 *         }
	 *     }
	 * }
	 * </pre>
	 */
	public static final int[] PUNCTUATION =
	{
			0x0021, 0x0022, 0x0023, 0x0025, 0x0026, 0x0027, 0x0028, 0x0029, 0x002a, 0x002c,
			0x002d, 0x002e, 0x002f, 0x003a, 0x003b, 0x003f, 0x0040, 0x005b, 0x005c, 0x005d,
			0x005f, 0x007b, 0x007d, 0x00a1, 0x00ab, 0x00ad, 0x00b7, 0x00bb, 0x00bf, 0x037e,
			0x0387, 0x055a, 0x055b, 0x055c, 0x055d, 0x055e, 0x055f, 0x0589, 0x058a, 0x05be,
			0x05c0, 0x05c3, 0x05c6, 0x05f3, 0x05f4, 0x0609, 0x060a, 0x060c, 0x060d, 0x061b,
			0x061e, 0x061f, 0x066a, 0x066b, 0x066c, 0x066d, 0x06d4, 0x0700, 0x0701, 0x0702,
			0x0703, 0x0704, 0x0705, 0x0706, 0x0707, 0x0708, 0x0709, 0x070a, 0x070b, 0x070c,
			0x070d, 0x07f7, 0x07f8, 0x07f9, 0x0964, 0x0965, 0x0970, 0x0df4, 0x0e4f, 0x0e5a,
			0x0e5b, 0x0f04, 0x0f05, 0x0f06, 0x0f07, 0x0f08, 0x0f09, 0x0f0a, 0x0f0b, 0x0f0c,
			0x0f0d, 0x0f0e, 0x0f0f, 0x0f10, 0x0f11, 0x0f12, 0x0f3a, 0x0f3b, 0x0f3c, 0x0f3d,
			0x0f85, 0x0fd0, 0x0fd1, 0x0fd2, 0x0fd3, 0x0fd4, 0x104a, 0x104b, 0x104c, 0x104d,
			0x104e, 0x104f, 0x10fb, 0x1361, 0x1362, 0x1363, 0x1364, 0x1365, 0x1366, 0x1367,
			0x1368, 0x166d, 0x166e, 0x169b, 0x169c, 0x16eb, 0x16ec, 0x16ed, 0x1735, 0x1736,
			0x17d4, 0x17d5, 0x17d6, 0x17d8, 0x17d9, 0x17da, 0x1800, 0x1801, 0x1802, 0x1803,
			0x1804, 0x1805, 0x1806, 0x1807, 0x1808, 0x1809, 0x180a, 0x1944, 0x1945, 0x19de,
			0x19df, 0x1a1e, 0x1a1f, 0x1b5a, 0x1b5b, 0x1b5c, 0x1b5d, 0x1b5e, 0x1b5f, 0x1b60,
			0x1c3b, 0x1c3c, 0x1c3d, 0x1c3e, 0x1c3f, 0x1c7e, 0x1c7f, 0x2010, 0x2011, 0x2012,
			0x2013, 0x2014, 0x2015, 0x2016, 0x2017, 0x2018, 0x2019, 0x201a, 0x201b, 0x201c,
			0x201d, 0x201e, 0x201f, 0x2020, 0x2021, 0x2022, 0x2023, 0x2024, 0x2025, 0x2026,
			0x2027, 0x2030, 0x2031, 0x2032, 0x2033, 0x2034, 0x2035, 0x2036, 0x2037, 0x2038,
			0x2039, 0x203a, 0x203b, 0x203c, 0x203d, 0x203e, 0x203f, 0x2040, 0x2041, 0x2042,
			0x2043, 0x2045, 0x2046, 0x2047, 0x2048, 0x2049, 0x204a, 0x204b, 0x204c, 0x204d,
			0x204e, 0x204f, 0x2050, 0x2051, 0x2053, 0x2054, 0x2055, 0x2056, 0x2057, 0x2058,
			0x2059, 0x205a, 0x205b, 0x205c, 0x205d, 0x205e, 0x207d, 0x207e, 0x208d, 0x208e,
			0x2329, 0x232a, 0x2768, 0x2769, 0x276a, 0x276b, 0x276c, 0x276d, 0x276e, 0x276f,
			0x2770, 0x2771, 0x2772, 0x2773, 0x2774, 0x2775, 0x27c5, 0x27c6, 0x27e6, 0x27e7,
			0x27e8, 0x27e9, 0x27ea, 0x27eb, 0x27ec, 0x27ed, 0x27ee, 0x27ef, 0x2983, 0x2984,
			0x2985, 0x2986, 0x2987, 0x2988, 0x2989, 0x298a, 0x298b, 0x298c, 0x298d, 0x298e,
			0x298f, 0x2990, 0x2991, 0x2992, 0x2993, 0x2994, 0x2995, 0x2996, 0x2997, 0x2998,
			0x29d8, 0x29d9, 0x29da, 0x29db, 0x29fc, 0x29fd, 0x2cf9, 0x2cfa, 0x2cfb, 0x2cfc,
			0x2cfe, 0x2cff, 0x2e00, 0x2e01, 0x2e02, 0x2e03, 0x2e04, 0x2e05, 0x2e06, 0x2e07,
			0x2e08, 0x2e09, 0x2e0a, 0x2e0b, 0x2e0c, 0x2e0d, 0x2e0e, 0x2e0f, 0x2e10, 0x2e11,
			0x2e12, 0x2e13, 0x2e14, 0x2e15, 0x2e16, 0x2e17, 0x2e18, 0x2e19, 0x2e1a, 0x2e1b,
			0x2e1c, 0x2e1d, 0x2e1e, 0x2e1f, 0x2e20, 0x2e21, 0x2e22, 0x2e23, 0x2e24, 0x2e25,
			0x2e26, 0x2e27, 0x2e28, 0x2e29, 0x2e2a, 0x2e2b, 0x2e2c, 0x2e2d, 0x2e2e, 0x2e30,
			0x3001, 0x3002, 0x3003, 0x3008, 0x3009, 0x300a, 0x300b, 0x300c, 0x300d, 0x300e,
			0x300f, 0x3010, 0x3011, 0x3014, 0x3015, 0x3016, 0x3017, 0x3018, 0x3019, 0x301a,
			0x301b, 0x301c, 0x301d, 0x301e, 0x301f, 0x3030, 0x303d, 0x30a0, 0x30fb, 0xa60d,
			0xa60e, 0xa60f, 0xa673, 0xa67e, 0xa874, 0xa875, 0xa876, 0xa877, 0xa8ce, 0xa8cf,
			0xa92e, 0xa92f, 0xa95f, 0xaa5c, 0xaa5d, 0xaa5e, 0xaa5f, 0xfd3e, 0xfd3f, 0xfe10,
			0xfe11, 0xfe12, 0xfe13, 0xfe14, 0xfe15, 0xfe16, 0xfe17, 0xfe18, 0xfe19, 0xfe30,
			0xfe31, 0xfe32, 0xfe33, 0xfe34, 0xfe35, 0xfe36, 0xfe37, 0xfe38, 0xfe39, 0xfe3a,
			0xfe3b, 0xfe3c, 0xfe3d, 0xfe3e, 0xfe3f, 0xfe40, 0xfe41, 0xfe42, 0xfe43, 0xfe44,
			0xfe45, 0xfe46, 0xfe47, 0xfe48, 0xfe49, 0xfe4a, 0xfe4b, 0xfe4c, 0xfe4d, 0xfe4e,
			0xfe4f, 0xfe50, 0xfe51, 0xfe52, 0xfe54, 0xfe55, 0xfe56, 0xfe57, 0xfe58, 0xfe59,
			0xfe5a, 0xfe5b, 0xfe5c, 0xfe5d, 0xfe5e, 0xfe5f, 0xfe60, 0xfe61, 0xfe63, 0xfe68,
			0xfe6a, 0xfe6b, 0xff01, 0xff02, 0xff03, 0xff05, 0xff06, 0xff07, 0xff08, 0xff09,
			0xff0a, 0xff0c, 0xff0d, 0xff0e, 0xff0f, 0xff1a, 0xff1b, 0xff1f, 0xff20, 0xff3b,
			0xff3c, 0xff3d, 0xff3f, 0xff5b, 0xff5d, 0xff5f, 0xff60, 0xff61, 0xff62, 0xff63,
			0xff64, 0xff65
	};

	/**
	 * Specifies the minimum diacritial character.
	 *
	 * @see #DIACRITICAL_TO_ASCII_MAP
	 */
	public static final char MIN_DIACRITICAL = 192;

	/**
	 * Specifies the maximum diacritical character.
	 *
	 * @see #DIACRITICAL_TO_ASCII_MAP
	 */
	public static final char MAX_DIACRITICAL = 383;

	/**
	 * Diacritical Character to ASCII Character Mapping
	 *
	 * <p>
	 * This table maps all character in the range MIN_DIACRITICAL to MAX_DIACRITICAL to the corresponding ASCII
	 * character as follows:
	 *
	 * For any character c in the range [{@link #MIN_DIACRITICAL}, {@link #MAX_DIACRITICAL}], this mapping table
	 * contains the following entries:
	 * </p>
	 *
	 * <ul>
	 *
	 * <li>{@link #DIACRITICAL_TO_ASCII_MAP}{@code [2*i] = c}</li>
	 *
	 * <li>{@link #DIACRITICAL_TO_ASCII_MAP}{@code [2*i + 1] = } ASCII mapping (or {@code c} if there is no mapping for
	 * {@code c})</li>
	 *
	 * </ul>
	 *
	 * <p>
	 * where
	 * </p>
	 * <blockquote>{@code i = c - } {@link #MIN_DIACRITICAL}</blockquote>
	 *
	 * @see <a href="http://docs.oracle.com/cd/E29584_01/webhelp/mdex_basicDev/src/rbdv_chars_mapping.html">Oracle
	 *      Documentation</a>
	 * @see #mapDiacriticalToASCII(int)
	 */
	public static final int[] DIACRITICAL_TO_ASCII_MAP =
	{
			// ISO Latin1
			// ----------
			192, 'a', // Capital A, grave accent (MIN_DIACRITICAL)
			193, 'a', // Capital A, acute accent
			194, 'a', // Capital A, circumflex accent
			195, 'a', // Capital A, tilde
			196, 'a', // Capital A, dieresis or umlaut mark
			197, 'a', // Capital A, ring
			198, 'a', // Capital AE diphthong
			199, 'c', // Capital C, cedilla
			200, 'e', // Capital E, grave accent
			201, 'e', // Capital E, acute accent
			202, 'e', // Capital E, circumflex accent
			203, 'e', // Capital E, dieresis or umlaut mark
			204, 'i', // Capital I, grave accent
			205, 'i', // Capital I, acute accent
			206, 'i', // Capital I, circumflex accent
			207, 'i', // Capital I, dieresis or umlaut mark
			208, 'e', // Capital Eth, Icelandic
			209, 'n', // Capital N, tilde
			210, 'o', // Capital O, grave accent
			211, 'o', // Capital O, acute accent
			212, 'o', // Capital O, circumflex accent
			213, 'o', // Capital O, tilde
			214, 'o', // Capital O, dieresis or umlaut mark
			215, 215, // <no mapping>
			216, 'o', // Capital O, slash
			217, 'u', // Capital U, grave accent
			218, 'u', // Capital U, acute accent
			219, 'u', // Capital U, circumflex accent
			220, 'u', // Capital U, dieresis or umlaut mark
			221, 'y', // Capital Y, acute accent
			222, 'p', // Capital thorn, Icelandic
			223, 's', // Small sharp s, German
			224, 'a', // Small a, grave accent
			225, 'a', // Small a, acute accent
			226, 'a', // Small a, circumflex accent
			227, 'a', // Small a, tilde
			228, 'a', // Small a, dieresis or umlaut mark
			229, 'a', // Small a, ring
			230, 'a', // Small ae diphthong
			231, 'c', // Small c, cedilla
			232, 'e', // Small e, grave accent
			233, 'e', // Small e, acute accent
			234, 'e', // Small e, circumflex accent
			235, 'e', // Small e, dieresis or umlaut mark
			236, 'i', // Small i, grave accent
			237, 'i', // Small i, acute accent
			238, 'i', // Small i, circumflex accent
			239, 'i', // Small i, dieresis or umlaut mark
			240, 'e', // Small eth, Icelandic
			241, 'n', // Small n, tilde
			242, 'o', // Small o, grave accent
			243, 'o', // Small o, acute accent
			244, 'o', // Small o, circumflex accent
			245, 'o', // Small o, tilde
			246, 'o', // Small o, dieresis or umlaut mark
			247, 247, // <no mapping>
			248, 'o', // Small o, slash
			249, 'u', // Small u, grave accent
			250, 'u', // Small u, acute accent
			251, 'u', // Small u, circumflex accent
			252, 'u', // Small u, dieresis or umlaut mark
			253, 'y', // Small y, acute accent
			254, 'p', // Small thorn, Icelandic
			255, 'y', // Small y, dieresis or umlaut mark

			// ISO Latin1 Extended A
			// ---------------------
			256, 'a', // Capital A, macron accent
			257, 'a', // Small a, macron accent
			258, 'a', // Capital A, breve accent
			259, 'a', // Small a, breve accent
			260, 'a', // Capital A, ogonek accent
			261, 'a', // Small a, ogonek accent
			262, 'c', // Capital C, acute accent
			263, 'c', // Small c, acute accent
			264, 'c', // Capital C, circumflex accent
			265, 'c', // Small c, circumflex accent
			266, 'c', // Capital C, dot accent
			267, 'c', // Small c, dot accent
			268, 'c', // Capital C, caron accent
			269, 'c', // Small c, caron accent
			270, 'd', // Capital D, caron accent
			271, 'd', // Small d, caron accent
			272, 'd', // Capital D, with stroke accent
			273, 'd', // Small d, with stroke accent
			274, 'e', // Capital E, macron accent
			275, 'e', // Small e, macron accent
			276, 'e', // Capital E, breve accent
			277, 'e', // Small e, breve accent
			278, 'e', // Capital E, dot accent
			279, 'e', // Small e, dot accent
			280, 'e', // Capital E, ogonek accent
			281, 'e', // Small e, ogonek accent
			282, 'e', // Capital E, caron accent
			283, 'e', // Small e, caron accent
			284, 'g', // Capital G, circumflex accent
			285, 'g', // Small g, circumflex accent
			286, 'g', // Capital G, breve accent
			287, 'g', // Small g, breve accent
			288, 'g', // Capital G, dot accent
			289, 'g', // Small g, dot accent
			290, 'g', // Capital G, cedilla accent
			291, 'g', // Small g, cedilla accent
			292, 'h', // Capital H, circumflex accent
			293, 'h', // Small h, circumflex accent
			294, 'h', // Capital H, with stroke accent
			295, 'h', // Small h, with stroke accent
			296, 'i', // Capital I, tilde accent
			297, 'i', // Small I, tilde accent
			298, 'i', // Capital I, macron accent
			299, 'i', // Small i, macron accent
			300, 'i', // Capital I, breve accent
			301, 'i', // Small i, breve accent
			302, 'i', // Capital I, ogonek accent
			303, 'i', // Small i, ogonek accent
			304, 'i', // Capital I, dot accent
			305, 'i', // Small dotless i
			306, 'i', // Capital ligature IJ
			307, 'i', // Small ligature IJ
			308, 'j', // Capital J, circumflex accent
			309, 'j', // Small j, circumflex accent
			310, 'k', // Capital K, cedilla accent
			311, 'k', // Small k, cedilla accent
			312, 'k', // Small Kra
			313, 'l', // Capital L, acute accent
			314, 'l', // Small l, acute accent
			315, 'l', // Capital L, cedilla accent
			316, 'l', // Small l, cedilla accent
			317, 'l', // Capital L, caron accent
			318, 'l', // Small L, caron accent
			319, 'l', // Capital L, middle dot accent
			320, 'l', // Small l, middle dot accent
			321, 'l', // Capital L, with stroke accent
			322, 'l', // Small l, with stroke accent
			323, 'n', // Capital N, acute accent
			324, 'n', // Small n, acute accent
			325, 'n', // Capital N, cedilla accent
			326, 'n', // Small n, cedilla accent
			327, 'n', // Capital N, caron accent
			328, 'n', // Small n, caron accent
			329, 'n', // Small N, preceded by apostrophe
			330, 'n', // Capital Eng
			331, 'n', // Small Eng
			332, 'o', // Capital O, macron accent
			333, 'o', // Small o, macron accent
			334, 'o', // Capital O, breve accent
			335, 'o', // Small o, breve accent
			336, 'o', // Capital O, with double acute accent
			337, 'o', // Small O, with double acute accent
			338, 'o', // Capital Ligature OE
			339, 'o', // Small Ligature OE
			340, 'r', // Capital R, acute accent
			341, 'r', // Small R, acute accent
			342, 'r', // Capital R, cedilla accent
			343, 'r', // Small r, cedilla accent
			344, 'r', // Capital R, caron accent
			345, 'r', // Small r, caron accent
			346, 's', // Capital S, acute accent
			347, 's', // Small s, acute accent
			348, 's', // Capital S, circumflex accent
			349, 's', // Small s, circumflex accent
			350, 's', // Capital S, cedilla accent
			351, 's', // Small s, cedilla accent
			352, 's', // Capital S, caron accent
			353, 's', // Small s, caron accent
			354, 't', // Capital T, cedilla accent
			355, 't', // Small t, cedilla accent
			356, 't', // Capital T, caron accent
			357, 't', // Small t, caron accent
			358, 't', // Capital T, with stroke accent
			359, 't', // Small t, with stroke accent
			360, 'u', // Capital U, tilde accent
			361, 'u', // Small u, tilde accent
			362, 'u', // Capital U, macron accent
			363, 'u', // Small u, macron accent
			364, 'u', // Capital U, breve accent
			365, 'u', // Small u, breve accent
			366, 'u', // Capital U with ring above
			367, 'u', // Small u with ring above
			368, 'u', // Capital U, double acute accent
			369, 'u', // Small u, double acute accent
			370, 'u', // Capital U, ogonek accent
			371, 'u', // Small u, ogonek accent
			372, 'w', // Capital W, circumflex accent
			373, 'w', // Small w, circumflex accent
			374, 'y', // Capital Y, circumflex accent
			375, 'y', // Small y, circumflex accent
			376, 'y', // Capital Y, diaeresis accent
			377, 'z', // Capital Z, acute accent
			378, 'z', // Small z, acute accent
			379, 'z', // Capital Z, dot accent
			380, 'z', // Small Z, dot accent
			381, 'z', // Capital Z, caron accent
			382, 'z', // Small z, caron accent
			383, 's' // Small long s (MAX_DIACRITICAL)
	};

	// =========================================================================
	// Properties
	// =========================================================================
	private Reader reader;
	private int pushBack;

	// =========================================================================
	// Public methods
	// =========================================================================

	/**
	 * Determines if the specified character is a diacritical character.
	 *
	 * @param c_
	 *            the UTF-16 character code to test
	 *
	 * @return Returns {@code true} if the specified is a diacritical character; {@code false} otherwise.
	 */
	public static boolean isDiacritical(final int c_)
	{
		return MIN_DIACRITICAL <= c_ && c_ <= MAX_DIACRITICAL && mapDiacriticalToASCII(c_) != c_;
	}

	/**
	 * Determines if the specified character is punctuation.
	 *
	 * @param c_
	 *            the UTF-16 character code to test
	 *
	 * @return Returns {@code true} if the character is punctuation; {@code false} otherwise.
	 *
	 * @see #PUNCTUATION
	 */
	public static boolean isPunctuation(final int c_)
	{
		return Arrays.binarySearch(PUNCTUATION, c_) >= 0;
	}

	/**
	 * Determines if the specified character is white space. A character is white space if it is one of the following
	 * characters: {@link #TAB}, {@link #LF}, {@link #VTAB}, {@link #FF}, {@link #CR}, {@link #FILESEP},
	 * {@link #GROUPSEP}, {@link #RECORDSEP}, {@link #SPACE}, {@link #NBSP}, {@link #FIGURESPACE}, or {@link #NNBSP}.
	 *
	 * <p>
	 * Note: This method does not handle supplementary characters. That is, supplemental characters (which are
	 * represented by a surrogate pair of UTF-16 characters) are never classified as white space.
	 * </p>
	 *
	 * <p>
	 * Note 2: This method differs functionally from {@link Character#isWhitespace(char)} in that this method treats
	 * non-breaking white spaces as white space, whereas {@link Character#isWhitespace(char)} does not.
	 * </p>
	 *
	 * @param c_
	 *            the UTF-16 character code to test
	 *
	 * @return Returns {@code true} if the character is white space; {@code false} otherwise.
	 */
	public static boolean isWhitespace(final int c_)
	{
		// This method assumes knowledge of character code values to optimize performance.
		// Use cautions when editing.

		if (c_ > SPACE)
		{
			return c_ == NBSP || c_ == FIGURESPACE || c_ == NNBSP;
		}

		return FILESEP <= c_ || TAB <= c_ && c_ <= CR;
	}

	/**
	 * Map the specified specified character to ASCII if it is a diacritical character.
	 *
	 * @param c_
	 *            the UTF-16 character code map
	 *
	 * @return Returns the ASCII mapping if the specified character is a diacritical character; otherwise, the specified
	 *         character itself is returned unchanged.
	 *
	 * @see #isDiacritical(int)
	 * @see #DIACRITICAL_TO_ASCII_MAP
	 */
	public static int mapDiacriticalToASCII(final int c_)
	{
		return MIN_DIACRITICAL <= c_ && c_ <= MAX_DIACRITICAL
				? DIACRITICAL_TO_ASCII_MAP[2 * (c_ - MIN_DIACRITICAL) + 1]
				: c_;
	}

	// =========================================================================
	// Abstract methods
	// =========================================================================
	/**
	 * Gets the {@link Reader} for this text source.
	 *
	 * @return Returns a {@link Reader} that is open and ready to read the characters for this text source.
	 *
	 * @throws IOException
	 *             if an error occurred getting the reader
	 */
	abstract protected Reader getReader() throws IOException;

	// =========================================================================
	// AbstractTextSource abstract methods
	// =========================================================================
	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.AbstractTextSource#closeImpl()
	 */
	@Override
	protected void closeImpl() throws IOException
	{
		try
		{
			reader.close();
		}
		finally
		{
			reader = null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.AbstractTextSource#openImpl()
	 */
	@Override
	protected void openImpl() throws IOException
	{
		reader = new BufferedReader(getReader());
		pushBack = -1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.dell.mensa.impl.generic.AbstractTextSource#readImpl()
	 */
	@Override
	protected Character readImpl(final ITailBuffer<Character> buffer_) throws IOException
	{
		final int c = rch();
		if (c == -1)
		{
			return null; // eof reached
		}

		final Character symbol = Character.valueOf((char) c);
		buffer_.add(symbol);

		return isWordBreak(c)
				? p_wordBreak(c, buffer_)
				: symbol;
	}

	// =========================================================================
	// Internal methods
	// =========================================================================
	/**
	 * @param c_
	 *            specifies the UTF-16 white space character that begins a run of one or more white space characters.
	 *
	 * @param buffer_
	 *            specifies the tail buffer to record additional white space characters that may be consumed by this
	 *            method.
	 *
	 * @return Returns the effective character used to represent this run of one or more white space characters.
	 *
	 * @throws IOException
	 *             if there is an error reading the text source
	 */
	private Character p_wordBreak(final int c_, final ITailBuffer<Character> buffer_) throws IOException
	{
		assert isWordBreak(c_);

		int nNewlines = c_ == LF ? 1 : 0;
		int nFormfeeds = c_ == FF ? 1 : 0;

		int c = rch();
		while (isWordBreak(c))
		{
			buffer_.add((char) c);

			if (c == LF)
			{
				++nNewlines;
			}
			else if (c == FF)
			{
				++nFormfeeds;
			}

			c = rch();
		}

		pushBack = c;

		return nFormfeeds > 0
				? FF
				: nNewlines > 1
						? LF
						: SPACE;
	}

	private static boolean isWordBreak(final int c_)
	{
		return isWhitespace(c_) || isPunctuation(c_);
	}

	/**
	 * Reads the next character from the reader (or the pushback buffer).
	 *
	 * @return Returns the next character.
	 *
	 * @throws IOException
	 *             if an error occurs reading the next character from the reader.
	 */
	private int rch() throws IOException
	{
		final int c;
		if (pushBack == -1)
		{
			c = reader.read();
		}
		else
		{
			c = pushBack;
			pushBack = -1;
		}
		return c;
	}
}
