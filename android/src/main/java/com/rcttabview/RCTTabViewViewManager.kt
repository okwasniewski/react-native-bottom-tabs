package com.rcttabview

import android.view.View.MeasureSpec
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.yoga.YogaMeasureFunction
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput
import com.facebook.yoga.YogaNode


data class TabInfo(
  val key: String,
  val icon: String,
  val title: String,
  val badge: String
)

@ReactModule(name = RCTTabViewViewManager.NAME)
class RCTTabViewViewManager :
  SimpleViewManager<ReactBottomNavigationView>() {
  private lateinit var eventDispatcher: EventDispatcher

  override fun getName(): String {
    return NAME
  }

  @ReactProp(name = "items")
  fun setItems(view: ReactBottomNavigationView, items: ReadableArray) {
    val itemsArray = mutableListOf<TabInfo>()
    for (i in 0 until items.size()) {
      items.getMap(i).let { item ->
          itemsArray.add(
            TabInfo(
              key = item.getString("key") ?: "",
              icon = item.getString("icon") ?: "",
              title = item.getString("title") ?: "",
              badge = item.getString("badge") ?: ""
            )
          )
      }
    }
    view.updateItems(itemsArray)
  }

  @ReactProp(name = "selectedPage")
  fun setSelectedPage(view: ReactBottomNavigationView, key: String) {
    view.items?.indexOfFirst { it.key == key }?.let {
      view.selectedItemId = it
    }
  }

  public override fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    eventDispatcher = context.getNativeModule(UIManagerModule::class.java)!!.eventDispatcher
    val view = ReactBottomNavigationView(context)
    view.onTabSelectedListener = { data ->
      data.getString("key")?.let {
        eventDispatcher.dispatchEvent(PageSelectedEvent(viewTag = view.id, key = it ))
      }
    }
    return view
  }


  class TabViewShadowNode() : LayoutShadowNode(),
    YogaMeasureFunction {
    private var mWidth = 0
    private var mHeight = 0
    private var mMeasured = false

    init {
      initMeasureFunction()
    }

    private fun initMeasureFunction() {
      setMeasureFunction(this)
    }

    override fun measure(
      node: YogaNode,
      width: Float,
      widthMode: YogaMeasureMode,
      height: Float,
      heightMode: YogaMeasureMode
    ): Long {
      if (mMeasured) {
        return YogaMeasureOutput.make(mWidth, mHeight)
      }

      val tabView = ReactBottomNavigationView(themedContext)
      val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
      tabView.measure(spec, spec)
      this.mWidth = tabView.measuredWidth
      this.mHeight = tabView.measuredHeight
      this.mMeasured = true

      return YogaMeasureOutput.make(mWidth, mHeight)
    }
  }

  override fun createShadowNodeInstance(): LayoutShadowNode {
    return TabViewShadowNode()
  }

  companion object {
    const val NAME = "RCTTabView"
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
    )
  }
}
