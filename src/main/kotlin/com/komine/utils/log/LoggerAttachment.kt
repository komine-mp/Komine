package com.komine.utils.log

interface LoggerAttachment : Comparable<LoggerAttachment> {
	fun send(messages: List<LogEntry>)

	override fun compareTo(other: LoggerAttachment): Int = hashCode() - other.hashCode()
}
