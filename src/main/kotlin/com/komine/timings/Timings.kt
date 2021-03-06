package com.komine.timings

import com.komine.Komine
import java.io.PrintWriter

class Timings {
	var enabled = true
	/**
	 * NOTE: Have to be on the top of all TimingsHandler due to initialization order
	 */
	private val handlers = mutableSetOf<TimingsHandler>()

	val fullTickTimer = TimingsHandler(this, "Full Server Tick")
	val serverTickTimer = TimingsHandler(this, "** Full Server Tick", fullTickTimer)
	val memoryManagerTimer = TimingsHandler(this, "Memory Manager")
	val garbageCollectorTimer = TimingsHandler(this, "Garbage Collector", memoryManagerTimer)
	val titleTickTimer = TimingsHandler(this, "Console Title Tick")
	val playerListTimer = TimingsHandler(this, "Player List")
	val playerNetworkTimer = TimingsHandler(this, "Player Network Send")
	val playerNetworkReceiveTimer = TimingsHandler(this, "Player Network Receive")
	val playerChunkOrderTimer = TimingsHandler(this, "Player Order Chunks")
	val playerChunkSendTimer = TimingsHandler(this, "Player Send Chunks")
	val connectionTimer = TimingsHandler(this, "Connection Handler")
	val tickablesTimer = TimingsHandler(this, "Tickables")
	val schedulerTimer = TimingsHandler(this, "Scheduler")
	val chunkIOTickTimer = TimingsHandler(this, "ChunkIOTick")
	val timeUpdateTimer = TimingsHandler(this, "Time Update")
	val serverCommandTimer = TimingsHandler(this, "Server Command")
	val worldSaveTimer = TimingsHandler(this, "World Save")
	val generationTimer = TimingsHandler(this, "World Generation")
	val populationTimer = TimingsHandler(this, "World Population")
	val generationCallbackTimer = TimingsHandler(this, "World Generation Callback")
	val permissibleCalculationTimer = TimingsHandler(this, "Permissible Calculation")
	val permissionDefaultTimer = TimingsHandler(this, "Default Permission Calculation")
	val entityMoveTimer = TimingsHandler(this, "** entityMove")
	val tickEntityTimer = TimingsHandler(this, "** tickEntity")
	val activatedEntityTimer = TimingsHandler(this, "** activatedTickEntity")
	val tickTileEntityTimer = TimingsHandler(this, "** tickTileEntity")
	val timerEntityBaseTick = TimingsHandler(this, "** entityBaseTick")
	val timerLivingEntityBaseTick = TimingsHandler(this, "** livingEntityBaseTick")
	val timerEntityAI = TimingsHandler(this, "** livingEntityAI")
	val timerEntityAICollision = TimingsHandler(this, "** livingEntityAICollision")
	val timerEntityAIMove = TimingsHandler(this, "** livingEntityAIMove")
	val timerEntityTickRest = TimingsHandler(this, "** livingEntityTickRest")
	val schedulerSyncTimer = TimingsHandler(this, "** Scheduler - Sync Tasks") // TODO: Maybe add plugin tasks
	val schedulerAsyncTimer = TimingsHandler(this, "** Scheduler - Async Tasks")
	val playerCommandTimer = TimingsHandler(this, "** playerCommand")
	val craftingDataCacheRebuildTimer = TimingsHandler(this, "** craftingDataCacheRebuild")

	fun addHandler(handler: TimingsHandler) = when {
		handler.timings != this -> throw IllegalStateException("Cannot add handler of other timings")
		!handlers.add(handler) -> throw IllegalStateException("Handler is already added")
		else -> Unit
	}

	fun tick(measure: Boolean) = handlers.forEach { it.tick(measure) }

	fun print(writer: PrintWriter) = with(writer) {
		println("Minecraft")
		handlers.forEach { it.print(writer) }
		println("# Version ${Komine.MinecraftVersion}")
		println("# ${Komine.Name} ${Komine.Version}")

		var entities = 0
		var livingEntities = 0
		println("# Entities $entities")
		println("# LivingEntities $livingEntities")
	}
}
