package com.rcttabview

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.TabViewManagerDelegate
import com.facebook.react.viewmanagers.TabViewManagerInterface

@ReactModule(name = RCTTabViewManager.NAME)
class RCTTabViewManager(context: ReactApplicationContext) :
  SimpleViewManager<ReactBottomNavigationView>(),
  TabViewManagerInterface<ReactBottomNavigationView> {
    private val context: ReactApplicationContext = context
  private val delegate: TabViewManagerDelegate<ReactBottomNavigationView, RCTTabViewManager> =
    TabViewManagerDelegate(this)
  private val tabViewImpl: RCTTabViewImpl = RCTTabViewImpl()
  companion object {
    const val NAME = "RCTTabView"
  }
  override fun getDelegate(): ViewManagerDelegate<ReactBottomNavigationView>? {
    return delegate
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

}
