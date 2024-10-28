package com.rcttabview

import android.view.View.MeasureSpec
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.yoga.YogaMeasureFunction
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput
import com.facebook.yoga.YogaNode
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.UIManagerModule

@ReactModule(name = RCTTabViewImpl.NAME)
class RCTTabViewManager(context: ReactApplicationContext) : SimpleViewManager<ReactBottomNavigationView>() {
  private lateinit var eventDispatcher: EventDispatcher
  private var tabViewImpl = RCTTabViewImpl()

  override fun getName(): String {
    return tabViewImpl.getName()
  }

  @ReactProp(name = "activeIndicatorColor", customType = "Color")
  fun setActiveIndicatorColor(view: ReactBottomNavigationView, color: Int?) {
    if (color != null) {
      view.setActiveIndicatorColor(color)
    }
  }

  public override fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    eventDispatcher = context.getNativeModule(UIManagerModule::class.java)!!.eventDispatcher
    val view = ReactBottomNavigationView(context)
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

  override fun createShadowNodeInstance(): LayoutShadowNode {
    return TabViewShadowNode()
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return tabViewImpl.getExportedCustomDirectEventTypeConstants()
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

  @ReactProp(name = "barTintColor", customType = "Color")
  fun setBarTintColor(view: ReactBottomNavigationView, color: Int?) {
    tabViewImpl.setBarTintColor(view, color)
  }

  @ReactProp(name = "rippleColor", customType = "Color")
  fun setRippleColor(view: ReactBottomNavigationView, rippleColor: Int?) {
    tabViewImpl.setRippleColor(view, rippleColor)
  }

  @ReactProp(name = "activeTintColor", customType = "Color")
  fun setActiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    tabViewImpl.setActiveTintColor(view, color)
  }

  @ReactProp(name = "inactiveTintColor", customType = "Color")
  fun setInactiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    tabViewImpl.setInactiveTintColor(view, color)
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

  class TabViewShadowNode() : LayoutShadowNode(),
    YogaMeasureFunction {
    private var mWidth = 0
    private var mHeight = 0
    private var mMeasured = false

    init {
      initMeasureFunction()
    }

    private fun initMeasureFunction() {
      setMeasureFunction(this)
    }

    override fun measure(
      node: YogaNode,
      width: Float,
      widthMode: YogaMeasureMode,
      height: Float,
      heightMode: YogaMeasureMode
    ): Long {
      if (mMeasured) {
        return YogaMeasureOutput.make(mWidth, mHeight)
      }

      val tabView = ReactBottomNavigationView(themedContext)
      val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
      tabView.measure(spec, spec)
      this.mWidth = tabView.measuredWidth
      this.mHeight = tabView.measuredHeight
      this.mMeasured = true

      return YogaMeasureOutput.make(mWidth, mHeight)
    }
  }
}
