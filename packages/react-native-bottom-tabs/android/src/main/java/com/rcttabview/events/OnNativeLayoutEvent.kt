package com.rcttabview.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter

class OnNativeLayoutEvent(viewTag: Int, private val width: Double, private val height: Double) :
  Event<TabLongPressEvent>(viewTag) {

  companion object {
    const val EVENT_NAME = "onNativeLayout"
  }

  override fun getEventName(): String {
    return EVENT_NAME
  }

  override fun dispatch(rctEventEmitter: RCTEventEmitter) {
    val event = Arguments.createMap().apply {
      putDouble("width", width)
      putDouble("height", height)
    }
    rctEventEmitter.receiveEvent(viewTag, eventName, event)
  }
}
