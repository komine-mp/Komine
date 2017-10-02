package com.komine

import com.komine.command.CommandReader
import com.komine.scheduler.Scheduler
import com.komine.timings.Timings
import com.komine.utils.config.Config
import com.komine.utils.log.Logger
import com.komine.utils.log.info
import com.komine.utils.log.warning
import java.nio.file.Files
import java.nio.file.Path

class Server(val logger: Logger, val commandReader: CommandReader, val dataPath: Path, val pluginPath: Path) {
	var isRunning = false
		private set
	var tickCounter = 0
		private set
	var nextTick: Long = 0
		private set

	val timings = Timings()
	val scheduler = Scheduler()

	// Configuration
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
				motd = prop("motd", { "${Komine.Name} Server" })
				serverIp = prop("server-ip", { "0.0.0.0" })
				serverPort = prop("server-port", { 19132 })

				save()
			}
		}
	}

	fun start() {
		isRunning = true
	}

	fun stop() {
		isRunning = false
	}

	fun tickProcessor() {
		nextTick = System.currentTimeMillis()
		while (isRunning) {
			tick()

			val toSleep = nextTick - System.currentTimeMillis() - 1
			if (toSleep >= 0) {
				try {
					Thread.sleep(toSleep, 999999)
				} catch (e: InterruptedException) {
					logger.warning(e) { "Tick processor sleeping interrupted" }
				}
			}
		}
	}

	/**
	 * @return Whether tick was executed or not
	 */
	fun tick(): Boolean {
		val time = System.currentTimeMillis()
		if (time - nextTick < 25) {
			return false
		}

		timings.serverTickTimer.use {
			++tickCounter

			timings.connectionTimer.use {
				// TODO: Process network
			}

			timings.schedulerTimer.use { scheduler.tick(tickCounter) }

			while (true) {
				val line = commandReader.readLine() ?: break
				// TODO: Handle command
			}

			// TODO: Other stuff
		}

		timings.tick(false) // TODO: Check TPS
		// TODO: Ticking analysis

		if (nextTick - time < -1000) {
			nextTick = time
		} else {
			nextTick += 50
		}

		return true
	}
}
