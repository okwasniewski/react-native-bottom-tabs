package com.rcttabview

import android.content.res.ColorStateList
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder

data class TabInfo(
  val key: String,
  val title: String,
  val badge: String,
  val activeTintColor: Int?,
  val hidden: Boolean,
)

class RCTTabViewImpl {
  fun getName(): String {
    return NAME
  }

  fun setItems(view: RCTTabView, items: ReadableArray) {
    val itemsArray = mutableListOf<TabInfo>()
    for (i in 0 until items.size()) {
      items.getMap(i).let { item ->
        itemsArray.add(
          TabInfo(
            key = item.getString("key") ?: "",
            title = item.getString("title") ?: "",
            badge = item.getString("badge") ?: "",
            activeTintColor = if (item.hasKey("activeTintColor")) item.getInt("activeTintColor") else null,
            hidden = if (item.hasKey("hidden")) item.getBoolean("hidden") else false
          )
        )
      }
    }
    view.updateItems(itemsArray)
  }

  fun setSelectedPage(view: RCTTabView, key: String) {
    view.items?.indexOfFirst { it.key == key }?.let {
      view.setSelectedItemId(it)
    }
  }

  fun setLabeled(view: RCTTabView, flag: Boolean?) {
    view.setLabeled(flag)
  }

  fun setIcons(view: RCTTabView, icons: ReadableArray?) {
    view.setIcons(icons)
  }

  fun setBarTintColor(view: RCTTabView, color: Int?) {
    view.setBarTintColor(color)
  }

  fun setRippleColor(view: RCTTabView, rippleColor: Int?) {
    if (rippleColor != null) {
      val color = ColorStateList.valueOf(rippleColor)
      view.setRippleColor(color)
    }
  }

  fun setActiveIndicatorColor(view: RCTTabView, color: Int?) {
    if (color != null) {
      val color = ColorStateList.valueOf(color)
      view.setActiveIndicatorColor(color)
    }
  }

  fun setActiveTintColor(view: RCTTabView, color: Int?) {
    view.setActiveTintColor(color)
  }

  fun setInactiveTintColor(view: RCTTabView, color: Int?) {
    view.setInactiveTintColor(color)
  }

  fun setHapticFeedbackEnabled(view: RCTTabView, enabled: Boolean) {
   view.setHapticFeedback(enabled)
  }

  fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
      TabLongPressEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onTabLongPress")
    )
  }

  companion object {
    const val NAME = "RNCTabView"
  }
}
