package com.rcttabview

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import com.caverock.androidsvg.SVG
import java.io.InputStream


@GlideModule
class SvgModule: LibraryGlideModule() {
  override fun registerComponents(
    context: Context, glide: Glide, registry: Registry
  ) {
    registry
      .register(
        SVG::class.java,
        Drawable::class.java, SVGDrawableTranscoder(context)
      )
      .append(InputStream::class.java, SVG::class.java, SVGDecoder())
  }
}
