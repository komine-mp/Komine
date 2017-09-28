package com.komine.utils.log

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE

class FileLoggerAttachment(val file: Path) : LoggerAttachment {
	override fun send(messages: List<LogEntry>) {
		Files.newBufferedWriter(file, APPEND, CREATE).use { writer ->
			with(writer) {
				messages.forEach {
					append(it.cleanMessage)
					newLine()
				}
			}
		}
	}
}
