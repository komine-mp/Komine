package com.komine.scheduler

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Scheduler {
	private val queue = PriorityQueue<TaskHandler>(TaskHandler.Comparator)
	private val idCounter = AtomicInteger(1)
	private var currentTick = 0

	fun schedule(task: Task) = addTask(task, -1, -1)
	fun scheduleDelayed(task: Task, delay: Int) = addTask(task, delay, -1)
	fun scheduleRepeating(task: Task, period: Int) = addTask(task, -1, period)
	fun scheduleDelayedRepeating(task: Task, delay: Int, period: Int) = addTask(task, delay, period)

	private fun addTask(task: Task, delay: Int, period: Int) = TaskHandler(task, nextId(), delay, period).also { handler ->
		handler.nextRun = currentTick + (if (handler.isDelayed) handler.delay else 0)
		queue.add(handler)
		return handler
	}

	fun tick(tick: Int) {
		currentTick = tick
		while (hasReadyTask()) {
			with(queue.poll()) {
				if (!isCancelled) {
					run(tick)

					if (isRepeating) {
						nextRun = currentTick + period
						queue.add(this)
					} else {
						remove()
					}
				}
			}
		}
	}

	private fun hasReadyTask() = queue.peek()?.nextRun ?: Int.MAX_VALUE <= currentTick
	private fun nextId() = idCounter.getAndIncrement()
}
