package com.komine.timings

import java.io.PrintWriter

class TimingsHandler(val timings: Timings, val name: String, val parent: TimingsHandler? = null) {
	init {
		timings.addHandler(this)
	}

	private var count = 0
	private var curCount = 0
	private var start = 0L
	private var timingDepth = 0
	private var totalTime = 0L
	private var curTickTotal = 0L
	private var violations = 0

	fun tick(measure: Boolean) {
		if (measure) {
			if (curTickTotal > 0.05) {
				violations += Math.round(curTickTotal / 0.05).toInt()
			}
		} else {
			totalTime -= curTickTotal
			count -= curCount
		}

		curTickTotal = 0
		curCount = 0
		timingDepth = 0
	}

	fun start() {
		if (timings.enabled && ++timingDepth == 1) {
			start = System.nanoTime()
			parent?.start()
		}
	}

	fun stop() {
		if (timings.enabled && --timingDepth == 0 && start != 0L) {
			val diff = System.nanoTime() - start
			totalTime += diff
			curTickTotal += diff
			++curCount
			++count
			start = 0
			parent?.stop()
		}
	}

	fun use(lambda: () -> Unit) {
		start()
		try {
			lambda()
		} finally {
			stop()
		}
	}

	fun print(writer: PrintWriter) = with(writer) {
		if (count == 0) {
			return
		}
		print("    ")
		print(name)
		print(" Time: ")
		print(totalTime)
		print(" Count: ")
		print(count)
		print(" Avg: ")
		print(Math.round(totalTime / count.toDouble()))
		print(" Violations: ")
		print(violations)
		println()
	}
}
