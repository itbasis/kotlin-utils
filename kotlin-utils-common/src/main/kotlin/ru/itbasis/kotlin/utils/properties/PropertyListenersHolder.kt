package ru.itbasis.kotlin.utils.properties

@Suppress("unused")
abstract class PropertyListenersHolder<Event : Enum<Event>> : WithPropertyListeners<Event> {

  @Suppress("VariableNaming", "PropertyName")
  protected abstract val _propertyListeners: MutableMap<Event, PropertyListeners<out Any?>>

  fun enablePropertyListeners(
    eventType: Event
                             ) {
    _propertyListeners[eventType]?.silentMode = false
  }

  fun disablePropertyListeners(
    eventType: Event
                              ) {
    _propertyListeners[eventType]?.silentMode = true
  }

  override fun <T> addBeforeEventListener(
    eventType: Event,
    listener: PropertyListener<T>
                                         ) {
    _propertyListeners[eventType]!!.addBeforeListener(listener)
  }

  override fun <T> addAfterEventListener(
    eventType: Event,
    listener: PropertyListener<T>
                                        ) {
    _propertyListeners[eventType]!!.addAfterListener(listener)
  }

  override fun removeAllEventListeners() {
    _propertyListeners.forEach { (_, listeners) ->
      listeners.cleanAllListeners()
    }
  }
}
