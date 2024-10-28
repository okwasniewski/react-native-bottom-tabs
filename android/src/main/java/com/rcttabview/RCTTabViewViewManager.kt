package com.rcttabview

import android.content.res.ColorStateList
import android.graphics.Color
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


data class TabInfo(
  val key: String,
  val title: String,
  val badge: String,
  val activeTintColor: Int?
)

@ReactModule(name = RCTTabViewViewManager.NAME)
class RCTTabViewViewManager :
  SimpleViewManager<ReactBottomNavigationView>() {
  private lateinit var eventDispatcher: EventDispatcher

  override fun getName(): String {
    return NAME
  }

  @ReactProp(name = "items")
  fun setItems(view: ReactBottomNavigationView, items: ReadableArray) {
    val itemsArray = mutableListOf<TabInfo>()
    for (i in 0 until items.size()) {
      items.getMap(i).let { item ->
        itemsArray.add(
          TabInfo(
            key = item.getString("key") ?: "",
            title = item.getString("title") ?: "",
            badge = item.getString("badge") ?: "",
            activeTintColor = if (item.hasKey("activeTintColor")) item.getInt("activeTintColor") else null
          )
        )
      }
    }
    view.updateItems(itemsArray)
  }

  @ReactProp(name = "selectedPage")
  fun setSelectedPage(view: ReactBottomNavigationView, key: String) {
    view.items?.indexOfFirst { it.key == key }?.let {
      view.selectedItemId = it
    }
  }

  @ReactProp(name = "labeled")
  fun setLabeled(view: ReactBottomNavigationView, flag: Boolean?) {
    view.setLabeled(flag)
  }

  @ReactProp(name = "icons")
  fun setIcons(view: ReactBottomNavigationView, icons: ReadableArray?) {
    view.setIcons(icons)
  }

  @ReactProp(name = "barTintColor", customType = "Color")
  fun setBarTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setBarTintColor(color)
  }

  @ReactProp(name = "rippleColor", customType = "Color")
  fun setRippleColor(view: ReactBottomNavigationView, rippleColor: Int?) {
    if (rippleColor != null) {
      val color = ColorStateList.valueOf(rippleColor)
      view.setRippleColor(color)
    }
  }

  @ReactProp(name = "activeTintColor", customType = "Color")
  fun setActiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setActiveTintColor(color)
  }

  @ReactProp(name = "inactiveTintColor", customType = "Color")
  fun setInactiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setInactiveTintColor(color)
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

  override fun createShadowNodeInstance(): LayoutShadowNode {
    return TabViewShadowNode()
  }

  companion object {
    const val NAME = "RNCTabView"
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
      TabLongPressEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onTabLongPress")
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

  @ReactProp(name = "translucent")
  fun setTranslucentview(view: ReactBottomNavigationView, translucent: Boolean?) {
  }
}
