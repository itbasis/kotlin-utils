package ru.itbasis.kotlin.utils.properties

import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class LazyEnvironmentPropertyTest {
	@AfterEach
	internal fun tearDown() {
		arrayOf("S", "_S", "PREFIX_S").forEach {
			System.clearProperty(it)
		}
	}

	@Test
	fun test_00() {
		val s: String by lazyProperty { "s0" }
		s shouldBe "s0"
	}

	@Test
	fun test_01() {
		System.setProperty("S", "pre")
		val s: String by lazyProperty { "s0" }
		s shouldBe "pre"
	}

	@Test
	fun test_02() {
		System.setProperty("PREFIX_S", "pre")
		val s: String by lazyProperty(prefix = "PREFIX") { "s0" }
		s shouldBe "pre"
	}

	@Test
	fun test_03() {
		System.setProperty("_S", "pre")
		@Suppress("LocalVariableName") val _s: String by lazyProperty { "s0" }
		_s shouldBe "pre"
	}

	@Test
	fun test_04() {
		val s: String by lazyProperty(prefix = "PREFIX") { "s0" }
		System.setProperty("PREFIX_S", "pre")
		s shouldBe "pre"
	}
}
