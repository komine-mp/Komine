package com.komine.utils.log

enum class LogLevel(val color: com.komine.utils.TextFormat) {
	OFF(com.komine.utils.TextFormat.BLACK),

	EMERGENCY(com.komine.utils.TextFormat.RED),
	ALERT(com.komine.utils.TextFormat.RED),
	CRITICAL(com.komine.utils.TextFormat.RED),
	ERROR(com.komine.utils.TextFormat.DARK_RED),
	WARNING(com.komine.utils.TextFormat.YELLOW),
	NOTICE(com.komine.utils.TextFormat.AQUA),
	INFO(com.komine.utils.TextFormat.WHITE),
	DEBUG(com.komine.utils.TextFormat.GRAY),
}
