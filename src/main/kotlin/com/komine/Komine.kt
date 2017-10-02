package com.komine

import com.komine.command.StashingCommandReader
import com.komine.utils.log.FileLoggerAttachment
import com.komine.utils.log.MainLogger
import com.komine.utils.log.debug
import com.komine.utils.log.info
import org.fusesource.jansi.Ansi
import java.nio.file.Paths

object Komine {
	val Name = "Komine"
	val Version = "0.0.1dev"
	val ApiVersion = "0.0.1"

	/**
	 * NOTE: Will be moved away
	 */
	val MinecraftVersion = "v1.2.0.81"
}

fun main(args: Array<String>) {
	val root = Paths.get("")

	Thread.currentThread().name = "Main"

	val commandReader = StashingCommandReader()
	MainLogger.addAttachment(commandReader)
	MainLogger.addAttachment(FileLoggerAttachment(root.resolve("server.log")))

	// TODO: Ability to specify these paths
	Server(MainLogger, commandReader, root, root.resolve("plugins")).run {
		try {
			start()
			tickProcessor()
		} finally {
			stop()
		}
	}

	MainLogger.info { "Stopping other threads..." }

	for (thread in Thread.getAllStackTraces().keys) {
		if (thread is InterruptibleThread && thread.isAlive) {
			MainLogger.debug { "Stopping ${thread::class.simpleName} thread" }
			thread.interrupt()
		}
	}

	MainLogger.info { "Server stopped" }
	MainLogger.shutdown()
	System.out.println(Ansi.ansi().reset())
	System.exit(0)
}
