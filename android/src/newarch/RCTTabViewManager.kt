package com.rcttabview

import android.content.Context
import android.util.Log
import android.view.View
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.common.mapbuffer.MapBuffer
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.PixelUtil.toDIPFromPixel
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerInterface
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput


@ReactModule(name = RCTTabViewManager.NAME)
class RCTTabViewManager(context: ReactApplicationContext) :
  SimpleViewManager<ReactBottomNavigationView>(),
  RNCTabViewManagerInterface<ReactBottomNavigationView> {
    private val contextInner: ReactApplicationContext = context
  private val delegate: RNCTabViewManagerDelegate<ReactBottomNavigationView, RCTTabViewManager> =
    RNCTabViewManagerDelegate(this)
  private val tabViewImpl: RCTTabViewImpl = RCTTabViewImpl()
  companion object {
    const val NAME = "RNCTabView"
  }
  override fun getDelegate(): ViewManagerDelegate<ReactBottomNavigationView>? {
    return delegate
  }

  override fun measure(
    context: Context?,
    localData: MapBuffer?,
    props: MapBuffer?,
    state: MapBuffer?,
    width: Float,
    widthMode: YogaMeasureMode?,
    height: Float,
    heightMode: YogaMeasureMode?,
    attachmentsPositions: FloatArray?
  ): Long {
    val view = ReactBottomNavigationView(context?:contextInner)
    val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    view.measure(measureSpec, measureSpec)
    Log.d("¸",view.getMeasuredWidth().toFloat().toString() )
    Log.d("bottomTabsH",view.getMeasuredHeight().toFloat().toString() )
    return YogaMeasureOutput.make(
      toDIPFromPixel(view.getMeasuredWidth().toFloat()),
      toDIPFromPixel(view.getMeasuredHeight().toFloat())
    )
  }

  override fun createViewInstance(p0: ThemedReactContext): ReactBottomNavigationView {
    return tabViewImpl.createViewInstance(p0)
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
    if (view != null && value != null)
      tabViewImpl.setIcons(view, value)
  }

  override fun setLabeled(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null && value != null)
      tabViewImpl.setLabeled(view, value)
  }

  override fun setSidebarAdaptable(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null && value != null)
      tabViewImpl.setSidebarAdaptable(view, value)
  }

  override fun setScrollEdgeAppearance(view: ReactBottomNavigationView?, value: String?) {
    if (view != null && value != null)
      tabViewImpl.setScrollEdgeAppearance(view, value)
  }

  override fun setRippleColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      tabViewImpl.setRippleColor(view, value)
  }

  override fun createShadowNodeInstance(context: ReactApplicationContext): LayoutShadowNode {
    return TabViewShadowNode(tabViewImpl.getViewInstance())
  }
  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
    )
  }

  override fun addEventEmitters(reactContext: ThemedReactContext, view: ReactBottomNavigationView) {
    super.addEventEmitters(reactContext, view)
  }

  override fun setBarTintColor(view: ReactBottomNavigationView?, value: Int?) {

  }

  override fun setTranslucent(view: ReactBottomNavigationView?, value: Boolean) {
  }

  override fun setActiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    TODO("Not yet implemented")
  }

  override fun setInactiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    TODO("Not yet implemented")
  }

  override fun setIgnoresTopSafeArea(view: ReactBottomNavigationView?, value: Boolean) {
    TODO("Not yet implemented")
  }

  override fun setDisablePageAnimations(view: ReactBottomNavigationView?, value: Boolean) {
    TODO("Not yet implemented")
  }

}
