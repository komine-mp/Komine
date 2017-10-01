package com.komine

import com.komine.utils.config.Config
import com.komine.utils.log.Logger
import com.komine.utils.log.info
import java.nio.file.Files
import java.nio.file.Path

class Server(val logger: Logger, val dataPath: Path, val pluginPath: Path) {
	lateinit var properties: Config
	lateinit var motd: String
	lateinit var serverIp: String
	var serverPort: Int = 19132

	init {
		Files.createDirectories(dataPath)
		Files.createDirectories(pluginPath)

		logger.info { "Loading komine.yml..." }
		dataPath.resolve("komine.yml").let {
			if (!Files.exists(it)) {
				logger.info { "Creating komine.yml..." }
			}
			// TODO: Create and load komine.yml
		}

		logger.info { "Loading server.properties..." }
		dataPath.resolve("server.properties").let {
			if (!Files.exists(it)) {
				logger.info { "Creating server.properties..." }
			}
			properties = Config(it).apply {
				motd = prop("motd", { "${Komine.NAME} Server" })
				serverIp = prop("server-ip", { "0.0.0.0" })
				serverPort = prop("server-port", { 19132 })

				save()
			}
		}
	}

	fun start() {

	}
}
