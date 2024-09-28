package com.swiftuitabview

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.EventDispatcher

data class TabInfo(
  val key: String,
  val icon: String,
  val title: String,
  val badge: String
)

@ReactModule(name = SwiftuiTabviewViewManager.NAME)
class SwiftuiTabviewViewManager :
  ViewGroupManager<ReactBottomNavigationView>() {
  private lateinit var eventDispatcher: EventDispatcher

  override fun getName(): String {
    return NAME
  }

  @ReactProp(name = "items")
  fun setItems(view: ReactBottomNavigationView, items: ReadableArray) {
    val itemsArray = mutableListOf<TabInfo>()
    for (i in 0 until items.size()) {
      items.getMap(i)?.let { item ->
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
    // TODO: BottomBar height is currently set to a constant, this may require Custom Shadow node to measure the view.
    // Sometimes the view behaves weird.
    val view = ReactBottomNavigationView(context)
    view.setOnTabSelectedListener { data ->
     data.getString("key")?.let {
        eventDispatcher.dispatchEvent(PageSelectedEvent(viewTag = view.id, key = it ))
      }
    }
    return view
  }

  companion object {
    const val NAME = "SwiftUITabViewView"
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      PageSelectedEvent.EVENT_NAME,
      MapBuilder.of("registrationName", "onPageSelected"),
    )
  }
}
