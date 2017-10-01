package com.komine.scheduler

import com.komine.utils.log.MainLogger
import com.komine.utils.log.critical
import com.komine.utils.log.error

class TaskHandler(val task: Task, val taskId: Int, val delay: Int = -1, val period: Int = -1) {
	var nextRun: Int = -1
	var isCancelled = false
		private set
	val isDelayed get() = delay != -1
	val isRepeating get() = period != -1
	val taskName = task::class.simpleName

	init {
		task.handler = this
	}

	fun cancel() {
		try {
			if (!isCancelled) {
				task.onCancel()
			}
		} catch (e: Throwable) {
			MainLogger.critical(e) { "While cancelling task $taskName" }
		} finally {
			remove()
		}
	}

	fun remove() {
		isCancelled = true
		task.handler = null
	}

	fun run(tick: Int) = try {
		task.onRun(tick)
	} catch (e: Throwable) {
		MainLogger.critical(e) { "Could not execute task $taskName:" }
	}

	object Comparator : java.util.Comparator<TaskHandler> {
		override fun compare(a: TaskHandler, b: TaskHandler) = a.nextRun - b.nextRun
	}
}
