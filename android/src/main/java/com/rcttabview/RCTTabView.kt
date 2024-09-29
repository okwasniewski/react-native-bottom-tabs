package com.rcttabview

import android.content.Context
import android.view.MenuItem
import android.view.ViewGroup
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
  private var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  var items: MutableList<TabInfo>? = null

  init {
    setOnItemSelectedListener { item ->
      onTabSelected(item)
      true
    }
  }

  private fun onTabSelected(item: MenuItem) {
    val selectedItem = items?.first { it.title == item.title }
    if (selectedItem == null) {
      return
    }

    val event = Arguments.createMap().apply {
      putString("key", selectedItem.key)
    }
    onTabSelectedListener?.invoke(event)
  }

  fun setOnTabSelectedListener(listener: (WritableMap) -> Unit) {
    onTabSelectedListener = listener
  }

  fun updateItems(items: MutableList<TabInfo>) {
    this.items = items
    menu.clear()
    items.forEachIndexed {index, item ->
      // TODO: Handle custom icons
      // TODO: Handle badges
      menu.add(0, index, 0, item.title).setIcon(android.R.drawable.btn_star)
    }
  }
}
