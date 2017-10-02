package com.komine.scheduler

abstract class Task {
	var handler: TaskHandler? = null
	val taskId get() = handler?.taskId ?: throw kotlin.IllegalStateException("Task is removed")

	abstract fun onRun(tick: Int)

	fun onCancel() {

	}
}
