package com.rcttabview

import android.view.View
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager

abstract class RCTTabViewViewManagerSpec<T : View> : SimpleViewManager<T>() {
  abstract fun setColor(view: T?, value: String?)
}
