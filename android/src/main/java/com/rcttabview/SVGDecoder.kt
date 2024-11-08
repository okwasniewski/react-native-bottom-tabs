package com.rcttabview

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

  @Throws(IOException::class)
  override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<SVG>? {
    return try {
      val svg: SVG = SVG.getFromInputStream(source)
      // Use document width and height if view box is not set.
      // Later, we will override the document width and height with the dimensions of the native view.
      if (svg.documentViewBox == null) {
        val documentWidth = svg.documentWidth
        val documentHeight = svg.documentHeight
        if (documentWidth != -1f && documentHeight != -1f) {
          svg.setDocumentViewBox(0f, 0f, documentWidth, documentHeight)
        }
      }
      svg.documentWidth = width.toFloat()
      svg.documentHeight = height.toFloat()
      SimpleResource(svg)
    } catch (ex: SVGParseException) {
      throw IOException("Cannot load SVG from stream", ex)
    }
  }
}
