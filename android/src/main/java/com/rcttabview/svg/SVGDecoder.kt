package com.rcttabview.svg

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream


class SVGDecoder : ResourceDecoder<InputStream, SVG> {
  override fun handles(source: InputStream, options: Options) = true

  companion object {
    const val DEFAULT_SIZE = 40f
  }

  @Throws(IOException::class)
  override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SVG>? {
    return try {
      val svg: SVG = SVG.getFromInputStream(source)
      // Taken from https://github.com/expo/expo/blob/215d8a13a7ef3f0b36b14eead41291e2d2d6cd0c/packages/expo-image/android/src/main/java/expo/modules/image/svg/SVGDecoder.kt#L28
      if (svg.documentViewBox == null) {
        val documentWidth = svg.documentWidth
        val documentHeight = svg.documentHeight
        if (documentWidth != -1f && documentHeight != -1f) {
          svg.setDocumentViewBox(0f, 0f, documentWidth, documentHeight)
        }
      }

      svg.documentWidth = DEFAULT_SIZE
      svg.documentHeight = DEFAULT_SIZE
      SimpleResource(svg)
    } catch (ex: SVGParseException) {
      throw IOException("Cannot load SVG from stream", ex)
    }
  }
}
