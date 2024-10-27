package com.rcttabview

import android.content.res.ColorStateList
import android.util.Log
import android.view.View.MeasureSpec
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
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

class RCTTabViewImpl {

  private lateinit var eventDispatcher: EventDispatcherWrapper
  private var tabView: ReactBottomNavigationView? = null
  fun getName(): String {
    return NAME
  }

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


  fun setSelectedPage(view: ReactBottomNavigationView, key: String) {
    view.items?.indexOfFirst { it.key == key }?.let {
      view.selectedItemId = it
    }
  }


  fun setLabeled(view: ReactBottomNavigationView, flag: Boolean?) {
    view.setLabeled(flag)
  }


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

  fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    val view = ReactBottomNavigationView(context)
    tabView = view
    eventDispatcher = EventDispatcherWrapper(context)
    view?.onTabSelectedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher.dispatchEvent(PageSelectedEvent(viewTag = view.id, key = it))
      }
    }

    view.onTabLongPressedListener = { data ->
      data.getString("key")?.let {
//        eventDispatcher.dispatchEvent(TabLongPressEvent(viewTag = view.id, key = it))
      }
    }

    return view
  }

  fun getViewInstance(): ReactBottomNavigationView? {
    return if(tabView!=null)
      tabView
    else
      null
  }

  companion object {
    const val NAME = "RNCTabView"
  }
  // iOS Props

  fun setSidebarAdaptable(view: ReactBottomNavigationView, flag: Boolean) {
  }

  fun setScrollEdgeAppearance(view: ReactBottomNavigationView, value: String) {

  }

  fun setIgnoresTopSafeArea(view: ReactBottomNavigationView, flag: Boolean) {
  }


  fun setDisablePageAnimations(view: ReactBottomNavigationView, flag: Boolean) {
  }

  @ReactProp(name = "translucent")
  fun setTranslucentview(view: ReactBottomNavigationView, translucent: Boolean?) {
  }
}
