package ru.itbasis.kotlin.utils.properties

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

typealias PropertyListener<T> = (property: KProperty<*>, oldValue: T, newValue: T) -> Unit

@Suppress("unused")
class PropertyListeners<T>(initialValue: T) : ObservableProperty<T>(initialValue = initialValue) {

  private val beforeListeners = mutableListOf<PropertyListener<*>>()
  private val afterListeners = mutableListOf<PropertyListener<*>>()

  var silentMode: Boolean = false

  fun <T> addBeforeListener(listener: PropertyListener<T>) {
    beforeListeners.add(listener)
  }

  fun <T> addAfterListener(listener: PropertyListener<T>) {
    afterListeners.add(listener)
  }

  fun <T> removeBeforeListener(listener: PropertyListener<T>) {
    beforeListeners.remove(listener)
  }

  fun <T> removeAfterListener(listener: PropertyListener<T>) {
    afterListeners.remove(listener)
  }

  fun cleanAllListeners() {
    beforeListeners.clear()
    afterListeners.clear()
  }

  override fun beforeChange(
    property: KProperty<*>,
    oldValue: T,
    newValue: T
                           ): Boolean {
    if (silentMode) {
      return true
    }

    beforeListeners.forEach { listener ->
      @Suppress("UNCHECKED_CAST") (listener as PropertyListener<T>)(property, oldValue, newValue)
    }
    return super.beforeChange(property, oldValue, newValue)
  }

  override fun afterChange(
    property: KProperty<*>,
    oldValue: T,
    newValue: T
                          ) {
    if (silentMode) {
      return
    }

    afterListeners.forEach { listener ->
      @Suppress("UNCHECKED_CAST") (listener as PropertyListener<T>)(property, oldValue, newValue)
    }
    super.afterChange(property, oldValue, newValue)
  }
}
