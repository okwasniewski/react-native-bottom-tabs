package com.rcttabview

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder

data class TabInfo(
  val key: String,
  val title: String,
  val badge: String,
  val activeTintColor: Int?
)

class RCTTabViewImpl {
  fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "RNCTabView"
    fun getNavigationBarInset(context: ReactContext): Int {
      val window = context.currentActivity?.window
      val isSystemBarTransparent = window?.navigationBarColor == Color.TRANSPARENT

      if (!isSystemBarTransparent) {
        return 0
      }

      val windowInsets = ViewCompat.getRootWindowInsets(window?.decorView ?: return 0)
      return windowInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
    }
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

  fun setBarTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setBarTintColor(color)
  }

  fun setRippleColor(view: ReactBottomNavigationView, rippleColor: Int?) {
    if (rippleColor != null) {
      val color = ColorStateList.valueOf(rippleColor)
      view.setRippleColor(color)
    }
  }

  fun setActiveIndicatorColor(view: ReactBottomNavigationView, color: Int?) {
    if (color != null) {
      val color = ColorStateList.valueOf(color)
      view.setActiveIndicatorColor(color)
    }
  }

  fun setActiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setActiveTintColor(color)
  }

  fun setInactiveTintColor(view: ReactBottomNavigationView, color: Int?) {
    view.setInactiveTintColor(color)
  }



  fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
      TabLongPressEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onTabLongPress")
    )
  }
}
