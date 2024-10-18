package com.rcttabview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.facebook.react.modules.core.ReactChoreographer
import com.facebook.react.views.imagehelper.ImageSource
import com.google.android.material.bottomnavigation.BottomNavigationView


class ReactBottomNavigationView(context: Context) : BottomNavigationView(context) {
  private val icons: MutableMap<Int, ImageSource> = mutableMapOf()
  private var isLayoutEnqueued = false
  var items: MutableList<TabInfo>? = null
  var onTabSelectedListener: ((WritableMap) -> Unit)? = null
  private var isAnimating = false

  private val layoutCallback = Choreographer.FrameCallback {
    isLayoutEnqueued = false
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
    )
    layout(left, top, right, bottom)
  }

  init {
    setOnItemSelectedListener { item ->
      onTabSelected(item)
      true
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
    }
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
      val imageSource =
        ImageSource(
          context,
          source.getString("uri")
        )
      this.icons[idx] = imageSource

      // Update existing item if exists.
      menu.findItem(idx)?.let { menuItem ->
        menuItem.icon = getDrawable(imageSource)
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

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    isAnimating = false
  }
}
