package com.rcttabview

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.ViewGroupManager

@ReactModule(name = RCTTabViewImpl.NAME)
class RCTTabViewManager() : ViewGroupManager<RCTTabView>() {
  private lateinit var eventDispatcher: EventDispatcher
  private var tabViewImpl = RCTTabViewImpl()

  override fun getName(): String {
    return tabViewImpl.getName()
  }

  public override fun createViewInstance(context: ThemedReactContext): RCTTabView {
    eventDispatcher = context.getNativeModule(UIManagerModule::class.java)!!.eventDispatcher
    val view = RCTTabView(context)
    view.onTabSelectedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher.dispatchEvent(PageSelectedEvent(viewTag = view.id, key = it))
      }
    }

    view.onTabLongPressedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher.dispatchEvent(TabLongPressEvent(viewTag = view.id, key = it))
      }
    }
    return view
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return tabViewImpl.getExportedCustomDirectEventTypeConstants()
  }

  @ReactProp(name = "items")
  fun setItems(view: RCTTabView, items: ReadableArray) {
    tabViewImpl.setItems(view, items)
  }

  @ReactProp(name = "selectedPage")
  fun setSelectedPage(view: RCTTabView, key: String) {
    tabViewImpl.setSelectedPage(view, key)
  }


  @ReactProp(name = "labeled")
  fun setLabeled(view: RCTTabView, flag: Boolean?) {
    tabViewImpl.setLabeled(view, flag)
  }

  @ReactProp(name = "icons")
  fun setIcons(view: RCTTabView, icons: ReadableArray?) {
    tabViewImpl.setIcons(view, icons)
  }

  @ReactProp(name = "barTintColor", customType = "Color")
  fun setBarTintColor(view: RCTTabView, color: Int?) {
    tabViewImpl.setBarTintColor(view, color)
  }

  @ReactProp(name = "rippleColor", customType = "Color")
  fun setRippleColor(view: RCTTabView, rippleColor: Int?) {
    tabViewImpl.setRippleColor(view, rippleColor)
  }

  @ReactProp(name = "activeTintColor", customType = "Color")
  fun setActiveTintColor(view: RCTTabView, color: Int?) {
    tabViewImpl.setActiveTintColor(view, color)
  }

  @ReactProp(name = "inactiveTintColor", customType = "Color")
  fun setInactiveTintColor(view: RCTTabView, color: Int?) {
    tabViewImpl.setInactiveTintColor(view, color)
  }

  @ReactProp(name = "activeIndicatorColor", customType = "Color")
  fun setActiveIndicatorColor(view: RCTTabView, color: Int?) {
    tabViewImpl.setActiveIndicatorColor(view, color)
  }

  // iOS Props

  @ReactProp(name = "sidebarAdaptable")
  fun setSidebarAdaptable(view: RCTTabView, flag: Boolean) {
  }

  @ReactProp(name = "ignoresTopSafeArea")
  fun setIgnoresTopSafeArea(view: RCTTabView, flag: Boolean) {
  }

  @ReactProp(name = "disablePageAnimations")
  fun setDisablePageAnimations(view: RCTTabView, flag: Boolean) {
  }

  @ReactProp(name = "hapticFeedbackEnabled")
  fun setHapticFeedbackEnabled(view: RCTTabView, value: Boolean) {
      tabViewImpl.setHapticFeedbackEnabled(view, value)
  }

  @ReactProp(name = "fontFamily")
  fun setFontFamily(view: RCTTabView?, value: String?) {
    view?.setFontFamily(value)
  }

  @ReactProp(name = "fontWeight")
  fun setFontWeight(view: RCTTabView?, value: String?) {
    view?.setFontWeight(value)
  }

  @ReactProp(name = "fontSize")
  fun setFontSize(view: RCTTabView?, value: Int) {
    view?.setFontSize(value)
  }
}
