package ru.itbasis.kotlin.utils

import klogging.KLogger
import klogging.KLoggers
import klogging.WithLogging
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Parameters can be passed as environment variables.
 * For example, if [prefix] is "PRE" and the field name is "myFieldName", then the environment will look for a parameter named "PRE_MY_FIELD_NAME"
 * @param security if TRUE, then the parameter is hidden when printed in the logger (See [PropertyDelegate.setValue])
 */
fun <T> Any.lazyProperty(
	prefix: String? = null, security: Boolean = false, defaultValue: (() -> T)? = null
): PropertyDelegate<T> = PropertyDelegate(this, prefix, security, defaultValue)

class PropertyDelegate<T>(
	private val self: Any,
	private val prefix: String? = null,
	private val security: Boolean,
	private var initializer: (() -> T)?
) : ReadWriteProperty<Any?, T>, WithLogging {

	override val logger: KLogger
		get() = KLoggers.logger(self)

	private var value: T? = null

	override fun getValue(
		thisRef: Any?, property: KProperty<*>
	): T {
		return value ?: run {
			// FIXME code smell
			val defaultValue = initializer?.invoke()

			val envName =
				arrayOf(prefix, property.name).joinToString("_").replace(REGEXP_ENV_NAME_UNDERSCORE, "_$1")
					.toUpperCase()
			logger.trace { "find environment variable '$envName'" }
			val sysEnvValue: String? = when {
				System.getProperties().containsKey(envName) -> System.getProperty(envName)
				System.getenv().containsKey(envName) -> System.getenv()[envName]
				else -> null
			}
			logger.trace { "$envName=$sysEnvValue" }

			@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY") val envValue = sysEnvValue?.let {
				when (defaultValue) {
					is String -> sysEnvValue
					is Boolean -> sysEnvValue.toBoolean()
					is Long -> sysEnvValue.toLong()
					is Int -> sysEnvValue.toInt()
					is Double -> sysEnvValue.toDouble()
					is Float -> sysEnvValue.toFloat()
					is Short -> sysEnvValue.toShort()
					is Byte -> sysEnvValue.toByte()
					else -> throw IllegalStateException("unsupported data type from field: ${property.name}")
				} as? T
			}
			if (envValue == null && defaultValue == null) {
				throw IllegalStateException("For the field '${property.name}' value has not been found - not found an environment variable '$envName' or default")
			}
			setValue(thisRef, property, envValue ?: defaultValue!!)
			return@run value!!
		}
	}

	@Suppress("IMPLICIT_CAST_TO_ANY")
	override fun setValue(
		thisRef: Any?, property: KProperty<*>, value: T
	) {
		logger.debug { "${property.name}=${if (security) "<PROTECTED DATA>" else value}" }
		this.value = value
	}

	companion object {
		val REGEXP_ENV_NAME_UNDERSCORE = "(?<=.)(?<![A-Z_])([A-Z])".toRegex()
	}
}