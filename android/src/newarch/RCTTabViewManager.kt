package com.rcttabview

import android.content.Context
import android.view.View
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.PixelUtil.toDIPFromPixel
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerInterface
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput


@ReactModule(name = RCTTabViewImpl.NAME)
class RCTTabViewManager(context: ReactApplicationContext) :
  SimpleViewManager<ReactBottomNavigationView>(),
  RNCTabViewManagerInterface<ReactBottomNavigationView> {

  private val contextInner: ReactApplicationContext = context
  private val delegate: RNCTabViewManagerDelegate<ReactBottomNavigationView, RCTTabViewManager> =
    RNCTabViewManagerDelegate(this)
  private val tabViewImpl: RCTTabViewImpl = RCTTabViewImpl()

  override fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    val view = ReactBottomNavigationView(context)
    val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
    view.onTabSelectedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher?.dispatchEvent(PageSelectedEvent(viewTag = view.id, key = it))
      }
    }

    view.onTabLongPressedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher?.dispatchEvent(TabLongPressEvent(viewTag = view.id, key = it))
      }
    }
    return view

  }

  override fun getName(): String {
    return tabViewImpl.getName()
  }

  override fun setItems(view: ReactBottomNavigationView?, value: ReadableArray?) {
    if (view != null && value != null)
      tabViewImpl.setItems(view, value)
  }

  override fun setSelectedPage(view: ReactBottomNavigationView?, value: String?) {
    if (view != null && value != null)
      tabViewImpl.setSelectedPage(view, value)
  }

  override fun setIcons(view: ReactBottomNavigationView?, value: ReadableArray?) {
    if (view != null)
      tabViewImpl.setIcons(view, value)
  }

  override fun setLabeled(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null)
      tabViewImpl.setLabeled(view, value)
  }

  override fun setRippleColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setRippleColor(view, value)
  }

  override fun setBarTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setBarTintColor(view, value)
  }

  override fun setActiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setActiveTintColor(view, value)
  }

  override fun setInactiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setInactiveTintColor(view, value)
  }

  override fun setActiveIndicatorColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setActiveIndicatorColor(view, value)
  }

  override fun getDelegate(): ViewManagerDelegate<ReactBottomNavigationView> {
    return delegate
  }

  override fun setHapticFeedbackEnabled(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null)
      tabViewImpl.setHapticFeedbackEnabled(view, value)
  }

  public override fun measure(
    context: Context?,
    localData: ReadableMap?,
    props: ReadableMap?,
    state: ReadableMap?,
    width: Float,
    widthMode: YogaMeasureMode?,
    height: Float,
    heightMode: YogaMeasureMode?,
    attachmentsPositions: FloatArray?
  ): Long {
    val view = ReactBottomNavigationView(context ?: contextInner)
    val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    view.measure(measureSpec, measureSpec)

    val bottomInset = RCTTabViewImpl.getNavigationBarInset(contextInner)

    return YogaMeasureOutput.make(
      toDIPFromPixel(view.measuredWidth.toFloat()),
      toDIPFromPixel(view.measuredHeight.toFloat() + bottomInset)
    )
  }

  // iOS Methods

  override fun setTranslucent(view: ReactBottomNavigationView?, value: Boolean) {
  }

  override fun setIgnoresTopSafeArea(view: ReactBottomNavigationView?, value: Boolean) {
  }

  override fun setDisablePageAnimations(view: ReactBottomNavigationView?, value: Boolean) {
  }

  override fun setSidebarAdaptable(view: ReactBottomNavigationView?, value: Boolean) {
  }

  override fun setScrollEdgeAppearance(view: ReactBottomNavigationView?, value: String?) {
  }
}
