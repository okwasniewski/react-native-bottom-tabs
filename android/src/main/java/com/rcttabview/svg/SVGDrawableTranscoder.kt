package com.rcttabview.svg

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.caverock.androidsvg.SVG

class SVGDrawableTranscoder(val context: Context) : ResourceTranscoder<SVG?, Drawable> {
  override fun transcode(toTranscode: Resource<SVG?>, options: Options): Resource<Drawable> {
    val svg = toTranscode.get()
    val picture = svg.renderToPicture()
    val drawable = PictureDrawable(picture)

    val returnedBitmap = Bitmap.createBitmap(
      drawable.intrinsicWidth,
      drawable.intrinsicHeight,
      Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(returnedBitmap)
    canvas.drawPicture(drawable.picture)
    val bitMapDrawable = BitmapDrawable(context.resources, returnedBitmap)
    return SimpleResource(bitMapDrawable)
  }
}
