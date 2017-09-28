package com.komine.utils.log

import com.komine.utils.TextFormat
import org.fusesource.jansi.AnsiConsole
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListSet

object MainLogger : Thread(), Logger {
	private val DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	private val logQueue = ConcurrentLinkedQueue<LogEntry>()
	private val attachments = ConcurrentSkipListSet<LoggerAttachment>()

	@Volatile private var shutdown = false
	private val lock = java.lang.Object()

	var enabled = true
	var logLevel = LogLevel.INFO

	init {
		start()
		addAttachment(StdoutLoggerAttachment)
	}

	fun addAttachment(attachment: LoggerAttachment) = attachments.add(attachment)
	fun removeAttachment(attachment: LoggerAttachment) = attachments.remove(attachment)

	fun shutdown() {
		shutdown = true
		interrupt()
		join()
	}

	override fun enabled(level: LogLevel): Boolean = enabled && !shutdown && level.ordinal <= logLevel.ordinal

	override fun log(level: LogLevel, throwable: Throwable?, message: String) {
		val entry = LogEntry(run {
			val now = LocalDateTime.now().format(DATETIME_FORMAT)
			val thread = Thread.currentThread().name

			"${TextFormat.AQUA}[$now]${TextFormat.RESET} ${level.color}[$thread thread/$level]: $message${TextFormat.RESET}"
		})

		logQueue.add(entry)
		synchronized(lock) {
			lock.notify()
		}
	}

	override fun run() {
		AnsiConsole.systemInstall()

		while (!shutdown) {
			flush()
		}
		flush()
	}

	private fun flush() {
		if (logQueue.isEmpty()) {
			try {
				synchronized(lock) {
					lock.wait(25000)
				}
				Thread.sleep(5)
			} catch (_: InterruptedException) {
			}
		}
		val messages = mutableListOf<LogEntry>()
		while (logQueue.isNotEmpty()) {
			messages.add(logQueue.poll())
		}
		attachments.forEach { it.send(messages) }
	}
}
