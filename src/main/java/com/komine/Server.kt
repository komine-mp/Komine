package com.komine

import com.komine.utils.config.Config
import com.komine.utils.log.Logger
import com.komine.utils.log.info
import java.nio.file.Files
import java.nio.file.Path

class Server(val logger: Logger, val dataPath: Path, val pluginPath: Path) {
	private lateinit var config: Config
	lateinit var test: String
		private set

	init {
		Files.createDirectories(dataPath)
		Files.createDirectories(pluginPath)

		logger.info { "Loading komine.yml..." }
		dataPath.resolve("komine.yml").let {
			if (!Files.exists(it)) {
				logger.info { "Creating komine.yml..." }
			}
			config = Config(it)
		}
	}
}
