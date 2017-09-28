package com.komine.utils.log

class LogEntry(val message: String) {
	val ansiMessage by lazy { com.komine.utils.TextFormat.Companion.ansi(message) }
	val cleanMessage by lazy { com.komine.utils.TextFormat.Companion.clean(message) }
}
