package com.rcttabview

import android.view.View.MeasureSpec
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.yoga.YogaMeasureFunction
import com.facebook.yoga.YogaMeasureMode
import com.facebook.yoga.YogaMeasureOutput
import com.facebook.yoga.YogaNode

class TabViewShadowNode(tabview: ReactBottomNavigationView?) : LayoutShadowNode(),
  YogaMeasureFunction {
  private var mWidth = 0
  private var mHeight = 0
  private var mMeasured = false
  private var mTabView = tabview
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
    val tempTabView = mTabView?: ReactBottomNavigationView(themedContext)
    val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    tempTabView.measure(spec, spec)
    this.mWidth = tempTabView.measuredWidth
    this.mHeight = tempTabView.measuredHeight
    this.mMeasured = true

    return YogaMeasureOutput.make(mWidth, mHeight)
  }
}
