package com.komine.utils.config

@Suppress("UNCHECKED_CAST")
abstract class ConfigSection(init: Map<String, Any?> = emptyMap()) {
	protected val map = LinkedHashMap<String, Any?>()

	init {
		for ((key, value) in init) {
			set(key, when (value) {
				is Map<*, *> -> MapConfigSection(value as Map<String, Any>)
				is ConfigSection -> MapConfigSection(value.map)
				else -> value
			})
		}
	}

	fun toMap() = map.toMap()

	/**
	 * @return value of key or default
	 */
	operator fun <T : Any?> get(key: String, default: T? = null): T? =
		(map[key] ?: run {
			val section = findSection(sectionOfKey(key)) ?: return null
			val subKey = endOfKey(key)
			return section.map[subKey] as T?
		} ?: default) as T

	/**
	 * Set value of key to the value
	 */
	operator fun <T : Any?> set(key: String, value: T): Any? {
		val section = section(sectionOfKey(key))
		val subKey = endOfKey(key)
		return section.map.put(subKey, value)
	}

	/**
	 * If property does not exists, set it to default value
	 * @see get
	 * @see set
	 * @return value by key
	 */
	fun <T : Any?> prop(key: String, default: () -> T): T {
		val section = section(sectionOfKey(key))
		val subKey = endOfKey(key)
		return section.map.getOrPut(subKey, default) as T
	}

	/**
	 * Searches or depth sections path
	 * @return section on given path or null if not exists
	 */
	fun findSection(path: String): ConfigSection? {
		if (path.isEmpty()) {
			return this
		}
		var currentSection = this
		for (key in path.split('.')) {
			val next = currentSection.map[key] as? ConfigSection ?: return null
			currentSection = next
		}
		return currentSection
	}

	/**
	 * Searches or creates depth sections path
	 * @return section on given path
	 */
	fun section(path: String): ConfigSection {
		if (path.isEmpty()) {
			return this
		}
		var section = this
		for (key in path.split('.')) {
			section = section.map.getOrPut(key, { MapConfigSection() }) as MapConfigSection
		}
		return section
	}

	/**
	 * Searches or creates depth sections path and executes lambda on it
	 * @see section
	 */
	inline fun section(key: String, lambda: ConfigSection.() -> Unit): ConfigSection {
		val section = section(key)
		section.lambda()
		return section
	}

	protected fun sectionOfKey(key: String) = key.substringBeforeLast('.', "")
	protected fun endOfKey(key: String) = key.substringAfterLast('.')

	fun clone() = MapConfigSection(map)
}
