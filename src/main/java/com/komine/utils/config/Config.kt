package com.komine.utils.config

import com.komine.utils.log.MainLogger
import com.komine.utils.log.warning
import java.nio.file.Files
import java.nio.file.Path

class Config(file: Path, type: ConfigType = ConfigType.DETECT) : ConfigSection() {
	var file = file
		private set
	var type = type
		private set

	init {
		if (Files.exists(file)) {
			load(file, type)
		}
	}

	fun reload() {
		// TODO:
	}

	fun load(file: Path, type: ConfigType = ConfigType.DETECT) {
		if (!Files.exists(file)) {
			Files.createDirectories(file.parent)
			Files.createFile(file)
		} else {
			Files.newBufferedReader(file).use {
				map.clear()
				try {
					map.putAll(type.load(it, file).toMap())
				} catch (e: Throwable) {
					MainLogger.warning(e) { "Failed to load config from file ${file.normalize()}" }
				}

				this.file = file
				this.type = type
			}
		}
	}

	fun save(file: Path = this.file) {
		Files.newBufferedWriter(file).use { type.save(this, it, file) }
	}
}
