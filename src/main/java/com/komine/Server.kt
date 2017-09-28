package com.komine

import com.komine.utils.log.Logger
import com.komine.utils.log.info
import java.nio.file.Files
import java.nio.file.Path

class Server(val logger: Logger, val dataPath: Path, val pluginPath: Path) {
	init {
		Files.createDirectories(dataPath)
		Files.createDirectories(pluginPath)

		logger.info { "Loading komine.yml..." }

	}
}
