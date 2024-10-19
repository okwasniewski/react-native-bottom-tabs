package com.rcttabview

import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.EventDispatcher

class EventDispatcherWrapper(context: ReactContext) {
  var context: ReactContext = context
  fun dispatchEvent(event: Event<PageSelectedEvent>) {
    UIManagerHelper.getEventDispatcherForReactTag(context, event.viewTag)?.dispatchEvent(event)
  }
}
