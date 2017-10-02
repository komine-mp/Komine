package com.komine.utils.log

import java.io.PrintStream

open class PrintStreamLoggerAttachment(val printer: PrintStream) : LoggerAttachment {
	override fun send(messages: List<LogEntry>) {
		// TODO: Check if ansi is available
		messages.forEach { printer.println(it.ansiMessage) }
		printer.flush()
	}
}
