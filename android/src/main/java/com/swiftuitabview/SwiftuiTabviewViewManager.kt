package com.swiftuitabview

import android.graphics.Color
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

@ReactModule(name = SwiftuiTabviewViewManager.NAME)
class SwiftuiTabviewViewManager :
  SwiftuiTabviewViewManagerSpec<SwiftuiTabviewView>() {
  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): SwiftuiTabviewView {
    return SwiftuiTabviewView(context)
  }

  @ReactProp(name = "color")
  override fun setColor(view: SwiftuiTabviewView?, color: String?) {
    view?.setBackgroundColor(Color.parseColor(color))
  }

  companion object {
    const val NAME = "SwiftuiTabviewView"
  }
}
