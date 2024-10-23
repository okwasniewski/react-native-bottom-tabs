package com.rcttabview

import android.content.res.ColorStateList
import android.view.View.MeasureSpec
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.yoga.YogaMeasureFunction
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput
import com.facebook.yoga.YogaNode
import com.facebook.react.bridge.ReactApplicationContext



class RCTTabViewManager(context: ReactApplicationContext) :
  SimpleViewManager<ReactBottomNavigationView>() {
  private lateinit var eventDispatcher: EventDispatcher
  private var tabViewImpl = RCTTabViewImpl()
  override fun getName(): String {
    return NAME
  }

  @ReactProp(name = "items")
  fun setItems(view: ReactBottomNavigationView, items: ReadableArray) {
    tabViewImpl.setItems(view, items)
  }

  @ReactProp(name = "selectedPage")
  fun setSelectedPage(view: ReactBottomNavigationView, key: String) {
    tabViewImpl.setSelectedPage(view, key)
  }


  @ReactProp(name = "labeled")
  fun setLabeled(view: ReactBottomNavigationView, flag: Boolean?) {
    tabViewImpl.setLabeled(view, flag)
  }

  @ReactProp(name = "icons")
  fun setIcons(view: ReactBottomNavigationView, icons: ReadableArray?) {
    tabViewImpl.setIcons(view, icons)
  }

  @ReactProp(name = "rippleColor")
  fun setRippleColor(view: ReactBottomNavigationView, rippleColor: Int?) {
  tabViewImpl.setRippleColor(view, rippleColor)
  }

  public override fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    return tabViewImpl.createViewInstance(context)
  }

  override fun createShadowNodeInstance(): LayoutShadowNode {
    return TabViewShadowNode(tabViewImpl.getViewInstance())
  }

  companion object {
    const val NAME = "RCTTabView"
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
    )
  }

  // iOS Props

  @ReactProp(name = "sidebarAdaptable")
  fun setSidebarAdaptable(view: ReactBottomNavigationView, flag: Boolean) {
  }

  @ReactProp(name = "ignoresTopSafeArea")
  fun setIgnoresTopSafeArea(view: ReactBottomNavigationView, flag: Boolean) {
  }

  @ReactProp(name = "disablePageAnimations")
  fun setDisablePageAnimations(view: ReactBottomNavigationView, flag: Boolean) {
  }
}
