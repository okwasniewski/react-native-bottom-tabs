package com.swiftuitabview

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter


class PageSelectedEvent(viewTag: Int, private val key: String) : Event<PageSelectedEvent>(viewTag) {
  override fun getEventName(): String {
    return EVENT_NAME
  }

  override fun canCoalesce(): Boolean {
    return false
  }

  override fun dispatch(rctEventEmitter: RCTEventEmitter) {
    rctEventEmitter.receiveEvent(viewTag, eventName, serializeEventData())
  }

  private fun serializeEventData(): WritableMap {
    val eventData = Arguments.createMap()
    eventData.putString("key", key)
    return eventData
  }

  companion object {
    const val EVENT_NAME = "topPageSelected"
  }

}
