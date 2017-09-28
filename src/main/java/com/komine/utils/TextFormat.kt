package com.komine.utils

import org.fusesource.jansi.Ansi

enum class TextFormat(val char: Char, val code: Int, val ansiVariant: String, val isColor: Boolean = true) {
	/**
	 * Black color
	 */
	BLACK('0', 0x00, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString()),
	/**
	 * Dark blue color
	 */
	DARK_BLUE('1', 0x1, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString()),
	/**
	 * Dark green color
	 */
	DARK_GREEN('2', 0x2, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString()),
	/**
	 * Dark blue (aqua) color
	 */
	DARK_AQUA('3', 0x3, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString()),
	/**
	 * Dark red color
	 */
	DARK_RED('4', 0x4, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString()),
	/**
	 * Dark purple color
	 */
	DARK_PURPLE('5', 0x5, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString()),
	/**
	 * Gold color
	 */
	GOLD('6', 0x6, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString()),
	/**
	 * Gray color
	 */
	GRAY('7', 0x7, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString()),
	/**
	 * Dark gray color
	 */
	DARK_GRAY('8', 0x8, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString()),
	/**
	 * Blue color
	 */
	BLUE('9', 0x9, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString()),
	/**
	 * Green color
	 */
	GREEN('a', 0xA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString()),
	/**
	 * Aqua color
	 */
	AQUA('b', 0xB, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString()),
	/**
	 * Red color
	 */
	RED('c', 0xC, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString()),
	/**
	 * Light purple color
	 */
	LIGHT_PURPLE('d', 0xD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString()),
	/**
	 * Yellow color
	 */
	YELLOW('e', 0xE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString()),
	/**
	 * White color
	 */
	WHITE('f', 0xF, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString()),

	/**
	 * Makes the text obfuscated
	 */
	OBFUSCATED('k', 0x10, "", false),
	/**
	 * Makes the text bold.
	 */
	BOLD('l', 0x11, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString(), false),
	/**
	 * Makes a line appear through the text.
	 */
	STRIKETHROUGH('m', 0x12, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString(), false),
	/**
	 * Makes the text appear underlined.
	 */
	UNDERLINE('n', 0x13, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString(), false),
	/**
	 * Makes the text italic.
	 */
	ITALIC('o', 0x14, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString(), false),
	/**
	 * Resets all previous chat colors or formats.
	 */
	RESET('r', 0x15, Ansi.ansi().a(Ansi.Attribute.RESET).toString(), false);

	val regex get() = "(?i)$ESCAPE$char".toRegex()
	override fun toString() = "$ESCAPE$char"

	companion object {
		val ESCAPE = '\u00A7'
		val REGEX_CLEAN = "(?i)$ESCAPE[0-9A-FK-OR]".toRegex()
		val BY_CODE = mutableMapOf<Int, TextFormat>().apply {
			for (format in values()) {
				put(format.code, format)
			}
		}.toMap()
		val BY_CHAR = mutableMapOf<Char, TextFormat>().apply {
			for (format in values()) {
				put(format.char, format)
			}
		}.toMap()

		fun clean(input: String) = input.replace(REGEX_CLEAN, "")

		fun ansi(input: String): String {
			if (!input.contains(ESCAPE)) {
				return input
			}
			var inputClone = input
			values().forEach {
				inputClone = inputClone.replace(it.regex, it.ansiVariant)
			}
			return inputClone + Ansi.ansi().reset()
		}
	}
}
