package com.komine.utils.log

interface Logger {
	fun enabled(level: LogLevel = LogLevel.OFF): Boolean

	fun log(level: LogLevel, throwable: Throwable? = null, message: String)
}

inline fun Logger.log(level: LogLevel, throwable: Throwable?, message: () -> String) {
	if (enabled(level)) {
		log(level, throwable, message())
	}
}

inline fun Logger.emergency(throwable: Throwable? = null, message: () -> String) = log(LogLevel.EMERGENCY, throwable, message)
inline fun Logger.alert(throwable: Throwable? = null, message: () -> String) = log(LogLevel.ALERT, throwable, message)
inline fun Logger.critical(throwable: Throwable? = null, message: () -> String) = log(LogLevel.CRITICAL, throwable, message)
inline fun Logger.error(throwable: Throwable? = null, message: () -> String) = log(LogLevel.ERROR, throwable, message)
inline fun Logger.warning(throwable: Throwable? = null, message: () -> String) = log(LogLevel.WARNING, throwable, message)
inline fun Logger.notice(throwable: Throwable? = null, message: () -> String) = log(LogLevel.NOTICE, throwable, message)
inline fun Logger.info(throwable: Throwable? = null, message: () -> String) = log(LogLevel.INFO, throwable, message)
inline fun Logger.debug(throwable: Throwable? = null, message: () -> String) = log(LogLevel.DEBUG, throwable, message)
