package com.rcttabview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Choreographer
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
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


class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
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
  private var hapticFeedbackEnabled = false
  private var fontSize: Int? = null
  private var fontFamily: String? = null
  private var fontWeight: Int? = null

  private val imageLoader = ImageLoader.Builder(context)
    .components {
      add(SvgDecoder.Factory())
    }
    .build()

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
    @Suppress("SENSELESS_COMPARISON") // layoutCallback can be null here since this method can be called in init

    if (!isLayoutEnqueued && layoutCallback != null) {
      isLayoutEnqueued = true
      // we use NATIVE_ANIMATED_MODULE choreographer queue because it allows us to catch the current
      // looper loop instead of enqueueing the update in the next loop causing a one frame delay.
      ReactChoreographer
        .getInstance()
        .postFrameCallback(
          ReactChoreographer.CallbackType.NATIVE_ANIMATED_MODULE,
          layoutCallback,
        )
    }
  }

  private fun onTabSelected(item: MenuItem) {
    if (isLayoutEnqueued) {
      return;
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

  fun updateItems(items: MutableList<TabInfo>) {
    this.items = items
    items.forEachIndexed { index, item ->
      val menuItem = getOrCreateItem(index, item.title)
      menuItem.isVisible = !item.hidden
      if (icons.containsKey(index)) {
        getDrawable(icons[index]!!)  {
          menuItem.icon = it
        }
      }

      if (item.badge.isNotEmpty()) {
        val badge = this.getOrCreateBadge(index)
        badge.isVisible = true
        badge.text = item.badge
      } else {
        removeBadge(index)
      }
      post {
        findViewById<View>(menuItem.itemId).setOnLongClickListener {
          onTabLongPressed(menuItem)
          true
        }
        findViewById<View>(menuItem.itemId).setOnClickListener {
          onTabSelected(menuItem)
          updateTintColors(menuItem)
        }
        updateTextAppearance()
      }
    }
  }

  private fun getOrCreateItem(index: Int, title: String): MenuItem {
    return menu.findItem(index) ?: menu.add(0, index, 0, title)
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
      val imageSource =
      ImageSource(
        context,
        uri
      )
      this.icons[idx] = imageSource

      // Update existing item if exists.
      menu.findItem(idx)?.let { menuItem ->
        getDrawable(imageSource)  {
          menuItem.icon = it
        }
      }
    }
  }

  fun setLabeled(labeled: Boolean?) {
    labelVisibilityMode = if (labeled == false) {
      LABEL_VISIBILITY_UNLABELED
    } else if (labeled == true) {
      LABEL_VISIBILITY_LABELED
    } else {
      LABEL_VISIBILITY_AUTO
    }
  }

  fun setRippleColor(color: ColorStateList) {
    itemRippleColor = color
  }

  private fun formatUri(uri: Uri): Uri {
    return when (uri.scheme) {
      "res" -> {
        val uriString = uri.toString()
        val parts = uriString.split(":/")
        
        if (parts.size > 1) {
          val resourceId = parts[1].toIntOrNull()
          Uri.parse("android.resource://${context.packageName}/${resourceId}")
        } else {
          uri
        }
      }
      else -> uri
    }
  }

  @SuppressLint("CheckResult")
  private fun getDrawable(imageSource: ImageSource, onDrawableReady: (Drawable?) -> Unit) {
    val request = ImageRequest.Builder(context)
      .data(formatUri(imageSource.uri))
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

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    isAnimating = false
  }

  fun setBarTintColor(color: Int?) {
    // Set the color, either using the active background color or a default color.
    val backgroundColor = color ?: getDefaultColorFor(android.R.attr.colorPrimary) ?: return

    // Apply the same color to both active and inactive states
    val colorDrawable = ColorDrawable(backgroundColor)

    itemBackground = colorDrawable
    backgroundTintList = ColorStateList.valueOf(backgroundColor)
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
    itemActiveIndicatorColor = color
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
      val menuView = getChildAt(0) as? ViewGroup ?: return
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
      this.performHapticFeedback(feedbackConstants)
    }
  }

  private fun updateTintColors(item: MenuItem? = null) {
    // First let's check current item color.
    val currentItemTintColor = items?.find { it.title == item?.title }?.activeTintColor

    // getDeaultColor will always return a valid color but to satisfy the compiler we need to check for null
    val colorPrimary = currentItemTintColor ?: activeTintColor ?: getDefaultColorFor(android.R.attr.colorPrimary) ?: return
    val colorSecondary =
      inactiveTintColor ?: getDefaultColorFor(android.R.attr.textColorSecondary) ?: return
    val states = arrayOf(uncheckedStateSet, checkedStateSet)
    val colors = intArrayOf(colorSecondary, colorPrimary)

    ColorStateList(states, colors).apply {
      this@ReactBottomNavigationView.itemTextColor = this
      this@ReactBottomNavigationView.itemIconTintList = this
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
