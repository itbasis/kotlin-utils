package ru.itbasis.kotlin.utils

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.row
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal class IOStreamExtensionsKtTest : FunSpec({
	test("copyTo") {
		forall(
			row(1, 1L, 100, 100, 100),
			row(2, 1L, 100, 50, 100),
			row(3, 1L, 100, 34, 100),
			row(1, 2L, 100, 50, 99),
			row(1, 3L, 100, 34, 100),
			row(3, 2L, 100, 34, 100),
			row(3, 4L, 100, 25, 99),
			row(1, 3L, 300, 100, 298),
			row(2, 3L, 300, 100, 298),
			row(3, 3L, 300, 100, 300),
			row(3, 4L, 300, 75, 297),
			row(3, 5L, 300, 60, 297),
			row(3, 6L, 300, 50, 297),
			row(4, 3L, 300, 75, 300),
			row(5, 3L, 300, 60, 300),
			row(6, 3L, 300, 50, 300),
			row(6, 7L, 300, 43, 300),
			row(6, 8L, 300, 38, 300)
		) { bufferSize, listenerStep, arraySize, expectedCount, expectedListenerLastReading ->
			val byteArray = ByteArray(arraySize)
			byteArray.fill(1)
			val isValue = ByteArrayInputStream(byteArray)

			//
			val actualOutputStream = ByteArrayOutputStream()
			var actualCount = 0
			var actualListenerLastReading: Long? = null
			val actualCopied =
				isValue.copyTo(actualOutputStream, bufferSize, listenerStep) { sizeCurrent ->
					actualListenerLastReading = sizeCurrent
					actualCount++
					return@copyTo true
				}
			val actualByteArray = actualOutputStream.toByteArray()
			actualByteArray.size shouldBe arraySize
			actualByteArray shouldBe byteArray
			actualCount shouldBe expectedCount
			actualListenerLastReading shouldBe expectedListenerLastReading
			actualCopied shouldBe arraySize

			//
			isValue.reset()
			val actualKotlinOutputStream = ByteArrayOutputStream()
			val actualKotlinCopied = isValue.copyTo(actualKotlinOutputStream, bufferSize)
			val actualKotlin = actualKotlinOutputStream.toByteArray()
			actualKotlin shouldBe byteArray
			actualKotlin shouldBe actualByteArray
			actualKotlinCopied shouldBe arraySize
		}
	}
})
