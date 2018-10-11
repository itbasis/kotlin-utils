package ru.itbasis.kotlin.utils.properties

interface WithPropertyListeners<Event : Enum<Event>> {
  fun <T> addBeforeEventListener(
    eventType: Event,
    listener: PropertyListener<T>
                                )

  fun <T> addAfterEventListener(
    eventType: Event,
    listener: PropertyListener<T>
                               )

  fun removeAllEventListeners()
}
