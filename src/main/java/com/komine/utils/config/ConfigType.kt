package com.komine.utils.config

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.Reader
import java.io.Writer
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class ConfigType(vararg extensions: String) {
	DETECT() {
		fun type(file: Path): ConfigType {
			val extension = file.normalize().toString().substringAfterLast('.')
			return values().find { !it.isDetect && it.extensions.contains(extension) } ?: throw InvalidFormatException(extension)
		}

		override fun save(config: ConfigSection, writer: Writer, file: Path) = type(file).save(config, writer, file)
		override fun load(reader: Reader, file: Path): ConfigSection = type(file).load(reader, file)
	},
	PROPERTIES("properties", "cnf", "conf", "config") {
		val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")!!
		val propertyPattern = "([a-zA-Z0-9-_.]*)=([^\r\n]*)".toRegex()
		val gson = with(GsonBuilder()) {
			disableHtmlEscaping()
			create()
		}!!

		override fun save(config: ConfigSection, writer: Writer, file: Path) {
			writer.appendln("#Properties Config File")
			writer.appendln("#${LocalDateTime.now().format(dateFormat)}")
			for ((key, value) in config.toMap()) {
				writer.append(key, "=")
				when (value) {
					is Boolean -> writer.appendln(if (value) "on" else "off")
					else -> writer.appendln(gson.toJson(value))
				}
			}
		}

		override fun load(reader: Reader, file: Path) = MapConfigSection().apply {
			reader.forEachLine { line ->
				propertyPattern.matchEntire(line)?.let { match ->
					val (_, key, value) = match.groupValues
					val computedValue: Any? = when (value) {
						"on", "true", "yes" -> true
						"off", "false", "no" -> false
						"null" -> null
						else -> {
							try {
								val it = gson.fromJson<JsonPrimitive>(value)
								when {
									it.isNumber -> it.asNumber.let { number ->
										@Suppress("IMPLICIT_CAST_TO_ANY")
										if (number.toLong() in Int.MIN_VALUE..Int.MAX_VALUE) {
											number.toInt()
										} else {
											number.toLong()
										}
									}
									it.isString -> it.asString
									else -> null
								}
							} catch (e: Throwable) {
								e.printStackTrace()
								value
							}
						}
					}
					set(key, computedValue)
				}
			}
		}
	},
	JSON("json", "js") {
		val gson = with(GsonBuilder()) {
			setPrettyPrinting()
			disableHtmlEscaping()

			registerTypeAdapter<ConfigSection> {
				serialize { it.context.typedSerialize(it.src.toMap()) }
			}

			create()
		}!!

		override fun save(config: ConfigSection, writer: Writer, file: Path) =
			gson.toJson(config.toMap(), config.toMap()::class.java, gson.newJsonWriter(writer).apply { setIndent("\t") })

		@Suppress("UNCHECKED_CAST")
		override fun load(reader: Reader, file: Path) = MapConfigSection(gson.fromJson<Map<*, *>>(reader) as Map<String, Any?>)
	},
	YAML("yml", "yaml") {
		val yaml = Yaml(object : Representer() {
			init {
				representers.put(MapConfigSection::class.java, Represent { representData((it as ConfigSection).toMap()) })
				representers.put(Config::class.java, Represent { representData((it as ConfigSection).toMap()) })
			}
		}, DumperOptions().apply {
			defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
			indent = 1
			indicatorIndent = 0
		})

		override fun save(config: ConfigSection, writer: Writer, file: Path) = yaml.dump(config, writer)
		@Suppress("UNCHECKED_CAST")
		override fun load(reader: Reader, file: Path) = MapConfigSection(yaml.loadAs(reader, HashMap::class.java) as Map<String, Any?>)
	},
	ENUM("txt", "list", "enum") {
		override fun save(config: ConfigSection, writer: Writer, file: Path) {
			for ((key, _) in config.toMap()) {
				writer.appendln(key)
			}
		}

		override fun load(reader: Reader, file: Path): ConfigSection {
			val section = MapConfigSection()
			reader.readLines()
				.map { it.trim() }
				.filter { it.isNotEmpty() }
				.forEach { section[it] = true }
			return section
		}
	};

	val defaultExtension = if (extensions.isEmpty()) null else extensions[0]

	@Suppress("CanBePrimaryConstructorProperty")
	val extensions = extensions

	val isDetect = defaultExtension == null

	abstract fun save(config: ConfigSection, writer: Writer, file: Path)
	abstract fun load(reader: Reader, file: Path): ConfigSection
}
