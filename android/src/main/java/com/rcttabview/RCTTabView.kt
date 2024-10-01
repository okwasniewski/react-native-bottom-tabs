package com.rcttabview

import android.content.Context
import android.view.Choreographer
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
  private val ANIMATION_DURATION: Long = 300

  var items: MutableList<TabInfo>? = null
  var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  private var isAnimating = false
  private val frameCallback = Choreographer.FrameCallback {
    if (isAnimating) {
      measureAndLayout()
    }
  }

  init {
    setOnItemSelectedListener { item ->
      onTabSelected(item)
      true
    }
  }

  override fun requestLayout() {
    super.requestLayout()
    // Manually trigger measure & layout, as RN on Android skips those.
    // See this issue: https://github.com/facebook/react-native/issues/17968#issuecomment-721958427
    this.post {
      measureAndLayout()
    }
  }

  private fun onTabSelected(item: MenuItem) {
    val selectedItem = items?.first { it.title == item.title }
    if (selectedItem == null) {
      return
    }
    
    startAnimation()

    val event = Arguments.createMap().apply {
      putString("key", selectedItem.key)
    }
    onTabSelectedListener?.invoke(event)
  }

  // Refresh TabView children to fix issue with animations.
  // https://github.com/facebook/react-native/issues/17968#issuecomment-697136929
  private fun startAnimation() {
    if (labelVisibilityMode != LABEL_VISIBILITY_AUTO) {
      return
    }
    isAnimating = true
    Choreographer.getInstance().postFrameCallback(frameCallback)
    postDelayed({
      isAnimating = false
    }, ANIMATION_DURATION)
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
  }

  // Fixes issues with BottomNavigationView children layouting.
  private fun measureAndLayout() {
    viewTreeObserver.dispatchOnGlobalLayout();
      measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
      layout(left, top, right, bottom)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    isAnimating = false
  }
}
