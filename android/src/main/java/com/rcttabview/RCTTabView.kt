package com.rcttabview

import android.content.Context
import android.view.Choreographer
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
  private var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  var items: MutableList<TabInfo>? = null

  init {
    // TODO: Refactor this outside of TabView (attach listener in ViewManager).
    setOnItemSelectedListener { item ->
      onTabSelected(item)
      true
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    refreshViewChildrenLayout(this)
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

    // Refresh TabView children to fix issue with animations.
    // https://github.com/facebook/react-native/issues/17968#issuecomment-697136929
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        refreshViewChildrenLayout(this@ReactBottomNavigationView)
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  fun setOnTabSelectedListener(listener: (WritableMap) -> Unit) {
    onTabSelectedListener = listener
  }

  fun updateItems(items: MutableList<TabInfo>) {
    this.items = items
    // TODO: This doesn't work with hot reload. It clears all menu items
    menu.clear()
    items.forEachIndexed {index, item ->
      val menuItem = menu.add(0, index, 0, item.title)
      val iconResourceId = resources.getIdentifier(
        item.icon, "drawable", context.packageName
      )
      if (iconResourceId != 0) {
        menuItem.icon = AppCompatResources.getDrawable(context, iconResourceId)
      } else {
        menuItem.setIcon(android.R.drawable.btn_star) // fallback icon
      }
      if (item.badge.isNotEmpty()) {
        val badge = this.getOrCreateBadge(index)
        badge.isVisible = true
        badge.text = item.badge
      } else {
        removeBadge(index)
      }
    }

    refreshViewChildrenLayout(this)
  }


  // Fixes issues with BottomNavigationView children layouting.
  private fun refreshViewChildrenLayout(view: View) {
    view.post {
      view.measure(
        View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
      view.layout(view.left, view.top, view.right, view.bottom)
    }
  }
}
