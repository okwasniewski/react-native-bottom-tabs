package com.rcttabview

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Choreographer
import android.view.MenuItem
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSources
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.views.imagehelper.ImageSource
import com.facebook.react.views.imagehelper.ImageSource.Companion.getTransparentBitmapImageSource
import com.google.android.material.bottomnavigation.BottomNavigationView


class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
  private val ANIMATION_DURATION: Long = 300
  private val icons: MutableMap<Int, ImageSource> = mutableMapOf()

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
    selectedItem?.let {
      val event = Arguments.createMap().apply {
        putString("key", selectedItem.key)
      }
      onTabSelectedListener?.invoke(event)
      startAnimation()
    }
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
    items.forEachIndexed {index, item ->
      val menuItem = getOrCreateItem(index, item.title)
      if (icons.containsKey(index)) {
        menuItem.icon = getDrawable(icons[index]!!)
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

  private fun getOrCreateItem(index: Int, title: String): MenuItem {
    return menu.findItem(index) ?: menu.add(0, index, 0, title)
  }

  fun setIcons(icons: ReadableArray?) {
    if (icons == null || icons.size() == 0) {
      return
    }

    for (idx in 0 until icons.size()) {
      val source = icons.getMap(idx)
      var imageSource =
        ImageSource(
          context,
          source.getString("uri")
        )
      if (Uri.EMPTY == imageSource.uri) {
        imageSource = getTransparentBitmapImageSource(context)
      }
      this.icons[idx] = imageSource

      // Update existing item if exists.
      menu.findItem(idx)?.let { menuItem ->
        menuItem.icon = getDrawable(imageSource)
      }
    }
  }

  private fun getDrawable(imageSource: ImageSource): Drawable {
    // TODO: Check if this can be done using some built-in React Native class
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(imageSource.uri).build()
    val dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, context)
    val result = DataSources.waitForFinalResult(dataSource) as CloseableReference<CloseableBitmap>
    val bitmap = result.get().underlyingBitmap

    CloseableReference.closeSafely(result)
    dataSource.close()

    return BitmapDrawable(resources, bitmap)
  }

  // Fixes issues with BottomNavigationView children layouting.
  private fun measureAndLayout() {
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
