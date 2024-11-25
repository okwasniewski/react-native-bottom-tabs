package com.rcttabview.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

class TabLongPressEvent(viewTag: Int, private val key: String) :
  Event<TabLongPressEvent>(viewTag) {

  companion object {
    const val EVENT_NAME = "onTabLongPress"
  }

  override fun getEventName(): String {
    return EVENT_NAME
  }

  override fun dispatch(rctEventEmitter: RCTEventEmitter) {
    val event = Arguments.createMap().apply {
      putString("key", key)
    }
    rctEventEmitter.receiveEvent(viewTag, eventName, event)
  }
}
