package com.swiftuitabview

import android.view.View

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.SwiftuiTabviewViewManagerDelegate
import com.facebook.react.viewmanagers.SwiftuiTabviewViewManagerInterface

abstract class SwiftuiTabviewViewManagerSpec<T : View> : SimpleViewManager<T>(), SwiftuiTabviewViewManagerInterface<T> {
  private val mDelegate: ViewManagerDelegate<T>

  init {
    mDelegate = SwiftuiTabviewViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<T>? {
    return mDelegate
  }
}
