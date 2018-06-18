package ru.itbasis.kotlin.utils

fun String.toBoolean() = when (this.toLowerCase()) {
	"y", "yes", "true" -> true
	"n", "no", "false" -> false
	else -> throw IllegalArgumentException("unsupported value: '$this'")
}
