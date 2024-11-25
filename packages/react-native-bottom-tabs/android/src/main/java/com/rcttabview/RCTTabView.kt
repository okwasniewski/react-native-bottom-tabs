package com.rcttabview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Choreographer
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import coil3.ImageLoader
import coil3.asDrawable
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.assets.ReactFontManager
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.views.imagehelper.ImageSource
import com.facebook.react.views.text.ReactTypefaceUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

class RCTTabView(context: Context) : LinearLayout(context) {
  private val contentContainer: FrameLayout
  private val bottomNavigationView: BottomNavigationView
  private val icons: MutableMap<Int, ImageSource> = mutableMapOf()
  private var isLayoutEnqueued = false
  var items: MutableList<TabInfo>? = null
  var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  var onTabLongPressedListener: ((WritableMap) -> Unit)? = null
  private var isAnimating = false
  private var activeTintColor: Int? = null
  private var inactiveTintColor: Int? = null
  private val checkedStateSet = intArrayOf(android.R.attr.state_checked)
  private val uncheckedStateSet = intArrayOf(-android.R.attr.state_checked)
  private var hapticFeedbackEnabled = true
  private var fontSize: Int? = null
  private var fontFamily: String? = null
  private var fontWeight: Int? = null

  private val imageLoader = ImageLoader.Builder(context)
    .components {
      add(SvgDecoder.Factory())
    }
    .build()

  init {
    this.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    this.orientation = VERTICAL
    // First create the views
    contentContainer = FrameLayout(context)
    bottomNavigationView = BottomNavigationView(context)

    contentContainer.layoutParams = LayoutParams(
      LayoutParams.MATCH_PARENT,
      0
    ).apply {
      weight = 1f
    }

    bottomNavigationView.layoutParams = LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.WRAP_CONTENT
    ).apply {
      gravity = android.view.Gravity.BOTTOM
    }

    // Add views in order
    addView(contentContainer)
    addView(bottomNavigationView)
    val size = Pair(contentContainer.width, contentContainer.height)
    Log.w("TABVIEW", size.toString())
  }

//  // Override addView to add children to the content container instead of the root layout
  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
    if (child is BottomNavigationView || child is FrameLayout) {
      super.addView(child, index, params)
    } else {
      contentContainer.addView(child, index, params)
    }
  }

  override fun addView(child: View?) {
    super.addView(child)
  }

  private fun getContentContainerSize(callback: (Pair<Int, Int>) -> Unit) {
    contentContainer.viewTreeObserver.addOnGlobalLayoutListener { // Remove the listener so it's only called once
      val size = Pair(contentContainer.width, contentContainer.height)
      callback(size)
    }
  }


//  override fun removeView(view: View?) {
//    if (view === bottomNavigationView || view === contentContainer) {
//      super.removeView(view)
//    } else {
//      contentContainer.removeView(view)
//    }
//  }

//  override fun removeViewAt(index: Int) {
//    if (index >= contentContainer.childCount) {
//      super.removeViewAt(index)
//    } else {
//      contentContainer.removeViewAt(index)
//    }
//  }
//
//  override fun removeAllViews() {
//    contentContainer.removeAllViews()
//  }
//
//  override fun getChildCount(): Int {
//    return contentContainer.childCount
//  }
//
//  override fun getChildAt(index: Int): View {
//    return contentContainer.getChildAt(index)
//  }

  private val layoutCallback = Choreographer.FrameCallback {
    isLayoutEnqueued = false
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    layout(left, top, right, bottom)
  }

  private fun onTabLongPressed(item: MenuItem) {
    val longPressedItem = items?.firstOrNull { it.title == item.title }
    longPressedItem?.let {
      val event = Arguments.createMap().apply {
        putString("key", longPressedItem.key)
      }
      onTabLongPressedListener?.invoke(event)
      emitHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
  }

  override fun requestLayout() {
    super.requestLayout()
    if (contentContainer != null){
      val size = Pair(contentContainer.width, contentContainer.height)
      Log.w("TABVIEW", "CONTAINER:$size")
      val size2 = Pair(this.width, this.height)
      Log.w("TABVIEW", "NavIgationView:$size2")
    }

    @Suppress("SENSELESS_COMPARISON")
    if (!isLayoutEnqueued && layoutCallback != null) {
      isLayoutEnqueued = true
      ReactChoreographer
        .getInstance()
        .postFrameCallback(
          ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE,
          layoutCallback,
        )
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    isAnimating = false
  }

  private fun onTabSelected(item: MenuItem) {
    if (isLayoutEnqueued) {
      return
    }
    val selectedItem = items?.first { it.title == item.title }
    selectedItem?.let {
      val event = Arguments.createMap().apply {
        putString("key", selectedItem.key)
      }
      onTabSelectedListener?.invoke(event)
      emitHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }
  }

  fun setSelectedItemId(itemId: Int) {
    bottomNavigationView.selectedItemId = itemId
    contentContainer.children.forEachIndexed { index, child ->
      if (index == itemId) {
        child.visibility = VISIBLE
      } else {
        child.visibility = INVISIBLE
      }
    }
  }

  fun updateItems(items: MutableList<TabInfo>) {
    this.items = items
    items.forEachIndexed { index, item ->
      val menuItem = getOrCreateItem(index, item.title)
      menuItem.isVisible = !item.hidden
      if (icons.containsKey(index)) {
        getDrawable(icons[index]!!) {
          menuItem.icon = it
        }
      }

      if (item.badge.isNotEmpty()) {
        val badge = bottomNavigationView.getOrCreateBadge(index)
        badge.isVisible = true
        badge.text = item.badge
      } else {
        bottomNavigationView.removeBadge(index)
      }
      post {
        bottomNavigationView.findViewById<View>(menuItem.itemId).setOnLongClickListener {
          onTabLongPressed(menuItem)
          true
        }
        bottomNavigationView.findViewById<View>(menuItem.itemId).setOnClickListener {
          onTabSelected(menuItem)
          updateTintColors(menuItem)
        }
        updateTextAppearance()
      }
    }
  }

  private fun getOrCreateItem(index: Int, title: String): MenuItem {
    return bottomNavigationView.menu.findItem(index)
      ?: bottomNavigationView.menu.add(0, index, 0, title)
  }

  fun setIcons(icons: ReadableArray?) {
    if (icons == null || icons.size() == 0) {
      return
    }

    for (idx in 0 until icons.size()) {
      val source = icons.getMap(idx)
      val uri = source.getString("uri")
      if (uri.isNullOrEmpty()) {
        continue
      }
      val imageSource = ImageSource(context, uri)
      this.icons[idx] = imageSource

      bottomNavigationView.menu.findItem(idx)?.let { menuItem ->
        getDrawable(imageSource) {
          menuItem.icon = it
        }
      }
    }
  }

  fun setLabeled(labeled: Boolean?) {
    bottomNavigationView.labelVisibilityMode = if (labeled == false) {
      BottomNavigationView.LABEL_VISIBILITY_UNLABELED
    } else if (labeled == true) {
      BottomNavigationView.LABEL_VISIBILITY_LABELED
    } else {
      BottomNavigationView.LABEL_VISIBILITY_AUTO
    }
  }

  fun setRippleColor(color: ColorStateList) {
    bottomNavigationView.itemRippleColor = color
  }

  @SuppressLint("CheckResult")
  private fun getDrawable(imageSource: ImageSource, onDrawableReady: (Drawable?) -> Unit) {
    val request = ImageRequest.Builder(context)
      .data(imageSource.uri)
      .target { drawable ->
        post { onDrawableReady(drawable.asDrawable(context.resources)) }
      }
      .listener(
        onError = { _, result ->
          Log.e("RCTTabView", "Error loading image: ${imageSource.uri}", result.throwable)
        }
      )
      .build()

    imageLoader.enqueue(request)
  }

  fun setBarTintColor(color: Int?) {
    val backgroundColor = color ?: getDefaultColorFor(android.R.attr.colorPrimary) ?: return
    val colorDrawable = ColorDrawable(backgroundColor)

    bottomNavigationView.itemBackground = colorDrawable
    bottomNavigationView.backgroundTintList = ColorStateList.valueOf(backgroundColor)
  }

  fun setActiveTintColor(color: Int?) {
    activeTintColor = color
    updateTintColors()
  }

  fun setInactiveTintColor(color: Int?) {
    inactiveTintColor = color
    updateTintColors()
  }

  fun setActiveIndicatorColor(color: ColorStateList) {
    bottomNavigationView.itemActiveIndicatorColor = color
  }

  fun setHapticFeedback(enabled: Boolean) {
    hapticFeedbackEnabled = enabled
  }

  fun setFontSize(size: Int) {
    fontSize = size
    updateTextAppearance()
  }

  fun setFontFamily(family: String?) {
    fontFamily = family
    updateTextAppearance()
  }

  fun setFontWeight(weight: String?) {
    val fontWeight = ReactTypefaceUtils.parseFontWeight(weight)
    this.fontWeight = fontWeight
    updateTextAppearance()
  }

  private fun getTypefaceStyle(weight: Int?) = when (weight) {
    700 -> Typeface.BOLD
    else -> Typeface.NORMAL
  }

  private fun updateTextAppearance() {
    if (fontSize != null || fontFamily != null || fontWeight != null) {
      val menuView = bottomNavigationView.getChildAt(0) as? ViewGroup ?: return
      val size = fontSize?.toFloat()?.takeIf { it > 0 } ?: 12f
      val typeface = ReactFontManager.getInstance().getTypeface(
        fontFamily ?: "",
        getTypefaceStyle(fontWeight),
        context.assets
      )

      for (i in 0 until menuView.childCount) {
        val item = menuView.getChildAt(i)
        val largeLabel =
          item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_large_label_view)
        val smallLabel =
          item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_small_label_view)

        listOf(largeLabel, smallLabel).forEach { label ->
          label?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
            setTypeface(typeface)
          }
        }
      }
    }
  }

  private fun emitHapticFeedback(feedbackConstants: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hapticFeedbackEnabled) {
      bottomNavigationView.performHapticFeedback(feedbackConstants)
    }
  }

  private fun updateTintColors(item: MenuItem? = null) {
    val currentItemTintColor = items?.find { it.title == item?.title }?.activeTintColor
    val colorPrimary = currentItemTintColor
      ?: activeTintColor
      ?: getDefaultColorFor(android.R.attr.colorPrimary)
      ?: return
    val colorSecondary =
      inactiveTintColor ?: getDefaultColorFor(android.R.attr.textColorSecondary) ?: return
    val states = arrayOf(uncheckedStateSet, checkedStateSet)
    val colors = intArrayOf(colorSecondary, colorPrimary)

    ColorStateList(states, colors).apply {
      bottomNavigationView.itemTextColor = this
      bottomNavigationView.itemIconTintList = this
    }
  }

  private fun getDefaultColorFor(baseColorThemeAttr: Int): Int? {
    val value = TypedValue()
    if (!context.theme.resolveAttribute(baseColorThemeAttr, value, true)) {
      return null
    }
    val baseColor = AppCompatResources.getColorStateList(
      context, value.resourceId
    )
    return baseColor.defaultColor
  }
}
