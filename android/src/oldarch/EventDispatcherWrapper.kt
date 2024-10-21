package com.rcttabview

import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.EventDispatcher

class EventDispatcherWrapper(context: ReactContext) {
  var eventDispatcher:EventDispatcher = context.getNativeModule(UIManagerModule::class.java)!!.eventDispatcher

  fun dispatchEvent(event: Event<PageSelectedEvent>){
    eventDispatcher.dispatchEvent(event)
  }
}
