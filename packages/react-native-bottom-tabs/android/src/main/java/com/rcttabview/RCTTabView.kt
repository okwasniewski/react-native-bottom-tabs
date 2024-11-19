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
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.ImageLoader
import coil3.asDrawable
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.views.imagehelper.ImageSource
import com.facebook.react.views.text.ReactTypefaceUtils
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder

data class TabViewProps(
//  var children: MutableMap<Int, View> = mutableMapOf(),
  var items: MutableList<TabInfo>? = null,
  var children: List<View> = emptyList(),
  var selectedItem: String? = null
)

class ReactBottomNavigationView(context: Context) : FrameLayout(context) {
  private var props = mutableStateOf(TabViewProps())
  private val icons: MutableMap<Int, ImageSource> = mutableMapOf()
  var items: MutableList<TabInfo>? = null
  var onTabSelectedListener: ((key: String) -> Unit)? = null
  var onTabLongPressedListener: ((key: String) -> Unit)? = null
  var onLayoutListener: ((width: Double, height: Double) -> Unit)? = null
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
    val composeView = ComposeView(context)
    composeView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
    composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    composeView.setContent {
      MaterialTheme {
        ColumnComposable(props = props.value, onClick = { key ->
          onTabSelectedListener?.invoke(key)
          emitHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }) { width, height ->
          onLayoutListener?.invoke(width, height)
        }
      }
    }
    addView(composeView)
  }

  override fun addView(child: View?, index: Int) {
    if (child is ComposeView) {
      super.addView(child, index)
    } else {
      if (child != null) {
        props.value = props.value.copy(children = props.value.children + child)
      }
    }
  }

  private fun onTabLongPressed(item: MenuItem) {
    val longPressedItem = items?.firstOrNull { it.title == item.title }
    longPressedItem?.let {
      onTabLongPressedListener?.invoke(longPressedItem.key)
      emitHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
  }

  private fun onTabSelected(item: MenuItem) {
    val selectedItem = items?.first { it.title == item.title }
    selectedItem?.let {
      onTabSelectedListener?.invoke(selectedItem.key)
      emitHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }
  }

  fun updateItems(items: MutableList<TabInfo>) {
    props.value = props.value.copy(items = items)
  }

  fun setSelectedPage(key: String) {
    props.value = props.value.copy(selectedItem = key)
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
//      menu.findItem(idx)?.let { menuItem ->
//        getDrawable(imageSource)  {
//          menuItem.icon = it
//        }
//      }
    }
  }

  fun setLabeled(labeled: Boolean?) {
  }

  fun setRippleColor(color: ColorStateList) {
//    itemRippleColor = color
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

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    isAnimating = false
  }

  fun setBarTintColor(color: Int?) {
    // Set the color, either using the active background color or a default color.
    val backgroundColor = color ?: getDefaultColorFor(android.R.attr.colorPrimary) ?: return

    // Apply the same color to both active and inactive states
    val colorDrawable = ColorDrawable(backgroundColor)

//    itemBackground = colorDrawable
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
//    itemActiveIndicatorColor = color
  }

  fun setHapticFeedback(enabled: Boolean) {
    hapticFeedbackEnabled = enabled
  }

  fun setFontSize(size: Int) {
    fontSize = size
//    updateTextAppearance()
  }

  fun setFontFamily(family: String?) {
    fontFamily = family
//    updateTextAppearance()
  }

 fun setFontWeight(weight: String?) {
   val fontWeight = ReactTypefaceUtils.parseFontWeight(weight)
   this.fontWeight = fontWeight
//   updateTextAppearance()
  }

  private fun getTypefaceStyle(weight: Int?) = when (weight) {
    700 -> Typeface.BOLD
    else -> Typeface.NORMAL
  }

//  private fun updateTextAppearance() {
//    if (fontSize != null || fontFamily != null || fontWeight != null) {
//      val menuView = getChildAt(0) as? ViewGroup ?: return
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
//  }

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

//    ColorStateList(states, colors).apply {
//      this@ReactBottomNavigationView.itemTextColor = this
//      this@ReactBottomNavigationView.itemIconTintList = this
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

@Composable
fun ColumnComposable(props: TabViewProps, onClick: (key: String) -> Unit, onLayout: (width: Double, height: Double) -> Unit
) {
  val context = LocalContext.current
  val windowWidthClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
  val previousSize = remember { mutableStateOf<Pair<Double, Double>?>(null) }

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      props.items?.forEach {
          item(
            icon = {
              Icon(
                Icons.Default.Home,
                contentDescription = ""
              )
            },
            label = { Text(it.title) },
            selected = it.key == props.selectedItem,
            onClick = {
              onClick(it.key)
            },
            alwaysShowLabel = false
          )
      }
    },
    layoutType = if(windowWidthClass == WindowWidthSizeClass.EXPANDED) {
      NavigationSuiteType.NavigationDrawer
    } else {
      NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
        currentWindowAdaptiveInfo()
      )
    }
  ) {
      val selectedIndex = props.items?.indexOfFirst { it.key == props.selectedItem } ?: 0
      AndroidView(
        modifier = Modifier.fillMaxSize()
          .onGloballyPositioned { coordinates ->
            val size = coordinates.size
            val dpWidth = (size.width / context.resources.displayMetrics.density).toDouble()
            val dpHeight = (size.height / context.resources.displayMetrics.density).toDouble()
            val newSize = Pair(dpWidth, dpHeight)
            if (previousSize.value != newSize) {
              previousSize.value = newSize
              onLayout(dpWidth, dpHeight)
            }
          },
        factory = { context ->
          FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
              MATCH_PARENT,
              MATCH_PARENT
            )
          }
        },
        update = { container ->
          val currentView = if (container.childCount > 0) container.getChildAt(0) else null
          val newView = props.children[selectedIndex]

          if (currentView != newView) {
            container.removeAllViews()
            if (newView.parent != null) {
              (newView.parent as? ViewGroup)?.removeView(newView)
            }

            container.addView(newView)
          }
        }
      )
    }
}
