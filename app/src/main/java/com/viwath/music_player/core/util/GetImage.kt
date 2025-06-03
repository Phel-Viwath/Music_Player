package com.viwath.music_player.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log

object GetImage {
    fun String.getEmbeddedPicture(): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this)
            val art = retriever.embeddedPicture
            art?.let {
                BitmapFactory.decodeByteArray(art, 0, it.size)
            }
        } catch (e: Exception) {
            Log.d("GetImage", "getEmbeddedPicture: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }
}