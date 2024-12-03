package com.rcttabview

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper
import java.util.Locale

data class ImageSource(
  val context: Context,
  val uri: String? = null,
) {
  private fun isLocalResourceUri(uri: Uri?) = uri?.scheme?.startsWith("res") ?: false

  fun getUri(context: Context): Uri? {
    val uri = computeUri(context)

    if (isLocalResourceUri(uri)) {
      return Uri.parse(
        uri!!.toString().replace("res:/", "android.resource://" + context.packageName + "/")
      )
    }

    return uri
  }

  private fun computeUri(context: Context): Uri? {
    val stringUri = uri ?: return null
    return try {
      val uri: Uri = Uri.parse(stringUri)
      // Verify scheme is set, so that relative uri (used by static resources) are not handled.
      if (uri.scheme == null) {
        computeLocalUri(stringUri, context)
      } else {
        uri
      }
    } catch (e: Exception) {
      computeLocalUri(stringUri, context)
    }
  }

  private fun computeLocalUri(stringUri: String, context: Context): Uri? {
    return ResourceIdHelper.getResourceUri(context, stringUri)
  }
}

// Taken from https://github.com/expo/expo/blob/sdk-52/packages/expo-image/android/src/main/java/expo/modules/image/ResourceIdHelper.kt
object ResourceIdHelper {
  private val idMap = mutableMapOf<String, Int>()

  @SuppressLint("DiscouragedApi")
  private fun getResourceRawId(context: Context, name: String): Int {
    if (name.isEmpty()) {
      return -1
    }

    val normalizedName = name.lowercase(Locale.ROOT).replace("-", "_")
    synchronized(this) {
      val id = idMap[normalizedName]
      if (id != null) {
        return id
      }

      return context
        .resources
        .getIdentifier(normalizedName, "raw", context.packageName)
        .also {
          idMap[normalizedName] = it
        }
    }
  }

  fun getResourceUri(context: Context, name: String): Uri? {
    val drawableUri = ResourceDrawableIdHelper.instance.getResourceDrawableUri(context, name)
    if (drawableUri != Uri.EMPTY) {
      return drawableUri
    }

    val resId = getResourceRawId(context, name)
    return if (resId > 0) {
      Uri.Builder().scheme("res").path(resId.toString()).build()
    } else {
      null
    }
  }
}
