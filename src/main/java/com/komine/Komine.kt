package com.komine

import com.komine.utils.log.FileLoggerAttachment
import com.komine.utils.log.MainLogger
import com.komine.utils.log.debug
import com.komine.utils.log.info
import org.fusesource.jansi.Ansi
import java.nio.file.Paths

object Komine {
	val NAME = "Komine"
	val VERSION = "0.0.1dev"
	val API_VERSION = "0.0.1"
}

fun main(args: Array<String>) {
	val root = Paths.get("")

	Thread.currentThread().name = "Main"

	MainLogger.addAttachment(FileLoggerAttachment(root.resolve("server.log")))

	// TODO: Ability to specify these paths
	Server(MainLogger, root, root.resolve("plugins"))

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
