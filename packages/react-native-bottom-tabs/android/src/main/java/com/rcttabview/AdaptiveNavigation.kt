package com.rcttabview

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ViewFlipper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import com.facebook.react.modules.core.ReactChoreographer
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationBarView.LabelVisibility
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView

enum class Layout {
  BOTTOM_NAV, NAVIGATION_RAIL, DRAWER
}

interface ContainerSizeListener {
  fun onSizeChanged(width: Double, height: Double)
}

class AdaptiveNavigation @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {
  private val navigationView: NavigationView = NavigationView(context).apply {
    id = generateViewId()
    visibility = GONE
    layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
  }
  private val navigationRail: NavigationRailView = NavigationRailView(context).apply {
    id = generateViewId()
    visibility = GONE
    layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
  }
  private val bottomNav: BottomNavigationView = BottomNavigationView(context).apply {
    id = generateViewId()
    visibility = GONE
    layoutParams = ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
      bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
      startToStart = ConstraintLayout.LayoutParams.PARENT_ID
      endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
    }
  }
  private val container: FrameLayout
  private val navContainer: FrameLayout = FrameLayout(context).apply {
    id = generateViewId()
    layoutParams = ConstraintLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
      startToStart = ConstraintLayout.LayoutParams.PARENT_ID
      topToTop = ConstraintLayout.LayoutParams.PARENT_ID
      bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
    }
  }
  private val viewFlipper: ViewFlipper

  private val rootLayout: ConstraintLayout = ConstraintLayout(context).apply {
    id = generateViewId()
    layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
  }

  private var currentLayout: Layout = Layout.BOTTOM_NAV

  var onNavigationItemSelected: ((MenuItem) -> Boolean)? = null
  private var containerSizeListener: ContainerSizeListener? = null


  init {

    viewFlipper = ViewFlipper(context).apply {
      id = generateViewId()
      layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
        startToEnd = navContainer.id
        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        bottomToTop = bottomNav.id
      }
      setInAnimation(context, android.R.anim.fade_in)
      setOutAnimation(context, android.R.anim.fade_out)
      addOnLayoutChangeListener { _, left, top, right, bottom,
                                  oldLeft, oldTop, oldRight, oldBottom ->
        val newWidth = right - left
        val newHeight = bottom - top
        val oldWidth = oldRight - oldLeft
        val oldHeight = oldBottom - oldTop


        if (newWidth != oldWidth || newHeight != oldHeight) {
          val dpWidth = (newWidth / context.resources.displayMetrics.density).toDouble()
          val dpHeight = (newHeight / context.resources.displayMetrics.density).toDouble()
          containerSizeListener?.onSizeChanged(dpWidth, dpHeight)
        }
      }

    }

    setupViews()
    setupNavigationListeners()
    updateNavigationViewVisibility()
  }

  fun setContainerSizeListener(listener: ContainerSizeListener) {
    containerSizeListener = listener
  }

  private fun setupViews() {
    navContainer.addView(navigationView)
    navContainer.addView(navigationRail)
    rootLayout.addView(navContainer)
    rootLayout.addView(bottomNav)
    rootLayout.addView(viewFlipper)
    addView(rootLayout)
  }

  private fun setupNavigationListeners() {
    val listener = NavigationBarView.OnItemSelectedListener { item ->
      onNavigationItemSelected?.invoke(item) ?: false
    }
    val listener2 = NavigationView.OnNavigationItemSelectedListener { item ->
      onNavigationItemSelected?.invoke(item) ?: false
    }

    navigationView.setNavigationItemSelectedListener(listener2)
    navigationRail.setOnItemSelectedListener(listener)
    bottomNav.setOnItemSelectedListener(listener)
  }

  fun setSelectedItemId(itemId: Int) {
    bottomNav.selectedItemId = itemId
    navigationRail.selectedItemId = itemId
    navigationView.setCheckedItem(itemId)
    viewFlipper.displayedChild = itemId
    refreshViewChildrenLayout(viewFlipper.currentView)
    viewFlipper.currentView.requestFocus()
  }

  val menu: Menu get() {
    return when (currentLayout) {
      Layout.BOTTOM_NAV -> bottomNav.menu
      Layout.NAVIGATION_RAIL -> navigationRail.menu
      Layout.DRAWER -> navigationView.menu
    }
  }

  fun setLabelVisibilityMode(labelVisibility: Int) {
    bottomNav.labelVisibilityMode = labelVisibility
    navigationRail.labelVisibilityMode = labelVisibility
  }

  fun setItemRippleColor(color: ColorStateList) {
    bottomNav.itemRippleColor = color
    navigationRail.itemRippleColor = color
  }

  private fun getMenuForLayout(layout: Layout): Menu {
    return when (layout) {
      Layout.BOTTOM_NAV -> bottomNav.menu
      Layout.NAVIGATION_RAIL -> navigationRail.menu
      Layout.DRAWER -> navigationView.menu
    }
  }

  fun getOrCreateBadge(menuItemId: Int): BadgeDrawable? {
    return when (currentLayout) {
      Layout.BOTTOM_NAV -> bottomNav.getOrCreateBadge(menuItemId)
      Layout.NAVIGATION_RAIL -> navigationRail.getOrCreateBadge(menuItemId)
      Layout.DRAWER -> null
    }
  }

  fun removeBadge(menuItemId: Int) {
    when (currentLayout) {
      Layout.BOTTOM_NAV -> bottomNav.removeBadge(menuItemId)
      Layout.NAVIGATION_RAIL -> navigationRail.removeBadge(menuItemId)
      Layout.DRAWER -> {}
    }
  }

  fun getOrCreateItem(
    itemId: Int,
    title: String,
    icon: Int? = null,
    order: Int = Menu.NONE
  ): MenuItem {
    menu.findItem(itemId)?.let {
      return it
    }

    return menu.add(Menu.NONE, itemId, order, title).apply {
      icon?.let { setIcon(it) }
    }
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
    if (child === rootLayout) {
      super.addView(child, index, params)
    } else {
      viewFlipper.addView(child, params)
    }
  }

  override fun removeView(view: View?) {
    if (view === rootLayout) {
      super.removeView(view)
    } else {
      viewFlipper.removeView(view)
    }
  }

  override fun removeViewAt(index: Int) {
    if (index == indexOfChild(rootLayout)) {
      super.removeViewAt(index)
    } else {
      viewFlipper.removeViewAt(index)
    }
  }

  override fun removeAllViews() {
    viewFlipper.removeAllViews()
  }

  private fun updateNavigationViewVisibility() {
    val displayMetrics = context.resources.displayMetrics
    val widthDp = displayMetrics.widthPixels / displayMetrics.density
    val oldLayout = currentLayout
    val oldMenu = getMenuForLayout(oldLayout)

    when {
      widthDp >= 1240 -> {
        currentLayout = Layout.DRAWER
        navigationView.visibility = VISIBLE
        navigationRail.visibility = GONE
        bottomNav.visibility = GONE
      }
      widthDp >= 600 -> {
        currentLayout = Layout.NAVIGATION_RAIL
        navigationView.visibility = GONE
        navigationRail.visibility = VISIBLE
        bottomNav.visibility = GONE
      }
      else -> {
        currentLayout = Layout.BOTTOM_NAV
        navigationView.visibility = GONE
        navigationRail.visibility = GONE
        bottomNav.visibility = VISIBLE
      }
    }

    menu.copyFrom(oldMenu)
    refreshViewChildrenLayout(this)
  }

  override fun onConfigurationChanged(newConfig: Configuration?) {
    super.onConfigurationChanged(newConfig)
    updateNavigationViewVisibility()
  }

  private fun refreshViewChildrenLayout(view: View) {
    view.post {
      view.measure(
        View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
      view.layout(view.left, view.top, view.right, view.bottom)
    }
  }
}

object MenuUtils {
  /**
   * Copies all items from source menu to destination menu
   */
  fun copyMenu(source: Menu, destination: Menu) {
    destination.clear() // Clear destination first
    source.forEach { sourceItem ->
      destination.add(
        sourceItem.groupId,
        sourceItem.itemId,
        sourceItem.order,
        sourceItem.title
      ).apply {
        icon = sourceItem.icon
        isCheckable = sourceItem.isCheckable
        isChecked = sourceItem.isChecked
        isEnabled = sourceItem.isEnabled
        isVisible = sourceItem.isVisible

        // Copy action view if exists
        sourceItem.actionView?.let { actionView ->
          setActionView(actionView)
        }
      }
    }
  }
}

// Extension functions for easier usage
fun Menu.copyFrom(source: Menu) {
  MenuUtils.copyMenu(source, this)
}
