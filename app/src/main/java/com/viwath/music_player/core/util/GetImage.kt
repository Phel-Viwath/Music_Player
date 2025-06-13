package com.viwath.music_player.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File

object GetImage {
    fun String.getImageBitMap(): Bitmap? {
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

    fun String.getImagePath(context: Context): String?{
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this)
            retriever.embeddedPicture?.let {
                val file = File(context.cacheDir, "image_${hashCode()}.jpg")
                file.writeBytes(it)
                file.absolutePath
            }
        }catch (e: Exception){
            Log.d("GetImage", "getImagePath: ${e.message}")
            null
        }finally {
            retriever.release()
        }
    }
}