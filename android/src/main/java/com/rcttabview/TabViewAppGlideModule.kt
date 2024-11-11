package com.rcttabview

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class TabViewAppGlideModule : AppGlideModule() {
  override fun applyOptions(context: Context, builder: GlideBuilder) {
    super.applyOptions(context, builder)

    builder.setLogLevel(
        Log.ERROR
    )
  }
}
