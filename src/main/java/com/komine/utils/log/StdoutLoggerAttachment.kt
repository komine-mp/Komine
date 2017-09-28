package com.komine.utils.log

object StdoutLoggerAttachment : LoggerAttachment {
	override fun send(messages: List<LogEntry>) {
		// TODO: Check if ansi is available
		messages.forEach { System.out.println(it.ansiMessage) }
	}
}
