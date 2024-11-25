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
import androidx.appcompat.content.res.AppCompatResources
import androidx.drawerlayout.widget.DrawerLayout
import coil3.ImageLoader
import coil3.asDrawable
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.views.imagehelper.ImageSource
import com.facebook.react.views.text.ReactTypefaceUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

class RCTTabView(context: Context) : DrawerLayout(context) {
  private var isLayoutEnqueued = false
  private val adaptiveNavigation: AdaptiveNavigation = AdaptiveNavigation(context)
  private val icons: MutableMap<Int, ImageSource> = mutableMapOf()
  var items: MutableList<TabInfo>? = null
  var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  var onTabLongPressedListener: ((WritableMap) -> Unit)? = null
  var onNativeLayoutListener: ((width: Double, height: Double) -> Unit)? = null
  private var activeTintColor: Int? = null
  private var inactiveTintColor: Int? = null
  private val checkedStateSet = intArrayOf(android.R.attr.state_checked)
  private val uncheckedStateSet = intArrayOf(-android.R.attr.state_checked)
  private var hapticFeedbackEnabled = true
  private var fontSize: Int? = null
  private var fontFamily: String? = null
  private var fontWeight: Int? = null

  override fun requestLayout() {
    super.requestLayout()
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

  private val layoutCallback = Choreographer.FrameCallback {
    isLayoutEnqueued = false
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    layout(left, top, right, bottom)
  }

  private val imageLoader = ImageLoader.Builder(context)
    .components {
      add(SvgDecoder.Factory())
    }
    .build()

  init {
    adaptiveNavigation.onNavigationItemSelected = { item ->
      onTabSelected(item)
      true
    }
    adaptiveNavigation.setContainerSizeListener(object : ContainerSizeListener {
      override fun onSizeChanged(width: Double, height: Double) {
        onNativeLayoutListener?.invoke(width, height)
        Log.d("ContainerSize", "Width: $width, Height: $height")
      }
    })
    addView(adaptiveNavigation)
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
    if (child is AdaptiveNavigation) {
      super.addView(child, index, params)
    } else {
      adaptiveNavigation.addView(child, index, params)
    }
  }

  override fun addView(child: View?) {
    super.addView(child)
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

  private fun onTabSelected(item: MenuItem) {
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
    adaptiveNavigation.setSelectedItemId(itemId)
  }

  fun updateItems(items: MutableList<TabInfo>) {
    this.items = items
    items.forEachIndexed { index, item ->
      val menuItem = adaptiveNavigation.getOrCreateItem(index, item.title)
      menuItem.isVisible = !item.hidden
      if (icons.containsKey(index)) {
        getDrawable(icons[index]!!) {
          menuItem.icon = it
        }
      }

      if (item.badge.isNotEmpty()) {
        val badge = adaptiveNavigation.getOrCreateBadge(index)
        badge?.isVisible = true
        badge?.text = item.badge
      } else {
        adaptiveNavigation.removeBadge(index)
      }
//      post {
////        bottomNavigationView.findViewById<View>(menuItem.itemId).setOnLongClickListener {
////          onTabLongPressed(menuItem)
////          true
////        }
////        bottomNavigationView.findViewById<View>(menuItem.itemId).setOnClickListener {
////          onTabSelected(menuItem)
////          updateTintColors(menuItem)
////        }
//        updateTextAppearance()
//      }
    }
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

      adaptiveNavigation.menu.findItem(idx)?.let { menuItem ->
        getDrawable(imageSource) {
          menuItem.icon = it
        }
      }
    }
  }

  fun setLabeled(labeled: Boolean?) {
    val visibility = when (labeled) {
        false -> {
          BottomNavigationView.LABEL_VISIBILITY_UNLABELED
        }
        true -> {
          BottomNavigationView.LABEL_VISIBILITY_LABELED
        }
        else -> {
          BottomNavigationView.LABEL_VISIBILITY_AUTO
        }
    }
    adaptiveNavigation.setLabelVisibilityMode(visibility)
  }

  fun setRippleColor(color: ColorStateList) {
    adaptiveNavigation.setItemRippleColor(color)
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
//    val backgroundColor = color ?: getDefaultColorFor(android.R.attr.colorPrimary) ?: return
//    val colorDrawable = ColorDrawable(backgroundColor)

//    bottomNavigationView.itemBackground = colorDrawable
//    bottomNavigationView.backgroundTintList = ColorStateList.valueOf(backgroundColor)
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
//    bottomNavigationView.itemActiveIndicatorColor = color
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
//    if (fontSize != null || fontFamily != null || fontWeight != null) {
//      val menuView = bottomNavigationView.getChildAt(0) as? ViewGroup ?: return
//      val size = fontSize?.toFloat()?.takeIf { it > 0 } ?: 12f
//      val typeface = ReactFontManager.getInstance().getTypeface(
//        fontFamily ?: "",
//        getTypefaceStyle(fontWeight),
//        context.assets
//      )
//
//      for (i in 0 until menuView.childCount) {
//        val item = menuView.getChildAt(i)
//        val largeLabel =
//          item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_large_label_view)
//        val smallLabel =
//          item.findViewById<TextView>(com.google.android.material.R.id.navigation_bar_item_small_label_view)
//
//        listOf(largeLabel, smallLabel).forEach { label ->
//          label?.apply {
//            setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
//            setTypeface(typeface)
//          }
//        }
//      }
//    }
  }

  private fun emitHapticFeedback(feedbackConstants: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hapticFeedbackEnabled) {
      adaptiveNavigation.performHapticFeedback(feedbackConstants)
    }
  }

  private fun updateTintColors(item: MenuItem? = null) {
//    val currentItemTintColor = items?.find { it.title == item?.title }?.activeTintColor
//    val colorPrimary = currentItemTintColor
//      ?: activeTintColor
//      ?: getDefaultColorFor(android.R.attr.colorPrimary)
//      ?: return
//    val colorSecondary =
//      inactiveTintColor ?: getDefaultColorFor(android.R.attr.textColorSecondary) ?: return
//    val states = arrayOf(uncheckedStateSet, checkedStateSet)
//    val colors = intArrayOf(colorSecondary, colorPrimary)
//
//    ColorStateList(states, colors).apply {
//      bottomNavigationView.itemTextColor = this
//      bottomNavigationView.itemIconTintList = this
//    }
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
