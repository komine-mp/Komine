package com.komine.command

import com.komine.InterruptibleThread
import com.komine.utils.log.LogEntry
import com.komine.utils.log.LoggerAttachment
import com.komine.utils.log.PrintStreamLoggerAttachment
import jline.console.ConsoleReader
import jline.console.CursorBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class StashingCommandReader(
	val reader: ConsoleReader = ConsoleReader(),
	val attachment: LoggerAttachment = PrintStreamLoggerAttachment(System.out),
	val prompt: String = "> ") : LoggerAttachment, CommandReader {
	private val commandsQueue = ConcurrentLinkedQueue<String>()

	init {
		object : Thread("CommandReader"), InterruptibleThread {
			override fun run() {
				try {
					while (true) {
						commandsQueue.add(reader.readLine())
					}
				} catch (_: InterruptedException) {
				}
			}
		}.start()
		reader.prompt = "> "
	}

	override fun send(messages: List<LogEntry>) {
		val stashed = stash()
		try {
			attachment.send(messages)
		} finally {
			unstash(stashed)
		}
	}

	override fun readLine(): String? = commandsQueue.poll()

	private fun stash() = reader.cursorBuffer.copy().also {
		reader.output.write("\u001b[1G\u001b[K")
		reader.flush()
	}!!

	private fun unstash(stash: CursorBuffer) = reader.resetPromptLine(prompt, stash.toString(), stash.cursor)
}
