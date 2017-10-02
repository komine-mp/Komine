package com.komine.utils.log

import com.komine.utils.TextFormat
import com.komine.utils.exceptionMessage
import org.fusesource.jansi.AnsiConsole
import java.nio.channels.ClosedByInterruptException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListSet

object MainLogger : Thread("MainLogger"), Logger {
	private val DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	private val logQueue = ConcurrentLinkedQueue<LogEntry>()
	private val attachments = ConcurrentSkipListSet<LoggerAttachment>()

	@Volatile private var shutdown = false
	private val lock = java.lang.Object()

	var enabled = true
	var logLevel = LogLevel.INFO

	init {
		start()
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
		throwable?.let {
			logQueue.add(LogEntry(exceptionMessage(it)))
		}

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
		if (!shutdown && logQueue.isEmpty()) {
			try {
				synchronized(lock) {
					lock.wait(25000)
				}
				Thread.sleep(5)
			} catch (_: InterruptedException) {
			} catch (_: ClosedByInterruptException) {
			}
		}
		val messages = mutableListOf<LogEntry>()
		while (logQueue.isNotEmpty()) {
			messages.add(logQueue.poll())
		}
		if (messages.isNotEmpty()) {
			attachments.forEach { it.send(messages) }
		}
	}
}
