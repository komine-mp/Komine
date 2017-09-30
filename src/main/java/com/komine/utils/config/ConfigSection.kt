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

	operator fun <T : Any?> get(key: String, default: T? = null): T? =
		(map[key] ?: run {
			val section = section(sectionOfKey(key)) ?: return null
			val subKey = endOfKey(key)
			return section.map[subKey] as T?
		} ?: default) as T

	operator fun <T : Any?> set(key: String, value: T): Any? {
		val section = ensureSection(sectionOfKey(key))
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
		val section = ensureSection(sectionOfKey(key))
		val subKey = endOfKey(key)
		return section.map.getOrPut(subKey, default) as T
	}

	protected fun sectionOfKey(key: String) = key.substringBeforeLast('.', "")
	protected fun endOfKey(key: String) = key.substringAfterLast('.')
	protected fun section(section: String): ConfigSection? {
		if (section.isEmpty()) {
			return this
		}
		var currentSection = this
		for (key in section.split('.')) {
			val next = currentSection.map[key] as? ConfigSection ?: return null
			currentSection = next
		}
		return currentSection
	}
	protected fun ensureSection(section: String): ConfigSection {
		if (section.isEmpty()) {
			return this
		}
		var currentSection = this
		for (key in section.split('.')) {
			currentSection = currentSection.map.getOrPut(key, { MapConfigSection() }) as MapConfigSection
		}
		return currentSection
	}

	fun clone() = MapConfigSection(map)
}
